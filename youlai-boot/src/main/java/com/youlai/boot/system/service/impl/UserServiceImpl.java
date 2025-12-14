package com.youlai.boot.system.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youlai.boot.common.constant.RedisConstants;
import com.youlai.boot.common.constant.SystemConstants;
import com.youlai.boot.core.exception.BusinessException;
import com.youlai.boot.common.model.Option;
import com.youlai.boot.platform.sms.enums.SmsTypeEnum;
import com.youlai.boot.platform.sms.service.SmsService;
import com.youlai.boot.security.model.UserAuthCredentials;
import com.youlai.boot.security.service.PermissionService;
import com.youlai.boot.security.token.TokenManager;
import com.youlai.boot.security.util.SecurityUtils;
import com.youlai.boot.platform.mail.service.MailService;
import com.youlai.boot.system.converter.UserConverter;
import com.youlai.boot.system.enums.DictCodeEnum;
import com.youlai.boot.system.mapper.UserMapper;
import com.youlai.boot.system.model.bo.UserBO;
import com.youlai.boot.system.model.dto.CurrentUserDTO;
import com.youlai.boot.system.model.dto.UserExportDTO;
import com.youlai.boot.system.model.entity.DictItem;
import com.youlai.boot.system.model.entity.User;
import com.youlai.boot.system.model.entity.UserRole;
import com.youlai.boot.system.model.form.*;
import com.youlai.boot.system.model.query.UserPageQuery;
import com.youlai.boot.system.model.vo.UserPageVO;
import com.youlai.boot.system.model.vo.UserProfileVO;
import com.youlai.boot.system.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 사용자비즈니스구현类
 *
 * @author Ray.Hao
 * @since 2022/1/14
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final PasswordEncoder passwordEncoder;

    private final UserRoleService userRoleService;

    private final RoleService roleService;

    private final PermissionService permissionService;

    private final SmsService smsService;

    private final MailService mailService;

    private final StringRedisTemplate redisTemplate;

    private final TokenManager tokenManager;

    private final DictItemService dictItemService;

    private final UserConverter userConverter;

    /**
     * 조회사용자 페이지 목록
     *
     * @param queryParams 조회参수
     * @return {@link IPage<UserPageVO>} 사용자 페이지 목록
     */
    @Override
    public IPage<UserPageVO> getUserPage(UserPageQuery queryParams) {

        // 参수构建
        int pageNum = queryParams.getPageNum();
        int pageSize = queryParams.getPageSize();
        Page<UserBO> page = new Page<>(pageNum, pageSize);

        boolean isRoot = SecurityUtils.isRoot();
        queryParams.setIsRoot(isRoot);

        // 조회데이터
        Page<UserBO> userPage = this.baseMapper.getUserPage(page, queryParams);

        // 实体转换
        return userConverter.toPageVo(userPage);
    }

    /**
     * 사용자 폼 데이터 조회
     *
     * @param userId 사용자ID
     * @return {@link UserForm} 사용자폼데이터
     */
    @Override
    public UserForm getUserFormData(Long userId) {
        return this.baseMapper.getUserFormData(userId);
    }

    /**
     * 사용자 추가
     *
     * @param userForm 사용자폼객체
     * @return true|false
     */
    @Override
    public boolean saveUser(UserForm userForm) {

        String username = userForm.getUsername();

        long count = this.count(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        Assert.isTrue(count == 0, "사용자명이미存에");

        // 实体转换 form->entity
        User entity = userConverter.toEntity(userForm);

        // 设置默认加密비밀번호
        String defaultEncryptPwd = passwordEncoder.encode(SystemConstants.DEFAULT_PASSWORD);
        entity.setPassword(defaultEncryptPwd);
        entity.setCreateBy(SecurityUtils.getUserId());

        // 사용자 추가
        boolean result = this.save(entity);

        if (result) {
            // 저장사용자역할
            userRoleService.saveUserRoles(entity.getId(), userForm.getRoleIds());
        }
        return result;
    }

    /**
     * 업데이트사용자
     *
     * @param userId   사용자ID
     * @param userForm 사용자폼객체
     * @return true|false
     */
    @Override
    @Transactional
    public boolean updateUser(Long userId, UserForm userForm) {

        String username = userForm.getUsername();

        long count = this.count(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username)
                .ne(User::getId, userId)
        );
        Assert.isTrue(count == 0, "사용자명이미存에");

        // form -> entity
        User entity = userConverter.toEntity(userForm);
        entity.setUpdateBy(SecurityUtils.getUserId());

        // 사용자 수정
        boolean result = this.updateById(entity);

        if (result) {
            // 저장사용자역할
            userRoleService.saveUserRoles(entity.getId(), userForm.getRoleIds());
        }
        return result;
    }

    /**
     * 사용자 삭제
     *
     * @param idsStr 사용자ID，여러 개는영문쉼표(,)로 구분
     * @return true|false
     */
    @Override
    public boolean deleteUsers(String idsStr) {
        Assert.isTrue(StrUtil.isNotBlank(idsStr), "삭제의사용자데이터값空");
        // 逻辑삭제
        List<Long> ids = Arrays.stream(idsStr.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());
        return this.removeByIds(ids);

    }

    /**
     * 根据사용자명조회인증凭证信息
     *
     * @param username 사용자명
     * @return 사용자인증凭证信息 {@link UserAuthCredentials}
     */
    @Override
    public UserAuthCredentials getAuthCredentialsByUsername(String username) {
        UserAuthCredentials userAuthCredentials = this.baseMapper.getAuthCredentialsByUsername(username);
        if (userAuthCredentials != null) {
            Set<String> roles = userAuthCredentials.getRoles();
            // 조회最大范围의데이터권한
            Integer dataScope = roleService.getMaximumDataScope(roles);
            userAuthCredentials.setDataScope(dataScope);
        }
        return userAuthCredentials;
    }

    /**
     * 根据OpenID조회사용자인증信息
     *
     * @param openId 위챗OpenID
     * @return 사용자인증信息
     */
    @Override
    public UserAuthCredentials getAuthCredentialsByOpenId(String openId) {
        if (StrUtil.isBlank(openId)) {
            return null;
        }
        UserAuthCredentials userAuthCredentials = this.baseMapper.getAuthCredentialsByOpenId(openId);
        if (userAuthCredentials != null) {
            Set<String> roles = userAuthCredentials.getRoles();
            // 조회最大范围의데이터권한
            Integer dataScope = roleService.getMaximumDataScope(roles);
            userAuthCredentials.setDataScope(dataScope);
        }
        return userAuthCredentials;
    }

    /**
     * 根据휴대폰 번호조회사용자인증信息
     *
     * @param mobile 휴대폰 번호
     * @return 사용자인증信息
     */
    @Override
    public UserAuthCredentials getAuthCredentialsByMobile(String mobile) {
        if (StrUtil.isBlank(mobile)) {
            return null;
        }
        UserAuthCredentials userAuthCredentials = this.baseMapper.getAuthCredentialsByMobile(mobile);
        if (userAuthCredentials != null) {
            Set<String> roles = userAuthCredentials.getRoles();
            // 조회最大范围의데이터권한
            Integer dataScope = roleService.getMaximumDataScope(roles);
            userAuthCredentials.setDataScope(dataScope);
        }
        return userAuthCredentials;
    }

    /**
     * 注册또는바인딩위챗사용자
     *
     * @param openId 위챗OpenID
     * @return 여부성공
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean registerOrBindWechatUser(String openId) {
        if (StrUtil.isBlank(openId)) {
            return false;
        }

        // 조회여부이미存에该openId의사용자
        User existUser = this.getOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getOpenid, openId)
        );

        if (existUser != null) {
            // 사용자이미存에，不需要注册
            return true;
        }

        // 생성새사용자
        User newUser = new User();
        newUser.setNickname("위챗사용자");  // 默认닉네임
        newUser.setUsername(openId);      // TODO 후续替换값휴대폰 번호
        newUser.setOpenid(openId);
        newUser.setGender(0); // 保密
        newUser.setUpdateBy(SecurityUtils.getUserId());
        newUser.setPassword(SystemConstants.DEFAULT_PASSWORD);
        newUser.setCreateTime(LocalDateTime.now());
        newUser.setUpdateTime(LocalDateTime.now());
        this.save(newUser);
        // 위해默认시스템관리员역할，这里按需调整，실제情况바인딩이미存에의시스템사용자，另원种情况是给默认游客역할，然후由시스템관리员设置사용자의역할
        UserRole userRole = new UserRole();
        userRole.setUserId(newUser.getId());
        userRole.setRoleId(1L);  // TODO 시스템관리员
        userRoleService.save(userRole);
        return true;
    }

    /**
     * 根据휴대폰 번호和OpenID注册사용자
     *
     * @param mobile 휴대폰 번호
     * @param openId 위챗OpenID
     * @return 여부성공
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean registerUserByMobileAndOpenId(String mobile, String openId) {
        if (StrUtil.isBlank(mobile) || StrUtil.isBlank(openId)) {
            return false;
        }

        // 先조회여부이미存에휴대폰 번호对应의사용자
        User existingUser = this.getOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getMobile, mobile)
        );

        if (existingUser != null) {
            // 如果存에사용자但没바인딩openId，则바인딩openId
            if (StrUtil.isBlank(existingUser.getOpenid())) {
                return bindUserOpenId(existingUser.getId(), openId);
            }
            // 如果이미经바인딩其他openId，则判断여부需要업데이트
            else if (!openId.equals(existingUser.getOpenid())) {
                return bindUserOpenId(existingUser.getId(), openId);
            }
            // 如果이미经바인딩相同의openId，则不需要任何操作
            return true;
        }

        // 不存에사용자，생성새사용자
        User newUser = new User();
        newUser.setMobile(mobile);
        newUser.setOpenid(openId);
        newUser.setUsername(mobile); // 사용휴대폰 번호作값사용자명
        newUser.setNickname("위챗사용자_" + mobile.substring(mobile.length() - 4)); // 사용휴대폰 번호후4자리作값닉네임
        newUser.setPassword(SystemConstants.DEFAULT_PASSWORD); // 사용加密의openId作값初始비밀번호
        newUser.setGender(0); // 保密
        newUser.setCreateTime(LocalDateTime.now());
        newUser.setUpdateTime(LocalDateTime.now());
        this.save(newUser);
        // 위해默认시스템관리员역할，这里按需调整，실제情况바인딩이미存에의시스템사용자，另원种情况是给默认游客역할，然후由시스템관리员设置사용자의역할
        UserRole userRole = new UserRole();
        userRole.setUserId(newUser.getId());
        userRole.setRoleId(1L);  // TODO 시스템관리员
        userRoleService.save(userRole);
        return true;
    }

    /**
     * 바인딩사용자위챗OpenID
     *
     * @param userId 사용자ID
     * @param openId 위챗OpenID
     * @return 여부성공
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean bindUserOpenId(Long userId, String openId) {
        if (userId == null || StrUtil.isBlank(openId)) {
            return false;
        }

        // 检查여부이미有其他사용자바인딩此openId
        User existingUser = this.getOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getOpenid, openId)
                        .ne(User::getId, userId)
        );

        if (existingUser != null) {
            log.warn("OpenID {} 이미被사용자 {} 바인딩，无法값사용자 {} 바인딩", openId, existingUser.getId(), userId);
            return false;
        }

        // 업데이트사용자openId
        boolean updated = this.update(
                new LambdaUpdateWrapper<User>()
                        .eq(User::getId, userId)
                        .set(User::getOpenid, openId)
                        .set(User::getUpdateTime, LocalDateTime.now())
        );
        return updated ;
    }

    /**
     * 조회사용자 내보내기 목록
     *
     * @param queryParams 조회参수
     * @return {@link List<UserExportDTO>} 사용자 내보내기 목록
     */
    @Override
    public List<UserExportDTO> listExportUsers(UserPageQuery queryParams) {

        boolean isRoot = SecurityUtils.isRoot();
        queryParams.setIsRoot(isRoot);

        List<UserExportDTO> exportUsers = this.baseMapper.listExportUsers(queryParams);
        if (CollectionUtil.isNotEmpty(exportUsers)) {
            //조회성별의사전 항목
            Map<String, String> genderMap = dictItemService.list(
                            new LambdaQueryWrapper<DictItem>().eq(DictItem::getDictCode,
                                    DictCodeEnum.GENDER.getValue())
                    ).stream()
                    .collect(Collectors.toMap(DictItem::getValue, DictItem::getLabel)
                    );

            exportUsers.forEach(item -> {
                String gender = item.getGender();
                if (StrUtil.isBlank(gender)) {
                    return;
                }

                // 判断map여부값空
                if (genderMap.isEmpty()) {
                    return;
                }

                item.setGender(genderMap.get(gender));
            });
        }
        return exportUsers;
    }

    /**
     * 조회로그인사용자 정보
     *
     * @return {@link CurrentUserDTO}   사용자 정보
     */
    @Override
    public CurrentUserDTO getCurrentUserInfo() {

        String username = SecurityUtils.getUsername();

        // 조회로그인사용자基础信息
        User user = this.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username)
                .select(
                        User::getId,
                        User::getUsername,
                        User::getNickname,
                        User::getAvatar
                )
        );
        // entity->VO
        CurrentUserDTO userInfoVO = userConverter.toCurrentUserDto(user);

        // 사용자역할集合
        Set<String> roles = SecurityUtils.getRoles();
        userInfoVO.setRoles(roles);

        // 사용자권한集合
        if (CollectionUtil.isNotEmpty(roles)) {
            Set<String> perms = permissionService.getRolePermsFormCache(roles);
            userInfoVO.setPerms(perms);
        }
        return userInfoVO;
    }

    /**
     * 개인센터 사용자 정보 조회
     *
     * @param userId 사용자ID
     * @return {@link UserProfileVO} 개인센터사용자 정보
     */
    @Override
    public UserProfileVO getUserProfile(Long userId) {
        UserBO entity = this.baseMapper.getUserProfile(userId);
        return userConverter.toProfileVo(entity);
    }

    /**
     * 수정개인센터사용자 정보
     *
     * @param formData 폼데이터
     * @return true|false
     */
    @Override
    public boolean updateUserProfile(UserProfileForm formData) {
        Long userId = SecurityUtils.getUserId();
        User entity = userConverter.toEntity(formData);
        entity.setId(userId);
        return this.updateById(entity);
    }

    /**
     * 수정지정된사용자비밀번호
     *
     * @param userId 사용자ID
     * @param data   비밀번호수정폼데이터
     * @return true|false
     */
    @Override
    public boolean changeUserPassword(Long userId, PasswordUpdateForm data) {

        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException("사용자不存에");
        }

        String oldPassword = data.getOldPassword();

        // 검증原비밀번호
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("原비밀번호错误");
        }
        // 새旧비밀번호不能相同
        if (passwordEncoder.matches(data.getNewPassword(), user.getPassword())) {
            throw new BusinessException("새비밀번호不能与原비밀번호相同");
        }

        // 判断새비밀번호和确认비밀번호여부원致
        if (passwordEncoder.matches(data.getNewPassword(), data.getConfirmPassword())) {
            throw new BusinessException("새비밀번호和确认비밀번호不원致");
        }

        String newPassword = data.getNewPassword();
        boolean result = this.update(new LambdaUpdateWrapper<User>()
                .eq(User::getId, userId)
                .set(User::getPassword, passwordEncoder.encode(newPassword))
        );

        if (result) {
            // 비밀번호变更후，使현재사용자의所有세션失效，强制重새로그인
            tokenManager.invalidateUserSessions(userId);
        }
        return result;
    }

    /**
     * 지정된 사용자 비밀번호 재설정
     *
     * @param userId   사용자ID
     * @param password 비밀번호재설정폼데이터
     * @return true|false
     */
    @Override
    public boolean resetUserPassword(Long userId, String password) {
        boolean result = this.update(new LambdaUpdateWrapper<User>()
                .eq(User::getId, userId)
                .set(User::getPassword, passwordEncoder.encode(password))
        );
        if (result) {
            // 관리员재설정사용자비밀번호후，使该사용자의所有세션失效
            tokenManager.invalidateUserSessions(userId);
        }
        return result;
    }

    /**
     * 발송SMS인증코드(휴대폰 번호 바인딩 또는 변경)
     *
     * @param mobile 휴대폰 번호
     * @return true|false
     */
    @Override
    public boolean sendMobileCode(String mobile) {

        // String code = String.valueOf((int) ((Math.random() * 9 + 1) * 1000));
        // TODO 위해편의테스트，인증코드固定값 1234，실제개발중에SMS 서비스 설정 후，가는사용上面의랜덤인증코드
        String code = "1234";

        Map<String, String> templateParams = new HashMap<>();
        templateParams.put("code", code);
        boolean result = smsService.sendSms(mobile, SmsTypeEnum.CHANGE_MOBILE, templateParams);
        if (result) {
            // 캐시인증코드，5分钟有效，용도변경휴대폰 번호검증
            String redisCacheKey = StrUtil.format(RedisConstants.Captcha.MOBILE_CODE, mobile);
            redisTemplate.opsForValue().set(redisCacheKey, code, 5, TimeUnit.MINUTES);
        }
        return result;
    }

    /**
     * 휴대폰 번호 바인딩 또는 변경
     *
     * @param form 폼데이터
     * @return true|false
     */
    @Override
    public boolean bindOrChangeMobile(MobileUpdateForm form) {

        Long currentUserId = SecurityUtils.getUserId();
        User currentUser = this.getById(currentUserId);

        if (currentUser == null) {
            throw new BusinessException("사용자不存에");
        }

        // 검증인증코드
        String inputVerifyCode = form.getCode();
        String mobile = form.getMobile();

        String cacheKey = StrUtil.format(RedisConstants.Captcha.MOBILE_CODE, mobile);

        String cachedVerifyCode = redisTemplate.opsForValue().get(cacheKey);

        if (StrUtil.isBlank(cachedVerifyCode)) {
            throw new BusinessException("인증코드이미过期");
        }
        if (!inputVerifyCode.equals(cachedVerifyCode)) {
            throw new BusinessException("인증코드错误");
        }
        // 验证完成삭제인증코드
        redisTemplate.delete(cacheKey);

        // 업데이트휴대폰 번호
        return this.update(
                new LambdaUpdateWrapper<User>()
                        .eq(User::getId, currentUserId)
                        .set(User::getMobile, mobile)
        );
    }

    /**
     * 발송이메일 인증코드(이메일 바인딩 또는 변경)
     *
     * @param email 이메일
     */
    @Override
    public void sendEmailCode(String email) {

        // String code = String.valueOf((int) ((Math.random() * 9 + 1) * 1000));
        // TODO 위해편의테스트，인증코드固定값 1234，실제개발중에설정이메일서비스후，가는사용上面의랜덤인증코드
        String code = "1234";

        mailService.sendMail(email, "이메일인증코드", "您의인증코드값：" + code + "，请에5分钟内사용");
        // 캐시인증코드，5分钟有效，용도변경이메일검증
        String redisCacheKey = StrUtil.format(RedisConstants.Captcha.EMAIL_CODE, email);
        redisTemplate.opsForValue().set(redisCacheKey, code, 5, TimeUnit.MINUTES);
    }

    /**
     * 수정현재사용자이메일
     *
     * @param form 폼데이터
     * @return true|false
     */
    @Override
    public boolean bindOrChangeEmail(EmailUpdateForm form) {

        Long currentUserId = SecurityUtils.getUserId();

        User currentUser = this.getById(currentUserId);
        if (currentUser == null) {
            throw new BusinessException("사용자不存에");
        }

        // 조회前端输入의인증코드
        String inputVerifyCode = form.getCode();

        // 조회캐시의인증코드
        String email = form.getEmail();
        String redisCacheKey = StrUtil.format(RedisConstants.Captcha.EMAIL_CODE, email);
        String cachedVerifyCode = redisTemplate.opsForValue().get(redisCacheKey);

        if (StrUtil.isBlank(cachedVerifyCode)) {
            throw new BusinessException("인증코드이미过期");
        }

        if (!inputVerifyCode.equals(cachedVerifyCode)) {
            throw new BusinessException("인증코드错误");
        }
        // 验证完成삭제인증코드
        redisTemplate.delete(redisCacheKey);

        // 업데이트이메일 주소
        return this.update(
                new LambdaUpdateWrapper<User>()
                        .eq(User::getId, currentUserId)
                        .set(User::getEmail, email)
        );
    }

    /**
     * 조회사용자옵션목록
     *
     * @return {@link List<Option<String>>} 사용자옵션목록
     */
    @Override
    public List<Option<String>> listUserOptions() {
        List<User> list = this.list(new LambdaQueryWrapper<User>()
                .eq(User::getStatus, 1)
        );
        return userConverter.toOptions(list);
    }

}

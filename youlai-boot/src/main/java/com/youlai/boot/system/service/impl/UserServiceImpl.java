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
 * 사용자 비즈니스 구현 클래스
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
     * 사용자 페이지 목록 조회
     *
     * @param queryParams 조회 파라미터
     * @return {@link IPage<UserPageVO>} 사용자 페이지 목록
     */
    @Override
    public IPage<UserPageVO> getUserPage(UserPageQuery queryParams) {

        // 파라미터 구성
        int pageNum = queryParams.getPageNum();
        int pageSize = queryParams.getPageSize();
        Page<UserBO> page = new Page<>(pageNum, pageSize);

        boolean isRoot = SecurityUtils.isRoot();
        queryParams.setIsRoot(isRoot);

        // 데이터 조회
        Page<UserBO> userPage = this.baseMapper.getUserPage(page, queryParams);

        // 엔티티 변환
        return userConverter.toPageVo(userPage);
    }

    /**
     * 사용자 폼 데이터 조회
     *
     * @param userId 사용자 ID
     * @return {@link UserForm} 사용자 폼 데이터
     */
    @Override
    public UserForm getUserFormData(Long userId) {
        return this.baseMapper.getUserFormData(userId);
    }

    /**
     * 사용자 추가
     *
     * @param userForm 사용자 폼 객체
     * @return true|false
     */
    @Override
    public boolean saveUser(UserForm userForm) {

        String username = userForm.getUsername();

        long count = this.count(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        Assert.isTrue(count == 0, "사용자명이 이미 존재합니다");

        // 엔티티 변환 form->entity
        User entity = userConverter.toEntity(userForm);

        // 기본 암호화 비밀번호 설정
        String defaultEncryptPwd = passwordEncoder.encode(SystemConstants.DEFAULT_PASSWORD);
        entity.setPassword(defaultEncryptPwd);
        entity.setCreateBy(SecurityUtils.getUserId());

        // 사용자 추가
        boolean result = this.save(entity);

        if (result) {
            // 사용자 역할 저장
            userRoleService.saveUserRoles(entity.getId(), userForm.getRoleIds());
        }
        return result;
    }

    /**
     * 사용자 업데이트
     *
     * @param userId   사용자 ID
     * @param userForm 사용자 폼 객체
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
        Assert.isTrue(count == 0, "사용자명이 이미 존재합니다");

        // form -> entity
        User entity = userConverter.toEntity(userForm);
        entity.setUpdateBy(SecurityUtils.getUserId());

        // 사용자 수정
        boolean result = this.updateById(entity);

        if (result) {
            // 사용자 역할 저장
            userRoleService.saveUserRoles(entity.getId(), userForm.getRoleIds());
        }
        return result;
    }

    /**
     * 사용자 삭제
     *
     * @param idsStr 사용자 ID, 여러 개는 영문 쉼표(,)로 구분
     * @return true|false
     */
    @Override
    public boolean deleteUsers(String idsStr) {
        Assert.isTrue(StrUtil.isNotBlank(idsStr), "삭제할 사용자 데이터가 비어있습니다");
        // 논리적 삭제
        List<Long> ids = Arrays.stream(idsStr.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());
        return this.removeByIds(ids);

    }

    /**
     * 사용자명으로 인증 자격 정보 조회
     *
     * @param username 사용자명
     * @return 사용자 인증 자격 정보 {@link UserAuthCredentials}
     */
    @Override
    public UserAuthCredentials getAuthCredentialsByUsername(String username) {
        UserAuthCredentials userAuthCredentials = this.baseMapper.getAuthCredentialsByUsername(username);
        if (userAuthCredentials != null) {
            Set<String> roles = userAuthCredentials.getRoles();
            // 최대 범위의 데이터 권한 조회
            Integer dataScope = roleService.getMaximumDataScope(roles);
            userAuthCredentials.setDataScope(dataScope);
        }
        return userAuthCredentials;
    }

    /**
     * OpenID로 사용자 인증 정보 조회
     *
     * @param openId 위챗 OpenID
     * @return 사용자 인증 정보
     */
    @Override
    public UserAuthCredentials getAuthCredentialsByOpenId(String openId) {
        if (StrUtil.isBlank(openId)) {
            return null;
        }
        UserAuthCredentials userAuthCredentials = this.baseMapper.getAuthCredentialsByOpenId(openId);
        if (userAuthCredentials != null) {
            Set<String> roles = userAuthCredentials.getRoles();
            // 최대 범위의 데이터 권한 조회
            Integer dataScope = roleService.getMaximumDataScope(roles);
            userAuthCredentials.setDataScope(dataScope);
        }
        return userAuthCredentials;
    }

    /**
     * 휴대폰 번호로 사용자 인증 정보 조회
     *
     * @param mobile 휴대폰 번호
     * @return 사용자 인증 정보
     */
    @Override
    public UserAuthCredentials getAuthCredentialsByMobile(String mobile) {
        if (StrUtil.isBlank(mobile)) {
            return null;
        }
        UserAuthCredentials userAuthCredentials = this.baseMapper.getAuthCredentialsByMobile(mobile);
        if (userAuthCredentials != null) {
            Set<String> roles = userAuthCredentials.getRoles();
            // 최대 범위의 데이터 권한 조회
            Integer dataScope = roleService.getMaximumDataScope(roles);
            userAuthCredentials.setDataScope(dataScope);
        }
        return userAuthCredentials;
    }

    /**
     * 위챗 사용자 등록 또는 바인딩
     *
     * @param openId 위챗 OpenID
     * @return 성공 여부
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean registerOrBindWechatUser(String openId) {
        if (StrUtil.isBlank(openId)) {
            return false;
        }

        // 해당 openId의 사용자가 이미 존재하는지 조회
        User existUser = this.getOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getOpenid, openId)
        );

        if (existUser != null) {
            // 사용자가 이미 존재하면 등록 불필요
            return true;
        }

        // 새 사용자 생성
        User newUser = new User();
        newUser.setNickname("위챗사용자");  // 기본 닉네임
        newUser.setUsername(openId);      // TODO 추후 휴대폰 번호로 대체
        newUser.setOpenid(openId);
        newUser.setGender(0); // 비공개
        newUser.setUpdateBy(SecurityUtils.getUserId());
        newUser.setPassword(SystemConstants.DEFAULT_PASSWORD);
        newUser.setCreateTime(LocalDateTime.now());
        newUser.setUpdateTime(LocalDateTime.now());
        this.save(newUser);
        // 기본 시스템 관리자 역할 부여, 여기서 필요에 따라 조정, 실제 상황에서는 기존 시스템 사용자에 바인딩하거나 기본 게스트 역할을 부여한 후 시스템 관리자가 사용자 역할을 설정
        UserRole userRole = new UserRole();
        userRole.setUserId(newUser.getId());
        userRole.setRoleId(1L);  // TODO 시스템 관리자
        userRoleService.save(userRole);
        return true;
    }

    /**
     * 휴대폰 번호와 OpenID로 사용자 등록
     *
     * @param mobile 휴대폰 번호
     * @param openId 위챗 OpenID
     * @return 성공 여부
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean registerUserByMobileAndOpenId(String mobile, String openId) {
        if (StrUtil.isBlank(mobile) || StrUtil.isBlank(openId)) {
            return false;
        }

        // 먼저 휴대폰 번호에 해당하는 사용자가 이미 존재하는지 조회
        User existingUser = this.getOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getMobile, mobile)
        );

        if (existingUser != null) {
            // 사용자가 존재하지만 openId가 바인딩되지 않은 경우, openId 바인딩
            if (StrUtil.isBlank(existingUser.getOpenid())) {
                return bindUserOpenId(existingUser.getId(), openId);
            }
            // 이미 다른 openId가 바인딩된 경우, 업데이트 필요 여부 판단
            else if (!openId.equals(existingUser.getOpenid())) {
                return bindUserOpenId(existingUser.getId(), openId);
            }
            // 이미 동일한 openId가 바인딩된 경우, 작업 불필요
            return true;
        }

        // 사용자가 존재하지 않으면 새 사용자 생성
        User newUser = new User();
        newUser.setMobile(mobile);
        newUser.setOpenid(openId);
        newUser.setUsername(mobile); // 휴대폰 번호를 사용자명으로 사용
        newUser.setNickname("위챗사용자_" + mobile.substring(mobile.length() - 4)); // 휴대폰 번호 뒤 4자리를 닉네임으로 사용
        newUser.setPassword(SystemConstants.DEFAULT_PASSWORD); // 암호화된 openId를 초기 비밀번호로 사용
        newUser.setGender(0); // 비공개
        newUser.setCreateTime(LocalDateTime.now());
        newUser.setUpdateTime(LocalDateTime.now());
        this.save(newUser);
        // 기본 시스템 관리자 역할 부여, 여기서 필요에 따라 조정, 실제 상황에서는 기존 시스템 사용자에 바인딩하거나 기본 게스트 역할을 부여한 후 시스템 관리자가 사용자 역할을 설정
        UserRole userRole = new UserRole();
        userRole.setUserId(newUser.getId());
        userRole.setRoleId(1L);  // TODO 시스템 관리자
        userRoleService.save(userRole);
        return true;
    }

    /**
     * 사용자 위챗 OpenID 바인딩
     *
     * @param userId 사용자 ID
     * @param openId 위챗 OpenID
     * @return 성공 여부
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean bindUserOpenId(Long userId, String openId) {
        if (userId == null || StrUtil.isBlank(openId)) {
            return false;
        }

        // 다른 사용자가 이미 이 openId를 바인딩했는지 확인
        User existingUser = this.getOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getOpenid, openId)
                        .ne(User::getId, userId)
        );

        if (existingUser != null) {
            log.warn("OpenID {}가 이미 사용자 {}에게 바인딩되어 있어 사용자 {}에게 바인딩할 수 없습니다", openId, existingUser.getId(), userId);
            return false;
        }

        // 사용자 openId 업데이트
        boolean updated = this.update(
                new LambdaUpdateWrapper<User>()
                        .eq(User::getId, userId)
                        .set(User::getOpenid, openId)
                        .set(User::getUpdateTime, LocalDateTime.now())
        );
        return updated ;
    }

    /**
     * 사용자 내보내기 목록 조회
     *
     * @param queryParams 조회 파라미터
     * @return {@link List<UserExportDTO>} 사용자 내보내기 목록
     */
    @Override
    public List<UserExportDTO> listExportUsers(UserPageQuery queryParams) {

        boolean isRoot = SecurityUtils.isRoot();
        queryParams.setIsRoot(isRoot);

        List<UserExportDTO> exportUsers = this.baseMapper.listExportUsers(queryParams);
        if (CollectionUtil.isNotEmpty(exportUsers)) {
            // 성별 사전 항목 조회
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

                // map이 비어있는지 확인
                if (genderMap.isEmpty()) {
                    return;
                }

                item.setGender(genderMap.get(gender));
            });
        }
        return exportUsers;
    }

    /**
     * 로그인 사용자 정보 조회
     *
     * @return {@link CurrentUserDTO}   사용자 정보
     */
    @Override
    public CurrentUserDTO getCurrentUserInfo() {

        String username = SecurityUtils.getUsername();

        // 로그인 사용자 기본 정보 조회
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

        // 사용자 역할 집합
        Set<String> roles = SecurityUtils.getRoles();
        userInfoVO.setRoles(roles);

        // 사용자 권한 집합
        if (CollectionUtil.isNotEmpty(roles)) {
            Set<String> perms = permissionService.getRolePermsFormCache(roles);
            userInfoVO.setPerms(perms);
        }
        return userInfoVO;
    }

    /**
     * 개인센터 사용자 정보 조회
     *
     * @param userId 사용자 ID
     * @return {@link UserProfileVO} 개인센터 사용자 정보
     */
    @Override
    public UserProfileVO getUserProfile(Long userId) {
        UserBO entity = this.baseMapper.getUserProfile(userId);
        return userConverter.toProfileVo(entity);
    }

    /**
     * 개인센터 사용자 정보 수정
     *
     * @param formData 폼 데이터
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
     * 지정된 사용자 비밀번호 수정
     *
     * @param userId 사용자 ID
     * @param data   비밀번호 수정 폼 데이터
     * @return true|false
     */
    @Override
    public boolean changeUserPassword(Long userId, PasswordUpdateForm data) {

        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException("사용자가 존재하지 않습니다");
        }

        String oldPassword = data.getOldPassword();

        // 기존 비밀번호 검증
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("기존 비밀번호가 올바르지 않습니다");
        }
        // 새 비밀번호와 기존 비밀번호가 같으면 안됨
        if (passwordEncoder.matches(data.getNewPassword(), user.getPassword())) {
            throw new BusinessException("새 비밀번호는 기존 비밀번호와 같을 수 없습니다");
        }

        // 새 비밀번호와 확인 비밀번호가 일치하는지 확인
        if (passwordEncoder.matches(data.getNewPassword(), data.getConfirmPassword())) {
            throw new BusinessException("새 비밀번호와 확인 비밀번호가 일치하지 않습니다");
        }

        String newPassword = data.getNewPassword();
        boolean result = this.update(new LambdaUpdateWrapper<User>()
                .eq(User::getId, userId)
                .set(User::getPassword, passwordEncoder.encode(newPassword))
        );

        if (result) {
            // 비밀번호 변경 후 현재 사용자의 모든 세션을 무효화하고 강제로 다시 로그인
            tokenManager.invalidateUserSessions(userId);
        }
        return result;
    }

    /**
     * 지정된 사용자 비밀번호 재설정
     *
     * @param userId   사용자 ID
     * @param password 비밀번호 재설정 폼 데이터
     * @return true|false
     */
    @Override
    public boolean resetUserPassword(Long userId, String password) {
        boolean result = this.update(new LambdaUpdateWrapper<User>()
                .eq(User::getId, userId)
                .set(User::getPassword, passwordEncoder.encode(password))
        );
        if (result) {
            // 관리자가 사용자 비밀번호 재설정 후 해당 사용자의 모든 세션 무효화
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
        // TODO 테스트 편의를 위해 인증코드를 1234로 고정, 실제 개발 시 SMS 서비스 설정 후 위의 랜덤 인증코드 사용 가능
        String code = "1234";

        Map<String, String> templateParams = new HashMap<>();
        templateParams.put("code", code);
        boolean result = smsService.sendSms(mobile, SmsTypeEnum.CHANGE_MOBILE, templateParams);
        if (result) {
            // 인증코드 캐시, 5분간 유효, 휴대폰 번호 변경 검증용
            String redisCacheKey = StrUtil.format(RedisConstants.Captcha.MOBILE_CODE, mobile);
            redisTemplate.opsForValue().set(redisCacheKey, code, 5, TimeUnit.MINUTES);
        }
        return result;
    }

    /**
     * 휴대폰 번호 바인딩 또는 변경
     *
     * @param form 폼 데이터
     * @return true|false
     */
    @Override
    public boolean bindOrChangeMobile(MobileUpdateForm form) {

        Long currentUserId = SecurityUtils.getUserId();
        User currentUser = this.getById(currentUserId);

        if (currentUser == null) {
            throw new BusinessException("사용자가 존재하지 않습니다");
        }

        // 인증코드 검증
        String inputVerifyCode = form.getCode();
        String mobile = form.getMobile();

        String cacheKey = StrUtil.format(RedisConstants.Captcha.MOBILE_CODE, mobile);

        String cachedVerifyCode = redisTemplate.opsForValue().get(cacheKey);

        if (StrUtil.isBlank(cachedVerifyCode)) {
            throw new BusinessException("인증코드가 만료되었습니다");
        }
        if (!inputVerifyCode.equals(cachedVerifyCode)) {
            throw new BusinessException("인증코드가 올바르지 않습니다");
        }
        // 검증 완료 후 인증코드 삭제
        redisTemplate.delete(cacheKey);

        // 휴대폰 번호 업데이트
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
        // TODO 테스트 편의를 위해 인증코드를 1234로 고정, 실제 개발 시 이메일 서비스 설정 후 위의 랜덤 인증코드 사용 가능
        String code = "1234";

        mailService.sendMail(email, "이메일 인증코드", "귀하의 인증코드: " + code + ", 5분 내에 사용해주세요");
        // 인증코드 캐시, 5분간 유효, 이메일 변경 검증용
        String redisCacheKey = StrUtil.format(RedisConstants.Captcha.EMAIL_CODE, email);
        redisTemplate.opsForValue().set(redisCacheKey, code, 5, TimeUnit.MINUTES);
    }

    /**
     * 현재 사용자 이메일 수정
     *
     * @param form 폼 데이터
     * @return true|false
     */
    @Override
    public boolean bindOrChangeEmail(EmailUpdateForm form) {

        Long currentUserId = SecurityUtils.getUserId();

        User currentUser = this.getById(currentUserId);
        if (currentUser == null) {
            throw new BusinessException("사용자가 존재하지 않습니다");
        }

        // 프론트엔드에서 입력한 인증코드 조회
        String inputVerifyCode = form.getCode();

        // 캐시된 인증코드 조회
        String email = form.getEmail();
        String redisCacheKey = StrUtil.format(RedisConstants.Captcha.EMAIL_CODE, email);
        String cachedVerifyCode = redisTemplate.opsForValue().get(redisCacheKey);

        if (StrUtil.isBlank(cachedVerifyCode)) {
            throw new BusinessException("인증코드가 만료되었습니다");
        }

        if (!inputVerifyCode.equals(cachedVerifyCode)) {
            throw new BusinessException("인증코드가 올바르지 않습니다");
        }
        // 검증 완료 후 인증코드 삭제
        redisTemplate.delete(redisCacheKey);

        // 이메일 주소 업데이트
        return this.update(
                new LambdaUpdateWrapper<User>()
                        .eq(User::getId, currentUserId)
                        .set(User::getEmail, email)
        );
    }

    /**
     * 사용자 옵션 목록 조회
     *
     * @return {@link List<Option<String>>} 사용자 옵션 목록
     */
    @Override
    public List<Option<String>> listUserOptions() {
        List<User> list = this.list(new LambdaQueryWrapper<User>()
                .eq(User::getStatus, 1)
        );
        return userConverter.toOptions(list);
    }

}

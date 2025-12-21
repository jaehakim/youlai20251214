package com.youlai.boot.system.listener;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.idev.excel.context.AnalysisContext;
import cn.idev.excel.event.AnalysisEventListener;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.youlai.boot.common.constant.SystemConstants;
import com.youlai.boot.common.enums.StatusEnum;
import com.youlai.boot.core.web.ExcelResult;
import com.youlai.boot.system.converter.UserConverter;
import com.youlai.boot.system.enums.DictCodeEnum;
import com.youlai.boot.system.model.dto.UserImportDTO;
import com.youlai.boot.system.model.entity.*;
import com.youlai.boot.system.service.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 사용자 가져오기 리스너
 * <p>
 * <a href="https://easyexcel.opensource.alibaba.com/docs/current/quickstart/read#%E6%9C%80%E7%AE%80%E5%8D%95%E7%9A%84%E8%AF%BB%E7%9A%84%E7%9B%91%E5%90%AC%E5%99%A8">가장 간단한 읽기 리스너</a>
 *
 * @author Ray
 * @since 2022/4/10
 */
@Slf4j
public class UserImportListener extends AnalysisEventListener<UserImportDTO> {

    /**
     * Excel 가져오기 결과
     */
    @Getter
    private final ExcelResult excelResult;

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final UserConverter userConverter;
    private final UserRoleService userRoleService;

    private final List<Role> roleList;
    private final List<Dept> deptList;
    private final List<DictItem> genderList;

    /**
     * 현재 행
     */
    private Integer currentRow = 1;

    /**
     * 생성자 메서드
     * <p>생성자 메서드에서 쿼리가 필요한 내용을 미리 조회하여 각 데이터마다 쿼리하는 것을 최대한 피함</p>
     */
    public UserImportListener() {
        this.userService = SpringUtil.getBean(UserService.class);
        this.passwordEncoder = SpringUtil.getBean(PasswordEncoder.class);
        this.userRoleService = SpringUtil.getBean(UserRoleService.class);
        this.userConverter = SpringUtil.getBean(UserConverter.class);
        this.roleList = SpringUtil.getBean(RoleService.class)
                .list(new LambdaQueryWrapper<Role>().eq(Role::getStatus, StatusEnum.ENABLE.getValue())
                        .select(Role::getId, Role::getCode));
        this.deptList = SpringUtil.getBean(DeptService.class)
                .list(new LambdaQueryWrapper<Dept>().select(Dept::getId, Dept::getCode));
        this.genderList = SpringUtil.getBean(DictItemService.class)
                .list(new LambdaQueryWrapper<DictItem>().eq(DictItem::getDictCode, DictCodeEnum.GENDER.getValue()));
        this.excelResult = new ExcelResult();
    }

    /**
     * 각 데이터 파싱마다 호출됨
     * <p>
     * 1. 데이터 검증: 전체 필드 검증
     * 2. 데이터 영속화
     *
     * @param userImportDTO 한 행의 데이터, {@link AnalysisContext#readRowHolder()}와 유사
     */
    @Override
    public void invoke(UserImportDTO userImportDTO, AnalysisContext analysisContext) {
        log.info("사용자 데이터 파싱: {}", JSONUtil.toJsonStr(userImportDTO));

        boolean validation = true;
        String errorMsg = "제" + currentRow + "행 데이터 검증 실패: ";
        String username = userImportDTO.getUsername();
        if (StrUtil.isBlank(username)) {
            errorMsg += "사용자명이 비어있음; ";
            validation = false;
        } else {
            long count = userService.count(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
            if (count > 0) {
                errorMsg += "사용자명이 이미 존재함; ";
                validation = false;
            }
        }

        String nickname = userImportDTO.getNickname();
        if (StrUtil.isBlank(nickname)) {
            errorMsg += "사용자 닉네임이 비어있음; ";
            validation = false;
        }

        String mobile = userImportDTO.getMobile();
        if (StrUtil.isBlank(mobile)) {
            errorMsg += "휴대폰 번호가 비어있음; ";
            validation = false;
        } else {
            if (!Validator.isMobile(mobile)) {
                errorMsg += "휴대폰 번호가 올바르지 않음; ";
                validation = false;
            }
        }

        if (validation) {
            // 검증 통과, 데이터베이스에 영속화
            User entity = userConverter.toEntity(userImportDTO);
            entity.setPassword(passwordEncoder.encode(SystemConstants.DEFAULT_PASSWORD));   // 기본 비밀번호
            // 성별 역변환 - 사전 레이블로부터 사전 값 획득
            String genderLabel = userImportDTO.getGenderLabel();
            entity.setGender(getGenderValue(genderLabel));
            // 역할 파싱
            String roleCodes = userImportDTO.getRoleCodes();
            List<Long> roleIds = getRoleIds(roleCodes);
            // 부서 파싱
            String deptCode = userImportDTO.getDeptCode();
            entity.setDeptId(getDeptId(deptCode));

            boolean saveResult = userService.save(entity);
            if (saveResult) {
                excelResult.setValidCount(excelResult.getValidCount() + 1);
                // 사용자 역할 연관 저장
                if (CollectionUtil.isNotEmpty(roleIds)) {
                    List<UserRole> userRoles = roleIds.stream()
                            .map(roleId -> new UserRole(entity.getId(), roleId))
                            .collect(Collectors.toList());
                    userRoleService.saveBatch(userRoles);
                }
            } else {
                excelResult.setInvalidCount(excelResult.getInvalidCount() + 1);
                errorMsg += "제" + currentRow + "행 데이터 저장 실패; ";
                excelResult.getMessageList().add(errorMsg);
            }
        } else {
            excelResult.setInvalidCount(excelResult.getInvalidCount() + 1);
            excelResult.getMessageList().add(errorMsg);
        }
        currentRow++;
    }


    /**
     * 역할 코드로 역할 ID 가져오기
     *
     * @param roleCodes 역할 코드 쉼표로 구분
     * @return 역할 ID 집합
     */
    private List<Long> getRoleIds(String roleCodes) {
        if (StrUtil.isNotBlank(roleCodes)) {
            String[] split = roleCodes.split(",");
            if (split.length > 0) {
                List<Long> roleIds = new ArrayList<>();
                for (String roleCode : split) {
                    this.roleList.stream().filter(r -> r.getCode().equals(roleCode))
                            .findFirst().ifPresent(role -> roleIds.add(role.getId()));
                }
                return roleIds.stream().distinct().toList();
            }
        }
        return Collections.emptyList();
    }

    /**
     * 부서 코드로 부서 ID 가져오기
     *
     * @param deptCode 부서 코드
     * @return 부서 ID
     */
    private Long getDeptId(String deptCode) {
        if (StrUtil.isNotBlank(deptCode)) {
            return this.deptList.stream().filter(r -> r.getCode().equals(deptCode))
                    .findFirst().map(Dept::getId).orElse(null);
        }
        return null;
    }

    /**
     * 성별 레이블로 성별 값 가져오기
     *
     * @param genderLabel 성별 레이블
     * @return 성별 값
     */
    private Integer getGenderValue(String genderLabel) {
        if (StrUtil.isNotBlank(genderLabel)) {
            return this.genderList.stream()
                    .filter(r -> r.getLabel().equals(genderLabel))
                    .findFirst()
                    .map(DictItem::getValue)
                    .map(Convert::toInt)
                    .orElse(null);
        }
        return null;
    }

    /**
     * 모든 데이터 파싱 완료 시 호출됨
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        log.info("모든 데이터 파싱 완료!");
    }

}

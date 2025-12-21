package com.youlai.boot.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.boot.system.model.bo.UserBO;
import com.youlai.boot.system.model.entity.User;
import com.youlai.boot.system.model.query.UserPageQuery;
import com.youlai.boot.system.model.form.UserForm;
import com.youlai.boot.common.annotation.DataPermission;
import com.youlai.boot.security.model.UserAuthCredentials;
import com.youlai.boot.system.model.dto.UserExportDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 사용자 영속성 계층 인터페이스
 *
 * @author Ray.Hao
 * @since 2022/1/14
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 사용자 페이지 목록 가져오기
     *
     * @param page        페이지 매개변수
     * @param queryParams 쿼리 매개변수
     * @return 사용자 페이지 목록
     */
    @DataPermission(deptAlias = "u", userAlias = "u")
    Page<UserBO> getUserPage(Page<UserBO> page, UserPageQuery queryParams);

    /**
     * 사용자 양식 상세정보 가져오기
     *
     * @param userId 사용자 ID
     * @return 사용자 양식 상세정보
     */
    UserForm getUserFormData(Long userId);

    /**
     * 사용자명으로 인증정보 가져오기
     *
     * @param username 사용자명
     * @return 인증정보
     */
    UserAuthCredentials getAuthCredentialsByUsername(String username);

    /**
     * 위챗 openid로 사용자 인증정보 가져오기
     *
     * @param openid 위챗 openid
     * @return 인증정보
     */
    UserAuthCredentials getAuthCredentialsByOpenId(String openid);

    /**
     * 휴대폰 번호로 사용자 인증정보 가져오기
     *
     * @param mobile 휴대폰 번호
     * @return 인증정보
     */
    UserAuthCredentials getAuthCredentialsByMobile(String mobile);

    /**
     * 내보내기 사용자 목록 가져오기
     *
     * @param queryParams 쿼리 매개변수
     * @return 내보내기 사용자 목록
     */
    @DataPermission(deptAlias = "u", userAlias = "u")
    List<UserExportDTO> listExportUsers(UserPageQuery queryParams);

    /**
     * 사용자 개인센터 정보 가져오기
     *
     * @param userId 사용자 ID
     * @return 사용자 개인센터 정보
     */
    UserBO getUserProfile(Long userId);

}

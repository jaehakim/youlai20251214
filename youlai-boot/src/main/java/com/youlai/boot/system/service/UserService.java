package com.youlai.boot.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.youlai.boot.common.model.Option;
import com.youlai.boot.security.model.UserAuthCredentials;
import com.youlai.boot.system.model.dto.CurrentUserDTO;
import com.youlai.boot.system.model.dto.UserExportDTO;
import com.youlai.boot.system.model.entity.User;
import com.youlai.boot.system.model.query.UserPageQuery;
import com.youlai.boot.system.model.vo.UserPageVO;
import com.youlai.boot.system.model.vo.UserProfileVO;
import com.youlai.boot.system.model.form.*;

import java.util.List;

/**
 * 사용자 비즈니스 인터페이스
 *
 * @author Ray.Hao
 * @since 2022/1/14
 */
public interface UserService extends IService<User> {

    /**
     * 사용자 페이지 목록
     *
     * @return {@link IPage<UserPageVO>} 사용자 페이지 목록
     */
    IPage<UserPageVO> getUserPage(UserPageQuery queryParams);

    /**
     * 사용자 폼 데이터 조회
     *
     * @param userId 사용자 ID
     * @return {@link UserForm} 사용자 폼 데이터
     */
    UserForm getUserFormData(Long userId);


    /**
     * 사용자 추가
     *
     * @param userForm 사용자 폼 객체
     * @return {@link Boolean} 추가 성공 여부
     */
    boolean saveUser(UserForm userForm);

    /**
     * 사용자 수정
     *
     * @param userId   사용자 ID
     * @param userForm 사용자 폼 객체
     * @return {@link Boolean} 수정 성공 여부
     */
    boolean updateUser(Long userId, UserForm userForm);


    /**
     * 사용자 삭제
     *
     * @param idsStr 사용자 ID, 여러 개는 영문 쉼표(,)로 구분
     * @return {@link Boolean} 삭제 성공 여부
     */
    boolean deleteUsers(String idsStr);


    /**
     * 사용자명으로 인증 정보 조회
     *
     * @param username 사용자명
     * @return {@link UserAuthCredentials}
     */

    UserAuthCredentials getAuthCredentialsByUsername(String username);


    /**
     * 조회 사용자 내보내기 목록
     *
     * @param queryParams 조회 파라미터
     * @return {@link List<UserExportDTO>} 사용자 내보내기 목록
     */
    List<UserExportDTO> listExportUsers(UserPageQuery queryParams);


    /**
     * 조회 로그인 사용자 정보
     *
     * @return {@link CurrentUserDTO} 로그인 사용자 정보
     */
    CurrentUserDTO getCurrentUserInfo();

    /**
     * 개인센터 사용자 정보 조회
     *
     * @return {@link UserProfileVO} 개인센터 사용자 정보
     */
    UserProfileVO getUserProfile(Long userId);

    /**
     * 수정 개인센터 사용자 정보
     *
     * @param formData 폼 데이터
     * @return {@link Boolean} 수정 성공 여부
     */
    boolean updateUserProfile(UserProfileForm formData);

    /**
     * 수정 지정된 사용자 비밀번호
     *
     * @param userId 사용자 ID
     * @param data   비밀번호 변경 폼 데이터
     * @return {@link Boolean} 수정 성공 여부
     */
    boolean changeUserPassword(Long userId, PasswordUpdateForm data);

    /**
     * 지정된 사용자 비밀번호 재설정
     *
     * @param userId   사용자 ID
     * @param password 재설정 후 비밀번호
     * @return {@link Boolean} 재설정 성공 여부
     */
    boolean resetUserPassword(Long userId, String password);

    /**
     * SMS 인증 코드 발송(휴대폰 번호 바인딩 또는 변경)
     *
     * @param mobile 휴대폰 번호
     * @return {@link Boolean} 발송 성공 여부
     */
    boolean sendMobileCode(String mobile);

    /**
     * 수정 현재 사용자 휴대폰 번호
     *
     * @param data 폼 데이터
     * @return {@link Boolean} 수정 성공 여부
     */
    boolean bindOrChangeMobile(MobileUpdateForm data);

    /**
     * 이메일 인증 코드 발송(이메일 바인딩 또는 변경)
     *
     * @param email 이메일
     */
    void sendEmailCode(String email);

    /**
     * 이메일 바인딩 또는 변경
     *
     * @param data 폼 데이터
     * @return {@link Boolean} 바인딩 성공 여부
     */
    boolean bindOrChangeEmail(EmailUpdateForm data);

    /**
     * 조회 사용자 옵션 목록
     *
     * @return {@link List<Option<String>>} 사용자 옵션 목록
     */
    List<Option<String>> listUserOptions();

    /**
     * openid로 사용자 인증 정보 조회
     *
     * @param openId 사용자명
     * @return {@link UserAuthCredentials}
     */

    UserAuthCredentials getAuthCredentialsByOpenId(String openId);

    /**
     * 위챗 OpenID로 사용자 등록 또는 바인딩
     *
     * @param openId 위챗 OpenID
     */
    boolean registerOrBindWechatUser(String openId);

    /**
     * 휴대폰 번호로 사용자 인증 정보 조회
     *
     * @param mobile 휴대폰 번호
     * @return {@link UserAuthCredentials}
     */
    UserAuthCredentials getAuthCredentialsByMobile(String mobile);

    /**
     * 휴대폰 번호와 OpenID로 사용자 등록
     *
     * @param mobile 휴대폰 번호
     * @param openId 위챗 OpenID
     * @return 성공 여부
     */
    boolean registerUserByMobileAndOpenId(String mobile, String openId);

    /**
     * 사용자 위챗 OpenID 바인딩
     *
     * @param userId 사용자 ID
     * @param openId 위챗 OpenID
     * @return 성공 여부
     */
    boolean bindUserOpenId(Long userId, String openId);

}

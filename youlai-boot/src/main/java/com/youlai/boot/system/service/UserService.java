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
 * 사용자비즈니스인터페이스
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
     * @param userId 사용자ID
     * @return {@link UserForm} 사용자폼데이터
     */
    UserForm getUserFormData(Long userId);


    /**
     * 사용자 추가
     *
     * @param userForm 사용자폼객체
     * @return {@link Boolean} 여부추가성공
     */
    boolean saveUser(UserForm userForm);

    /**
     * 사용자 수정
     *
     * @param userId   사용자ID
     * @param userForm 사용자폼객체
     * @return {@link Boolean} 여부수정성공
     */
    boolean updateUser(Long userId, UserForm userForm);


    /**
     * 사용자 삭제
     *
     * @param idsStr 사용자ID，여러 개는영문쉼표(,)로 구분
     * @return {@link Boolean} 여부삭제성공
     */
    boolean deleteUsers(String idsStr);


    /**
     * 根据사용자명조회인증信息
     *
     * @param username 사용자명
     * @return {@link UserAuthCredentials}
     */

    UserAuthCredentials getAuthCredentialsByUsername(String username);


    /**
     * 조회사용자 내보내기 목록
     *
     * @param queryParams 조회参수
     * @return {@link List<UserExportDTO>} 사용자 내보내기 목록
     */
    List<UserExportDTO> listExportUsers(UserPageQuery queryParams);


    /**
     * 조회로그인사용자 정보
     *
     * @return {@link CurrentUserDTO} 로그인사용자 정보
     */
    CurrentUserDTO getCurrentUserInfo();

    /**
     * 개인센터 사용자 정보 조회
     *
     * @return {@link UserProfileVO} 개인센터사용자 정보
     */
    UserProfileVO getUserProfile(Long userId);

    /**
     * 수정개인센터사용자 정보
     *
     * @param formData 폼데이터
     * @return {@link Boolean} 여부수정성공
     */
    boolean updateUserProfile(UserProfileForm formData);

    /**
     * 수정지정된사용자비밀번호
     *
     * @param userId 사용자ID
     * @param data   비밀번호 변경폼데이터
     * @return {@link Boolean} 여부수정성공
     */
    boolean changeUserPassword(Long userId, PasswordUpdateForm data);

    /**
     * 지정된 사용자 비밀번호 재설정
     *
     * @param userId   사용자ID
     * @param password 재설정후의비밀번호
     * @return {@link Boolean} 여부재설정성공
     */
    boolean resetUserPassword(Long userId, String password);

    /**
     * 발송SMS인증코드(휴대폰 번호 바인딩 또는 변경)
     *
     * @param mobile 휴대폰 번호
     * @return {@link Boolean} 여부발송성공
     */
    boolean sendMobileCode(String mobile);

    /**
     * 수정현재사용자휴대폰 번호
     *
     * @param data 폼데이터
     * @return {@link Boolean} 여부수정성공
     */
    boolean bindOrChangeMobile(MobileUpdateForm data);

    /**
     * 발송이메일인증코드(이메일 바인딩 또는 변경)
     *
     * @param email 이메일
     */
    void sendEmailCode(String email);

    /**
     * 이메일 바인딩 또는 변경
     *
     * @param data 폼데이터
     * @return {@link Boolean} 여부바인딩성공
     */
    boolean bindOrChangeEmail(EmailUpdateForm data);

    /**
     * 조회사용자옵션목록
     *
     * @return {@link List<Option<String>>} 사용자옵션목록
     */
    List<Option<String>> listUserOptions();

    /**
     * 根据 openid 조회사용자인증信息
     *
     * @param openId 사용자명
     * @return {@link UserAuthCredentials}
     */

    UserAuthCredentials getAuthCredentialsByOpenId(String openId);

    /**
     * 根据위챗 OpenID 注册또는바인딩사용자
     *
     * @param openId 위챗 OpenID
     */
    boolean registerOrBindWechatUser(String openId);

    /**
     * 根据휴대폰 번호조회사용자인증信息
     *
     * @param mobile 휴대폰 번호
     * @return {@link UserAuthCredentials}
     */
    UserAuthCredentials getAuthCredentialsByMobile(String mobile);

    /**
     * 根据휴대폰 번호和OpenID注册사용자
     *
     * @param mobile 휴대폰 번호
     * @param openId 위챗OpenID
     * @return 여부성공
     */
    boolean registerUserByMobileAndOpenId(String mobile, String openId);

    /**
     * 바인딩사용자위챗OpenID
     *
     * @param userId 사용자ID
     * @param openId 위챗OpenID
     * @return 여부성공
     */
    boolean bindUserOpenId(Long userId, String openId);

}

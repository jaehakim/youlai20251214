package com.youlai.boot.security.service;

import com.youlai.boot.security.model.SysUserDetails;
import com.youlai.boot.security.model.UserAuthCredentials;
import com.youlai.boot.system.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 시스템사용자인증 DetailsService
 *
 * @author Ray.Hao
 * @since 2021/10/19
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SysUserDetailsService implements UserDetailsService {

    private final UserService userService;

    /**
     * 根据사용자명조회사용자 정보
     *
     * @param username 사용자명
     * @return 사용자 정보
     * @throws UsernameNotFoundException 사용자명미找到오류
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            UserAuthCredentials userAuthCredentials = userService.getAuthCredentialsByUsername(username);
            if (userAuthCredentials == null) {
                throw new UsernameNotFoundException(username);
            }
            return new SysUserDetails(userAuthCredentials);
        } catch (Exception e) {
            // 기록오류로그
            log.error("인증오류:{}", e.getMessage());
            // 抛出오류
            throw e;
        }
    }
}

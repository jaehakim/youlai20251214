package com.youlai.boot.common.util;

import cn.hutool.core.util.StrUtil;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.xdb.Searcher;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * IP 유틸리티 클래스
 * <p>
 * 클라이언트 IP 주소 및 IP 주소에 해당하는 지리적 위치 정보 가져오기
 * <p>
 * Nginx 등의 리버스 프록시 소프트웨어를 사용하는 경우, request.getRemoteAddr()로 IP 주소를 가져올 수 없음
 * 다중 레벨 리버스 프록시를 사용하는 경우, X-Forwarded-For 값은 하나가 아니라 일련의 IP 주소이며, X-Forwarded-For에서 첫 번째 unknown이 아닌 유효한 IP 문자열이 실제 IP 주소임
 * </p>
 *
 * @author Ray
 * @since 2.10.0
 */
@Slf4j
@Component
public class IPUtils {

    private static final String DB_PATH = "/data/ip2region.xdb";
    private static Searcher searcher;

    @PostConstruct
    public void init() {
        try {
            // 클래스 경로에서 리소스 파일 로드
            InputStream inputStream = getClass().getResourceAsStream(DB_PATH);
            if (inputStream == null) {
                throw new FileNotFoundException("Resource not found: " + DB_PATH);
            }

            // 리소스 파일을 임시 파일로 복사
            Path tempDbPath = Files.createTempFile("ip2region", ".xdb");
            Files.copy(inputStream, tempDbPath, StandardCopyOption.REPLACE_EXISTING);

            // 임시 파일을 사용하여 Searcher 객체 초기화
            searcher = Searcher.newWithFileOnly(tempDbPath.toString());
        } catch (Exception e) {
            log.error("IpRegionUtil initialization ERROR, {}", e.getMessage());
        }
    }

    /**
     * IP 주소 가져오기
     *
     * @param request HttpServletRequest 객체
     * @return 클라이언트 IP 주소
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ip = null;
        try {
            if (request == null) {
                return "";
            }
            ip = request.getHeader("x-forwarded-for");
            if (checkIp(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (checkIp(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (checkIp(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
            }
            if (checkIp(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }
            if (checkIp(ip)) {
                ip = request.getRemoteAddr();
                if ("127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip)) {
                    // 네트워크 카드를 통해 로컬 구성 IP 가져오기
                    ip = getLocalAddr();
                }
            }
        } catch (Exception e) {
            log.error("IPUtils ERROR, {}", e.getMessage());
        }

        // 프록시를 사용하는 경우 첫 번째 IP 주소 가져오기
        if (StrUtil.isNotBlank(ip) && ip.indexOf(",") > 0) {
            ip = ip.substring(0, ip.indexOf(","));
        }

        return ip;
    }

    private static boolean checkIp(String ip) {
        String unknown = "unknown";
        return StrUtil.isEmpty(ip) || unknown.equalsIgnoreCase(ip);
    }

    /**
     * 로컬 호스트의 IP 주소 가져오기
     *
     * @return 로컬 호스트 IP 주소
     */
    private static String getLocalAddr() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.error("InetAddress.getLocalHost()-error, {}", e.getMessage());
        }
        return null;
    }

    /**
     * IP 주소를 기반으로 지리적 위치 정보 가져오기
     *
     * @param ip IP 주소
     * @return 지리적 위치 정보
     */
    public static String getRegion(String ip) {
        if (searcher == null) {
            log.error("Searcher is not initialized");
            return null;
        }

        try {
            return searcher.search(ip);
        } catch (Exception e) {
            log.error("IpRegionUtil ERROR, {}", e.getMessage());
            return null;
        }
    }
}

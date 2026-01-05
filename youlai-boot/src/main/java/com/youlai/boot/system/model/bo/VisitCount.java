package com.youlai.boot.system.model.bo;

import lombok.Data;

/**
 * 특정 날짜 방문 통계
 *
 * @author Ray
 * @since 2.10.0
 */
@Data
public class VisitCount {

    /**
     * 날짜 yyyy-MM-dd
     */
    private String createDate;

    /**
     * 방문 횟수
     */
    private Integer count;
}

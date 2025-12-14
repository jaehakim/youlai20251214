package com.youlai.boot.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.boot.system.model.entity.Log;
import com.baomidou.mybatisplus.extension.service.IService;
import com.youlai.boot.system.model.query.LogPageQuery;
import com.youlai.boot.system.model.vo.LogPageVO;
import com.youlai.boot.system.model.vo.VisitStatsVO;
import com.youlai.boot.system.model.vo.VisitTrendVO;

import java.time.LocalDate;
import java.util.List;

/**
 * 시스템로그 서비스인터페이스
 *
 * @author Ray.Hao
 * @since 2.10.0
 */
public interface LogService extends IService<Log> {

    /**
     * 조회로그페이지 목록
     */
    Page<LogPageVO> getLogPage(LogPageQuery queryParams);


    /**
     * 접근 추세 조회
     *
     * @param startDate 시작 시간
     * @param endDate   종료 시간
     */
    VisitTrendVO getVisitTrend(LocalDate startDate, LocalDate endDate);

    /**
     * 접근 통계 조회
     */
    VisitStatsVO getVisitStats();

}

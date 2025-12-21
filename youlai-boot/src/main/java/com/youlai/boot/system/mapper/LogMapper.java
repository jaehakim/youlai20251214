package com.youlai.boot.system.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.boot.system.model.bo.VisitCount;
import com.youlai.boot.system.model.bo.VisitStatsBO;
import com.youlai.boot.system.model.entity.Log;
import com.youlai.boot.system.model.query.LogPageQuery;
import com.youlai.boot.system.model.vo.LogPageVO;
import com.youlai.boot.system.model.vo.VisitStatsVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


/**
 * 시스템 로그 데이터 접근 계층
 *
 * @author Ray
 * @since 2.10.0
 */
@Mapper
public interface LogMapper extends BaseMapper<Log> {

    /**
     * 로그 페이지 목록 가져오기
     */
    Page<LogPageVO> getLogPage(Page<LogPageVO> page, LogPageQuery queryParams);

    /**
     * 페이지뷰(PV) 통계
     *
     * @param startDate 시작 날짜 yyyy-MM-dd
     * @param endDate   종료 날짜 yyyy-MM-dd
     */
    List<VisitCount> getPvCounts(String startDate, String endDate);

    /**
     * IP 수 통계
     *
     * @param startDate 시작 날짜 yyyy-MM-dd
     * @param endDate   종료 날짜 yyyy-MM-dd
     */
    List<VisitCount> getIpCounts(String startDate, String endDate);

    /**
     * 페이지뷰(PV) 통계 가져오기
     */
    VisitStatsBO getPvStats();

    /**
     * 방문 IP 통계 가져오기
     */
    VisitStatsBO getUvStats();
}





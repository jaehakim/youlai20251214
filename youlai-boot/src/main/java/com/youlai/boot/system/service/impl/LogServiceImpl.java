package com.youlai.boot.system.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youlai.boot.system.mapper.LogMapper;
import com.youlai.boot.system.model.bo.VisitCount;
import com.youlai.boot.system.model.bo.VisitStatsBO;
import com.youlai.boot.system.model.entity.Log;
import com.youlai.boot.system.model.query.LogPageQuery;
import com.youlai.boot.system.model.vo.LogPageVO;
import com.youlai.boot.system.model.vo.VisitStatsVO;
import com.youlai.boot.system.model.vo.VisitTrendVO;
import com.youlai.boot.system.service.LogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 시스템 로그 서비스 구현 클래스
 *
 * @author Ray.Hao
 * @since 2.10.0
 */
@Service
@Slf4j
public class LogServiceImpl extends ServiceImpl<LogMapper, Log>
        implements LogService {

    /**
     * 로그 페이지 리스트 가져오기
     *
     * @param queryParams 조회 파라미터
     * @return 로그 페이지 리스트
     */
    @Override
    public Page<LogPageVO> getLogPage(LogPageQuery queryParams) {
        // 파라미터 유효성 검사 및 보정
        queryParams.validateAndCorrect();

        // 정렬 파라미터 유효성 검사
        if (queryParams.hasSortCondition()) {
            if (!queryParams.isValidSortField(queryParams.getSortBy())) {
                log.warn("유효하지 않은 정렬 필드: {}", queryParams.getSortBy());
                queryParams.clearSort(); // 잘못된 정렬 조건 제거
            }
        }

        // 페이지 객체 생성 및 정렬 조건 설정
        Page<LogPageVO> page = queryParams.buildPage();

        // 매퍼를 통한 데이터 조회
        return this.baseMapper.getLogPage(page, queryParams);
    }

    /**
     * 방문 트렌드 가져오기
     *
     * @param startDate 시작 날짜
     * @param endDate   종료 날짜
     * @return
     */
    @Override
    public VisitTrendVO getVisitTrend(LocalDate startDate, LocalDate endDate) {
        VisitTrendVO visitTrend = new VisitTrendVO();
        List<String> dates = new ArrayList<>();

        // 날짜 범위 내의 날짜 가져오기
        while (!startDate.isAfter(endDate)) {
            dates.add(startDate.toString());
            startDate = startDate.plusDays(1);
        }
        visitTrend.setDates(dates);

        // 방문량 및 방문 IP 수 통계 데이터 가져오기
        List<VisitCount> pvCounts = this.baseMapper.getPvCounts(dates.get(0) + " 00:00:00", dates.get(dates.size() - 1) + " 23:59:59");
        List<VisitCount> ipCounts = this.baseMapper.getIpCounts(dates.get(0) + " 00:00:00", dates.get(dates.size() - 1) + " 23:59:59");

        // 통계 데이터를 Map으로 변환
        //Map<String, Integer> pvMap = pvCounts.stream().collect(Collectors.toMap(VisitCount::getDate, VisitCount::getCount));
        //Map<String, Integer> ipMap = ipCounts.stream().collect(Collectors.toMap(VisitCount::getDate, VisitCount::getCount));

        // 날짜와 방문량/방문 IP 수 매칭
        List<Integer> pvList = new ArrayList<>();
        List<Integer> ipList = new ArrayList<>();

        for (String date : dates) {
           // pvList.add(pvMap.getOrDefault(date, 0));
           // ipList.add(ipMap.getOrDefault(date, 0));
        }

        visitTrend.setPvList(pvList);
        visitTrend.setIpList(ipList);

        return visitTrend;
    }

    /**
     * 방문량 통계
     */
    @Override
    public VisitStatsVO getVisitStats() {
        VisitStatsVO result = new VisitStatsVO();

        // 방문자 수 통계(UV)
        VisitStatsBO uvStats = this.baseMapper.getUvStats();
        if(uvStats!=null){
            result.setTodayUvCount(uvStats.getTodayCount());
            result.setTotalUvCount(uvStats.getTotalCount());
            result.setUvGrowthRate(uvStats.getGrowthRate());
        }

        // 페이지 뷰 통계(PV)
        VisitStatsBO pvStats = this.baseMapper.getPvStats();
        if(pvStats!=null){
            result.setTodayPvCount(pvStats.getTodayCount());
            result.setTotalPvCount(pvStats.getTotalCount());
            result.setPvGrowthRate(pvStats.getGrowthRate());
        }

        return result;
    }

}





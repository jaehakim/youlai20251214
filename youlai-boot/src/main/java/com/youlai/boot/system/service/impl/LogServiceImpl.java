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
public class LogServiceImpl extends ServiceImpl<LogMapper, Log>
        implements LogService {

    /**
     * 조회로그페이지 목록
     *
     * @param queryParams 조회参수
     * @return 로그페이지 목록
     */
    @Override
    public Page<LogPageVO> getLogPage(LogPageQuery queryParams) {
        return this.baseMapper.getLogPage(new Page<>(queryParams.getPageNum(), queryParams.getPageSize()),
                queryParams);
    }

    /**
     * 접근 추세 조회
     *
     * @param startDate 시작 시간
     * @param endDate   종료 시간
     * @return
     */
    @Override
    public VisitTrendVO getVisitTrend(LocalDate startDate, LocalDate endDate) {
        VisitTrendVO visitTrend = new VisitTrendVO();
        List<String> dates = new ArrayList<>();

        // 조회日期范围内의日期
        while (!startDate.isAfter(endDate)) {
            dates.add(startDate.toString());
            startDate = startDate.plusDays(1);
        }
        visitTrend.setDates(dates);

        // 조회접근量和접근 IP 수의통계데이터
        List<VisitCount> pvCounts = this.baseMapper.getPvCounts(dates.get(0) + " 00:00:00", dates.get(dates.size() - 1) + " 23:59:59");
        List<VisitCount> ipCounts = this.baseMapper.getIpCounts(dates.get(0) + " 00:00:00", dates.get(dates.size() - 1) + " 23:59:59");

        // 을통계데이터转换값 Map
        Map<String, Integer> pvMap = pvCounts.stream().collect(Collectors.toMap(VisitCount::getDate, VisitCount::getCount));
        Map<String, Integer> ipMap = ipCounts.stream().collect(Collectors.toMap(VisitCount::getDate, VisitCount::getCount));

        // 匹配日期和접근量/접근 IP 수
        List<Integer> pvList = new ArrayList<>();
        List<Integer> ipList = new ArrayList<>();

        for (String date : dates) {
            pvList.add(pvMap.getOrDefault(date, 0));
            ipList.add(ipMap.getOrDefault(date, 0));
        }

        visitTrend.setPvList(pvList);
        visitTrend.setIpList(ipList);

        return visitTrend;
    }

    /**
     * 접근量통계
     */
    @Override
    public VisitStatsVO getVisitStats() {
        VisitStatsVO result = new VisitStatsVO();

        // 访客수통계(UV)
        VisitStatsBO uvStats = this.baseMapper.getUvStats();
        if(uvStats!=null){
            result.setTodayUvCount(uvStats.getTodayCount());
            result.setTotalUvCount(uvStats.getTotalCount());
            result.setUvGrowthRate(uvStats.getGrowthRate());
        }

        // 浏览量통계(PV)
        VisitStatsBO pvStats = this.baseMapper.getPvStats();
        if(pvStats!=null){
            result.setTodayPvCount(pvStats.getTodayCount());
            result.setTotalPvCount(pvStats.getTotalCount());
            result.setPvGrowthRate(pvStats.getGrowthRate());
        }

        return result;
    }

}





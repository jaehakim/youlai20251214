package com.youlai.boot.platform.ai.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.youlai.boot.platform.ai.model.entity.AiCommandRecord;
import com.youlai.boot.platform.ai.model.query.AiCommandPageQuery;
import com.youlai.boot.platform.ai.model.vo.AiCommandRecordVO;

/**
 * AI 명령 기록 서비스 인터페이스
 */
public interface AiCommandRecordService extends IService<AiCommandRecord> {

    /**
     * 명령 기록 페이지 목록 조회
     *
     * @param queryParams 조회参수
     * @return 명령 기록 페이지 목록
     */
    IPage<AiCommandRecordVO> getRecordPage(AiCommandPageQuery queryParams);

    /**
     * 명령 실행 취소
     *
     * @param recordId 기록 ID
     */
    void rollbackCommand(String recordId);
}



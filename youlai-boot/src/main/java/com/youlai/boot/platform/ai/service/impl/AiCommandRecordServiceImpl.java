package com.youlai.boot.platform.ai.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youlai.boot.platform.ai.mapper.AiCommandRecordMapper;
import com.youlai.boot.platform.ai.model.entity.AiCommandRecord;
import com.youlai.boot.platform.ai.model.query.AiCommandPageQuery;
import com.youlai.boot.platform.ai.model.vo.AiCommandRecordVO;
import com.youlai.boot.platform.ai.service.AiCommandRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * AI 명령 기록서비스구현类
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AiCommandRecordServiceImpl extends ServiceImpl<AiCommandRecordMapper, AiCommandRecord>
        implements AiCommandRecordService {

    @Override
    public IPage<AiCommandRecordVO> getRecordPage(AiCommandPageQuery queryParams) {
        Page<AiCommandRecordVO> page = new Page<>(queryParams.getPageNum(), queryParams.getPageSize());
        return this.baseMapper.getRecordPage(page, queryParams);
    }

    @Override
    public void rollbackCommand(String recordId) {
        AiCommandRecord record = this.getById(recordId);
        if (record == null) {
            throw new RuntimeException("명령 기록不存에");
        }

        if (!"success".equals(record.getExecuteStatus())) {
            throw new RuntimeException("만能撤销성공执行의명령");
        }

        // TODO: 구현具体의回滚逻辑
        log.info("명령 실행 취소: recordId={}, function={}", recordId, record.getFunctionName());
        throw new UnsupportedOperationException("回滚功能尚미구현");
    }
}



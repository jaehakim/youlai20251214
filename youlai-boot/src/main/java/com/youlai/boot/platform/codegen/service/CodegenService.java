package com.youlai.boot.platform.codegen.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.boot.platform.codegen.model.query.TablePageQuery;
import com.youlai.boot.platform.codegen.model.vo.CodegenPreviewVO;
import com.youlai.boot.platform.codegen.model.vo.TablePageVO;

import java.util.List;

/**
 * 코드 생성 설정 인터페이스
 *
 * @author Ray
 * @since 2.10.0
 */
public interface CodegenService {

    /**
     * 데이터 테이블 페이지 목록 조회
     *
     * @param queryParams 조회파라미터수
     * @return
     */
    Page<TablePageVO> getTablePage(TablePageQuery queryParams);

    /**
     * 조회미리보기생성코드
     *
     * @param tableName 테이블명
     * @return
     */
    List<CodegenPreviewVO> getCodegenPreviewData(String tableName, String pageType);

    /**
     * 코드 다운로드
     * @param tableNames 테이블명
     * @return
     */
    byte[] downloadCode(String[] tableNames, String pageType);
}

package com.youlai.boot.platform.codegen.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.boot.platform.codegen.model.bo.ColumnMetaData;
import com.youlai.boot.platform.codegen.model.bo.TableMetaData;
import com.youlai.boot.platform.codegen.model.query.TablePageQuery;
import com.youlai.boot.platform.codegen.model.vo.TablePageVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


/**
 * 데이터베이스 매핑 계층
 *
 * @author Ray
 * @since 2.9.0
 */
@Mapper
public interface DatabaseMapper extends BaseMapper {

    /**
     * 테이블 페이지 목록 조회
     *
     * @param page
     * @param queryParams
     * @return
     */
    Page<TablePageVO> getTablePage(Page<TablePageVO> page, TablePageQuery queryParams);

    /**
     * 테이블 필드 목록 조회
     *
     * @param tableName
     * @return
     */
    List<ColumnMetaData> getTableColumns(String tableName);

    /**
     * 테이블 메타데이터 조회
     *
     * @param tableName
     * @return
     */
    TableMetaData getTableMetadata(String tableName);
}

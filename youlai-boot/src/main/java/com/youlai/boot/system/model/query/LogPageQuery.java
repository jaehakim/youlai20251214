package com.youlai.boot.system.model.query;

import com.youlai.boot.common.base.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

/**
 * 로그 페이지 조회 객체
 *
 * @author Ray
 * @since 2.10.0
 */
@Schema(description = "로그 페이지 조회 객체")
@Getter
@Setter
public class LogPageQuery extends BasePageQuery {

    @Schema(description="키워드(로그 내용/요청 경로/요청 메서드/지역/브라우저/운영체제)")
    private String keywords;

    @Schema(description="작업 시간 범위")
    List<String> createTime;

}

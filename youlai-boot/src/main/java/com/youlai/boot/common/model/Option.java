package com.youlai.boot.common.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 드롭다운 옵션 객체
 *
 * @author haoxr
 * @since 2022/1/22
 */
@Schema(description ="드롭다운 옵션 객체")
@Data
@NoArgsConstructor
public class Option<T> {

    public Option(T value, String label) {
        this.value = value;
        this.label = label;
    }

    public Option(T value, String label, List<Option<T>> children) {
        this.value = value;
        this.label = label;
        this.children= children;
    }

    public Option(T value, String label, String tag) {
        this.value = value;
        this.label = label;
        this.tag= tag;
    }


    @Schema(description="옵션 값")
    private T value;

    @Schema(description="옵션 라벨")
    private String label;

    @Schema(description = "라벨 타입")
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    private String tag;

    @Schema(description="하위 옵션 목록")
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    private List<Option<T>> children;

}
package com.youlai.boot.system.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serial;
import java.io.Serializable;

/**
 * 시스템 설정 뷰 객체
 *
 * @author Theo
 * @since 2024-07-30 14:49
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = false)
@Schema(description = "시스템 설정 VO")
public class ConfigVO {

    @Schema(description = "기본키")
    private Long id;

    @Schema(description = "설정명")
    private String configName;

    @Schema(description = "설정 키")
    private String configKey;

    @Schema(description = "설정값")
    private String configValue;

    @Schema(description = "설명, 비고")
    private String remark;
}

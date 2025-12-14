package com.youlai.boot.system.model.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 시스템 설정 폼 엔티티
 *
 * @author Theo
 * @since 2024-07-29 11:17:26
 */
@Data
@Schema(description = "시스템 설정 폼 엔티티")
public class ConfigForm implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "기본 키")
    private Long id;

    @NotBlank(message = "설정명은 비어있을 수 없습니다")
    @Schema(description = "설정명")
    private String configName;

    @NotBlank(message = "설정 키는 비어있을 수 없습니다")
    @Schema(description = "설정 키")
    private String configKey;

    @NotBlank(message = "설정값은 비어있을 수 없습니다")
    @Schema(description = "설정값")
    private String configValue;

    @Schema(description = "설명, 비고")
    private String remark;
}

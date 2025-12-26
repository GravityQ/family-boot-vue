package cn.iocoder.yudao.module.family.controller.app.family.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 家族更新 Request VO
 *
 * @author 芋道源码
 */
@Schema(description = "家族更新 Request VO")
@Data
public class FamilyUpdateReqVO {

    @Schema(description = "家族编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "家族编号不能为空")
    private Long id;

    @Schema(description = "封面图URL", example = "https://example.com/cover.jpg")
    private String coverUrl;

    @Schema(description = "家族简介/家训", example = "传承家族文化")
    private String desc;

    @Schema(description = "隐私等级", example = "0")
    private Integer privacyLevel;

}


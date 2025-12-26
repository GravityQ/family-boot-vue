package cn.iocoder.yudao.module.family.controller.admin.family.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 家族审核 Request VO
 *
 * @author 芋道源码
 */
@Schema(description = "家族审核 Request VO")
@Data
public class FamilyAuditReqVO {

    @Schema(description = "家族编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "家族编号不能为空")
    private Long id;

    @Schema(description = "驳回原因（驳回时必填）", example = "家族名称不符合规范")
    private String rejectReason;

}


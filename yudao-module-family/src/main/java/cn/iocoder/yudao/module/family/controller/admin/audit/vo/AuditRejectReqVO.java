package cn.iocoder.yudao.module.family.controller.admin.audit.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 审核驳回 Request VO
 *
 * @author 芋道源码
 */
@Schema(description = "审核驳回 Request VO")
@Data
public class AuditRejectReqVO {

    @Schema(description = "审核记录ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "审核记录ID不能为空")
    private Long id;

    @Schema(description = "驳回原因", requiredMode = Schema.RequiredMode.REQUIRED, example = "信息不完整")
    @NotNull(message = "驳回原因不能为空")
    private String rejectReason;

}


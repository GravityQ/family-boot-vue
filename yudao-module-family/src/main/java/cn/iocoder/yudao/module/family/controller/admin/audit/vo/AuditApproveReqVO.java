package cn.iocoder.yudao.module.family.controller.admin.audit.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 审核通过 Request VO
 *
 * @author 芋道源码
 */
@Schema(description = "审核通过 Request VO")
@Data
public class AuditApproveReqVO {

    @Schema(description = "审核记录ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "审核记录ID不能为空")
    private Long id;

}


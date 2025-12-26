package cn.iocoder.yudao.module.family.controller.admin.audit.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 审核记录分页查询 Request VO
 *
 * @author 芋道源码
 */
@Schema(description = "审核记录分页查询 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AuditPageReqVO extends PageParam {

    @Schema(description = "家族编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "家族编号不能为空")
    private Long familyId;

    @Schema(description = "审核状态", example = "0")
    private Integer auditStatus;

}


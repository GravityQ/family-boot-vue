package cn.iocoder.yudao.module.family.controller.admin.audit.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 审核记录 Response VO
 *
 * @author 芋道源码
 */
@Schema(description = "审核记录 Response VO")
@Data
public class AuditRespVO {

    @Schema(description = "审核记录ID", example = "1")
    private Long id;
    @Schema(description = "家族编号", example = "1")
    private Long familyId;
    @Schema(description = "申请人ID", example = "1")
    private Long applicantId;
    @Schema(description = "实体类型", example = "MEMBER")
    private String entityType;
    @Schema(description = "关联实体ID", example = "1")
    private Long entityId;
    @Schema(description = "操作类型", example = "CREATE")
    private String operationType;
    @Schema(description = "修改前数据快照")
    private String oldJson;
    @Schema(description = "期望修改后数据")
    private String newJson;
    @Schema(description = "审核状态", example = "0")
    private Integer auditStatus;
    @Schema(description = "驳回原因")
    private String auditComment;
    @Schema(description = "审核人ID", example = "1")
    private Long auditorId;
    @Schema(description = "审核时间")
    private LocalDateTime auditTime;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}


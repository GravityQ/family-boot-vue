package cn.iocoder.yudao.module.family.dal.dataobject.audit;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.module.family.enums.AuditEntityTypeEnum;
import cn.iocoder.yudao.module.family.enums.AuditOperationTypeEnum;
import cn.iocoder.yudao.module.family.enums.AuditStatusEnum;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 审核记录 DO
 *
 * @author 芋道源码
 */
@TableName("audit_log")
@KeySequence("audit_log_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogDO extends BaseDO {

    /**
     * 主键ID
     */
    @TableId
    private Long id;
    /**
     * 所属家族ID
     */
    private Long familyId;
    /**
     * 申请人ID
     */
    private Long applicantId;
    /**
     * 实体类型
     *
     * 枚举 {@link AuditEntityTypeEnum}
     */
    private String entityType;
    /**
     * 关联实体ID(新增时为空)
     */
    private Long entityId;
    /**
     * 操作类型
     *
     * 枚举 {@link AuditOperationTypeEnum}
     */
    private String operationType;
    /**
     * 修改前数据快照
     */
    private String oldJson;
    /**
     * 期望修改后数据
     */
    private String newJson;
    /**
     * 审核状态
     *
     * 枚举 {@link AuditStatusEnum}
     */
    private Integer auditStatus;
    /**
     * 驳回原因
     */
    private String auditComment;
    /**
     * 审核人ID
     */
    private Long auditorId;
    /**
     * 审核时间
     */
    private LocalDateTime auditTime;

}


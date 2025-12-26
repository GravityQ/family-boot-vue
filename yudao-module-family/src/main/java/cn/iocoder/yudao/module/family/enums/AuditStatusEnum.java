package cn.iocoder.yudao.module.family.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 审核状态枚举
 *
 * @author 芋道源码
 */
@Getter
@AllArgsConstructor
public enum AuditStatusEnum {

    PENDING(0, "待审核"),
    APPROVED(1, "已通过"),
    REJECTED(2, "已驳回");

    /**
     * 审核状态值
     */
    private final Integer status;
    /**
     * 审核状态名称
     */
    private final String name;

}


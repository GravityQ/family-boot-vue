package cn.iocoder.yudao.module.family.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 审核实体类型枚举
 *
 * @author 芋道源码
 */
@Getter
@AllArgsConstructor
public enum AuditEntityTypeEnum {

    MEMBER("MEMBER", "成员"),
    FAMILY("FAMILY", "家族");

    /**
     * 实体类型值
     */
    private final String type;
    /**
     * 实体类型名称
     */
    private final String name;

}


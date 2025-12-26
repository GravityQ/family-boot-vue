package cn.iocoder.yudao.module.family.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 家庭单元状态枚举
 *
 * @author 芋道源码
 */
@Getter
@AllArgsConstructor
public enum FamilyUnitStatusEnum {

    MARRIED(1, "婚姻中"),
    DIVORCED(0, "离异"),
    WIDOWED(2, "丧偶");

    /**
     * 状态值
     */
    private final Integer status;
    /**
     * 状态名称
     */
    private final String name;

}


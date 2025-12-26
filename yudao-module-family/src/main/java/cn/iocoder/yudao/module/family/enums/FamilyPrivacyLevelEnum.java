package cn.iocoder.yudao.module.family.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 家族隐私等级枚举
 *
 * @author 芋道源码
 */
@Getter
@AllArgsConstructor
public enum FamilyPrivacyLevelEnum {

    PUBLIC(0, "公开"),
    MEMBERS_ONLY(1, "仅族人可见");

    /**
     * 隐私等级值
     */
    private final Integer level;
    /**
     * 隐私等级名称
     */
    private final String name;

}


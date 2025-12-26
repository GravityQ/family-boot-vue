package cn.iocoder.yudao.module.family.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 家族状态枚举
 *
 * @author 芋道源码
 */
@Getter
@AllArgsConstructor
public enum FamilyStatusEnum {

    PENDING(0, "待审核"),
    APPROVED(1, "已生效"),
    REJECTED(2, "已拒绝"),
    DELETED(3, "已删除");

    /**
     * 状态值
     */
    private final Integer status;
    /**
     * 状态名称
     */
    private final String name;

}


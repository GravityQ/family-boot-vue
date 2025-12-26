package cn.iocoder.yudao.module.family.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 审核操作类型枚举
 *
 * @author 芋道源码
 */
@Getter
@AllArgsConstructor
public enum AuditOperationTypeEnum {

    CREATE("CREATE", "新增"),
    UPDATE("UPDATE", "修改"),
    DELETE("DELETE", "删除");

    /**
     * 操作类型值
     */
    private final String type;
    /**
     * 操作类型名称
     */
    private final String name;

}


package cn.iocoder.yudao.module.family.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

/**
 * Family 错误码枚举类
 *
 * family 系统，使用 1-010-000-000 段
 */
public interface ErrorCodeConstants {

    // ========== 家族模块 1-010-000-000 ==========
    ErrorCode FAMILY_NOT_EXISTS = new ErrorCode(1_010_000_000, "家族不存在");
    ErrorCode FAMILY_NAME_DUPLICATE = new ErrorCode(1_010_000_001, "家族名称已存在");
    ErrorCode FAMILY_STATUS_NOT_PENDING = new ErrorCode(1_010_000_002, "家族状态不是待审核状态");
    ErrorCode FAMILY_NOT_ADMIN = new ErrorCode(1_010_000_003, "当前用户不是该家族的家族管理员");
    ErrorCode FAMILY_NOT_MEMBER = new ErrorCode(1_010_000_004, "当前用户不是该家族的成员");

    // ========== 成员模块 1-010-001-000 ==========
    ErrorCode MEMBER_NOT_EXISTS = new ErrorCode(1_010_001_000, "成员不存在");
    ErrorCode MEMBER_HAS_CHILDREN = new ErrorCode(1_010_001_001, "成员存在子女，无法删除");
    ErrorCode MEMBER_NOT_IN_FAMILY = new ErrorCode(1_010_001_002, "成员不属于该家族");

    // ========== 审核模块 1-010-002-000 ==========
    ErrorCode AUDIT_LOG_NOT_EXISTS = new ErrorCode(1_010_002_000, "审核记录不存在");
    ErrorCode AUDIT_LOG_STATUS_NOT_PENDING = new ErrorCode(1_010_002_001, "审核记录状态不是待审核状态");
    ErrorCode AUDIT_LOG_NOT_FAMILY_ADMIN = new ErrorCode(1_010_002_002, "当前用户不是该家族的家族管理员，无法审核");

}


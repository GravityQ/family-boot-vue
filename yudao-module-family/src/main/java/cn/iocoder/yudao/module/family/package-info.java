/**
 * family 模块，我们放家族业务，提供家族管理能力。
 * 例如说：家族创建、成员管理、审核流程等等
 *
 * 1. Controller URL：以 /family 开头，避免和其它 Module 冲突
 *    - admin 包下的 Controller：最终路径为 /admin-api/family/xxx（由 YudaoWebAutoConfiguration 自动添加 /admin-api 前缀）
 *    - app 包下的 Controller：最终路径为 /app-api/family/xxx（由 YudaoWebAutoConfiguration 自动添加 /app-api 前缀）
 * 2. DataObject 表名：以 family_ 开头，方便在数据库中区分
 *    - 家族表：family_family
 *    - 成员表：family_member
 *    - 审核表：family_audit_log
 *    - 家庭单元表：family_family_unit
 *    - 家族成员关联表：family_family_member
 */
package cn.iocoder.yudao.module.family;


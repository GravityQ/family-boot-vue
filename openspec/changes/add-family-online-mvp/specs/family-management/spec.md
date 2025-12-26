## ADDED Requirements

### Requirement: Family create application lifecycle
系统 MUST 支持用户提交“创建家族申请”，并进入“待系统管理员审核”的生命周期管理。

#### Scenario: Submit family create application successfully
- **GIVEN** 当前不存在同名家族
- **WHEN** 登录用户提交家族名称、主要姓氏、祖籍（可选）、简介（可选）、封面（可选）
- **THEN** 系统创建家族记录为待审状态，并返回申请已提交的结果

#### Scenario: Reject duplicate family name
- **GIVEN** 系统已存在同名家族
- **WHEN** 登录用户提交创建家族申请
- **THEN** 系统 MUST 拒绝并返回“名称已存在”的业务错误

### Requirement: System admin review family create application
系统 MUST 支持系统管理员对家族创建申请执行审核（通过/驳回），并记录审核人、审核时间与驳回原因（如有）。

#### Scenario: Approve family create application
- **GIVEN** 系统管理员拥有审核权限，且存在一条待审核的家族创建申请
- **WHEN** 系统管理员执行“通过”
- **THEN** 家族状态变为已生效，并将申请人赋予该家族的“家族管理员”权限

#### Scenario: Reject family create application with reason
- **GIVEN** 系统管理员拥有审核权限，且存在一条待审核的家族创建申请
- **WHEN** 系统管理员执行“驳回”并填写原因
- **THEN** 家族状态变为已拒绝，并记录驳回原因

### Requirement: Family settings update by family admin
系统 MUST 允许家族管理员更新家族基础信息（封面、简介、隐私等级），并仅对其管理的家族生效。

#### Scenario: Family admin updates family profile
- **GIVEN** 登录用户为目标家族的家族管理员
- **WHEN** 用户更新封面或简介或隐私等级
- **THEN** 系统保存更新并返回更新成功

#### Scenario: Non-admin cannot update family settings
- **GIVEN** 登录用户不是目标家族的家族管理员
- **WHEN** 用户尝试更新该家族设置
- **THEN** 系统 MUST 拒绝该请求（无权限）



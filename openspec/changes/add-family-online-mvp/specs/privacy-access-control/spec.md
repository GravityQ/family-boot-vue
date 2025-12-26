## ADDED Requirements

### Requirement: RBAC roles and access boundaries for family domain
系统 MUST 在家人在线业务域内支持并区分以下角色的核心权限边界：普通用户、家族管理员、系统管理员。

#### Scenario: System admin can review family create applications
- **GIVEN** 登录用户为系统管理员
- **WHEN** 用户访问家族创建审核相关接口
- **THEN** 系统允许访问并执行通过/驳回

#### Scenario: Family admin can manage only owned family
- **GIVEN** 登录用户为某家族的家族管理员
- **WHEN** 用户访问成员管理/审核工作台等管理接口并携带 familyId
- **THEN** 系统仅允许访问其管理的家族数据

#### Scenario: Normal user cannot bypass audit
- **GIVEN** 登录用户为普通用户
- **WHEN** 用户尝试调用“免审核新增成员”或“直接修改成员”的管理接口
- **THEN** 系统 MUST 拒绝请求（无权限）

### Requirement: Privacy level affects visibility for non-members
系统 MUST 根据家族隐私等级与“是否加入家族”决定响应内容的可见性，并避免通过 ID 直链泄露敏感信息。

#### Scenario: Non-member gets limited family detail
- **GIVEN** 登录用户未加入目标家族
- **WHEN** 用户请求家族详情
- **THEN** 系统返回受限字段集合（不包含敏感字段），并标识 isMember=false

#### Scenario: Non-member cannot access member detail with sensitive fields
- **GIVEN** 登录用户未加入目标家族
- **WHEN** 用户通过成员 id 直链访问成员详情
- **THEN** 系统 MUST 拒绝或返回脱敏后的成员信息（不得返回敏感字段）



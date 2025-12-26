## 1. Preparation(准备)
- [ ] 1.1 确认模块落点与目录结构：是否新增 `yudao-module-family`（参考 `document/2_技术架构与数据库设计_TDD.md`）或复用现有模块分包（验证：评审结论记录在 `design.md`）
- [ ] 1.2 梳理接口前缀与控制器分层：`/app-api/family/v1` 与 `/admin-api/family`（验证：接口清单在 Spec 中可追溯）
- [ ] 1.3 明确 RBAC 边界：普通用户/家族管理员/系统管理员（验证：权限校验点在 Spec 场景中覆盖）

## 2. Schema & DO(数据模型)
- [ ] 2.1 定义并落库 `family` 表（含状态、隐私等级、创建者等字段）（验证：提供 MySQL 脚本 + 单测/集成测试准备）
- [ ] 2.2 定义并落库 `member` 表（含父母冗余、归属家庭单元字段）（验证同上）
- [ ] 2.3 定义并落库 `family_unit` 表（支持多配偶/离异/丧偶状态）（验证同上）
- [ ] 2.4 定义并落库 `audit_log` 表（申请制变更的 old/new 快照、审核状态、审核人信息）（验证同上）

## 3. App API(小程序/移动端接口)
- [ ] 3.1 `POST /app-api/family/v1/family/create` 创建家族申请（验证：单测覆盖“名称唯一/入参校验/待审状态”）
- [ ] 3.2 `GET /app-api/family/v1/family/get` 获取家族详情（含未加入时脱敏逻辑）（验证：单测覆盖“成员/非成员”）
- [ ] 3.3 `GET /app-api/family/v1/member/tree` 获取树谱（MVP 可先返回扁平结构，或返回树结构；以设计决策为准）（验证：单测覆盖“root/children/spouse”）
- [ ] 3.4 `GET /app-api/family/v1/member/get` 获取成员详情（验证：单测覆盖“关系聚合与脱敏”）
- [ ] 3.5 `POST /app-api/family/v1/member/apply-create` 申请新增成员（验证：单测覆盖“非管理员生成审核记录，不写正式表”）
- [ ] 3.6 `POST /app-api/family/v1/audit/apply-update` 提交纠错/修改（验证：单测覆盖“只允许提交变更字段、落 audit_log”）

## 4. Admin API(管理端接口)
- [ ] 4.1 `GET /admin-api/family/audit/page` 待审核分页查询（验证：单测覆盖“仅本家族管理员可见”）
- [ ] 4.2 `PUT /admin-api/family/audit/approve` 审核通过（验证：单测覆盖“写正式表 + 状态变更 + 审核人/时间”）
- [ ] 4.3 `PUT /admin-api/family/audit/reject` 审核驳回（验证：单测覆盖“驳回原因落库、不可修改正式表”）
- [ ] 4.4 `POST /admin-api/family/member/create` 管理员免审新增成员（验证：单测覆盖“管理员直写正式表”）
- [ ] 4.5 `DELETE /admin-api/family/member/delete` 删除成员（MVP：默认禁止删除有子女成员）（验证：单测覆盖“有子女禁止”）

## 5. Security & Privacy(安全与隐私)
- [ ] 5.1 权限与数据范围：系统管理员审核家族创建；家族管理员管理本家族；普通用户仅可申请制（验证：单测覆盖主要分支）
- [ ] 5.2 未加入家族的脱敏展示（MVP：在家族详情/成员详情至少做到“敏感字段不返回”）（验证：单测覆盖字段级脱敏）

## 6. Validation(校验)
- [ ] 6.1 运行 `openspec validate add-family-online-mvp --strict`（验证：严格模式通过）
- [ ] 6.2 预留实现阶段验证：模块单测 `mvn test`（验证：测试用例列表在实现阶段补齐）



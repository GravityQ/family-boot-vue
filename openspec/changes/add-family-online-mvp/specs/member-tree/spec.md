## ADDED Requirements

### Requirement: Member tree retrieval for a family
系统 MUST 提供按家族获取树谱数据的能力，用于前端渲染吊线图/架构图。

#### Scenario: Get member tree as a flat node list
- **GIVEN** 登录用户已加入该家族
- **WHEN** 用户请求获取该家族树谱
- **THEN** 系统返回包含 rootId 与成员节点列表（扁平结构），每个节点包含必要的关系引用字段（父母、子女、配偶/家庭单元标识）

#### Scenario: Non-member cannot access full tree
- **GIVEN** 登录用户未加入该家族
- **WHEN** 用户请求获取该家族树谱
- **THEN** 系统 MUST 拒绝请求或返回脱敏/受限结果（具体策略由隐私 capability 约束）

### Requirement: Member detail with relations
系统 MUST 提供成员详情查询，并在响应中包含直系亲属导航信息（父母、配偶、子女）。

#### Scenario: Get member detail with relations
- **GIVEN** 登录用户已加入该成员所属家族
- **WHEN** 用户请求成员详情
- **THEN** 系统返回成员基础信息与 relations（father/mother/spouses/children）

### Requirement: FamilyUnit supports multiple spouses
系统 MUST 支持以“家庭单元（FamilyUnit）”表示一段婚姻/伴侣关系，从而支持多配偶与离异/丧偶等状态标记。

#### Scenario: Member has multiple spouses with ordering
- **GIVEN** 某成员存在多段家庭单元记录
- **WHEN** 用户请求成员详情或树谱
- **THEN** 系统在 spouses 中返回多配偶列表，并包含排序信息以区分原配/续弦



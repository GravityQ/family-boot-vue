## ADDED Requirements

### Requirement: Associate spouse across families with privacy constraints
系统 MUST 支持在录入配偶时按 FamilyID 或 MemberID 搜索并进行跨家族关联，同时严格遵循对方家族隐私策略。

#### Scenario: Bind spouse by member id when visible
- **GIVEN** 目标配偶成员存在且对当前用户可见（满足隐私策略）
- **WHEN** 用户使用 MemberID 搜索并选择绑定
- **THEN** 系统建立跨家族关联关系，并在树谱/详情中展示可跳转入口（仅在允许跳转时）

#### Scenario: Private family does not leak existence
- **GIVEN** 用户输入的 FamilyID/MemberID 指向一个 PRIVATE 家族或不可见对象
- **WHEN** 用户尝试搜索绑定
- **THEN** 系统 MUST 返回统一的“未找到/不可用”结果，不得泄露其存在性或任何详情



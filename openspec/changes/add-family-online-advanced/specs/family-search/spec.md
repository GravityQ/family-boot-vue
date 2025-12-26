## ADDED Requirements

### Requirement: Family search with privacy-preserving results
系统 MUST 提供家族搜索分页能力，并根据“是否加入家族/隐私等级”返回符合脱敏策略的结果集。

#### Scenario: Search families by surname and location
- **GIVEN** 用户发起搜索请求并提供 surname 与 location 条件
- **WHEN** 系统执行分页查询
- **THEN** 系统返回匹配家族的分页列表

#### Scenario: Non-member sees only overview fields
- **GIVEN** 当前用户未加入目标家族
- **WHEN** 用户在搜索结果中查看该家族条目
- **THEN** 系统返回的条目 MUST 仅包含概况字段（例如 id/name/location/memberCount/coverUrl），不得包含树谱、成员详情或敏感字段

### Requirement: Access to family detail after search respects privacy rules
系统 MUST 确保通过搜索获得的家族 id 不能用于绕过隐私规则访问完整信息。

#### Scenario: Non-member cannot use id to access full tree
- **GIVEN** 用户未加入目标家族
- **WHEN** 用户使用搜索返回的 familyId 访问树谱接口
- **THEN** 系统 MUST 按隐私策略拒绝或返回受限结果



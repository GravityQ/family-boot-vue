## ADDED Requirements

### Requirement: Default tree view loads only limited generations for large families
当家族成员规模较大时，系统 MUST 支持默认仅加载有限代数（例如 5 代）的树谱数据，以保证首屏体验。

#### Scenario: Large family initial tree request returns 5 generations
- **GIVEN** 目标家族成员规模超过阈值（例如 > 500）
- **WHEN** 用户首次请求树谱
- **THEN** 系统返回的数据 MUST 仅覆盖默认代数范围（例如 5 代），并能标识边界节点可继续展开

### Requirement: Expand boundary node loads children on demand
系统 MUST 支持对边界节点按需加载其子代数据，用于懒加载展开。

#### Scenario: Expand node loads next-level children
- **GIVEN** 用户在树谱中选择一个边界节点
- **WHEN** 用户触发“展开加载子代”
- **THEN** 系统返回该节点的子女（以及必要的家庭单元/配偶关系信息）



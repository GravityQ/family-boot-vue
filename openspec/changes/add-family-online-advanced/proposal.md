# Change: add-family-online-advanced（家人在线 增强能力）

## Why(为什么)
在 `add-family-online-mvp` 完成 P0 闭环后，仍存在一批对用户体验与规模化运营至关重要的增强能力：全局搜索与脱敏、审核通知与结果通知、大家族树谱的分代/懒加载策略、以及跨家族配偶关联。将这些能力独立成 Change 可以降低评审与实现风险，并保持可回退。

## Dependency(依赖)
- 本 Change **依赖** `add-family-online-mvp`：需要其定义的家族/成员/审核/隐私基础能力先落地或至少规格冻结。

## What Changes(要做哪些变更)
- 新增增强能力的 Spec Delta（本 Change 仅编写规格与任务拆分，不做实现）：
  - 全局搜索（按姓氏/地区/字辈等组合）与“未加入脱敏概况”策略细化
  - 通知与消息：管理员待办通知、申请人结果通知
  - 树谱大数据策略：默认 5 代视图、边界节点展开加载（懒加载）
  - 跨家族配偶关联：按 FamilyID/MemberID 搜索绑定，隐私策略下的跳转/不可见处理

## Out of Scope / Non-Goals(非目标/不在范围内)
- 图谱渲染前端实现细节（d3/G6 等）不属于本仓库范围，仅定义后端数据契约。
- 复杂风控、内容审核、反爬等全局策略（如需单独 Change）。

## Impact(影响范围)
- **受影响的 Specs(规格文档)**（本次新增 Delta）：
  - `openspec/changes/add-family-online-advanced/specs/family-search/spec.md`
  - `openspec/changes/add-family-online-advanced/specs/notifications/spec.md`
  - `openspec/changes/add-family-online-advanced/specs/tree-loading-strategy/spec.md`
  - `openspec/changes/add-family-online-advanced/specs/cross-family-association/spec.md`
- **相关输入文档（来源）**：
  - `document/1_产品需求文档_PRD.md`
  - `document/2_技术架构与数据库设计_TDD.md`
  - `document/3_UI交互设计规范.md`
  - `document/4_API接口定义文档.md`



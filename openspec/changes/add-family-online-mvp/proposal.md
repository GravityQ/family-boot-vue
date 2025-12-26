# Change: add-family-online-mvp（家人在线 MVP/P0 能力）

## Why(为什么)
当前仓库为芋道（RuoYi-Vue-Pro）后端基座，尚未提供“家人在线”业务域的后端能力。需要一个可评审、可分阶段落地的规格提案，先交付 P0 闭环：**家族创建申请 → 系统审核 → 家族内成员树谱浏览 → 普通用户提交新增/纠错 → 管理员审核通过/驳回**。

## What Changes(要做哪些变更)
- 新增家人在线 MVP/P0 后端能力的 Spec Delta（本 Change 仅编写规格与任务拆分，不做实现）。
- 新增能力范围（P0 闭环）：
  - 家族创建申请与系统管理员审核
  - 家族基础设置（简介、封面、隐私等级等）
  - 成员与树谱获取（支持多配偶/家庭单元模型）
  - 普通用户“申请制”新增/修改（审核记录）与家族管理员审核工作台
  - 隐私脱敏与访问控制边界（未加入家族的访问限制）

## Out of Scope / Non-Goals(非目标/不在范围内)
- 全局搜索（P1）与更细粒度的公开搜索策略（将由 `add-family-online-advanced` 承接）
- 消息与通知（P2：待办通知/结果通知）（将由 `add-family-online-advanced` 承接）
- 大家族懒加载/分代加载的性能细则（将由 `add-family-online-advanced` 承接）
- 跨家族配偶关联与跳转（高级）（将由 `add-family-online-advanced` 承接）

## Impact(影响范围)
- **受影响的 Specs(规格文档)**（本次新增 Delta）：
  - `openspec/changes/add-family-online-mvp/specs/family-management/spec.md`
  - `openspec/changes/add-family-online-mvp/specs/member-tree/spec.md`
  - `openspec/changes/add-family-online-mvp/specs/audit-workflow/spec.md`
  - `openspec/changes/add-family-online-mvp/specs/privacy-access-control/spec.md`
- **相关输入文档（来源）**：
  - `document/1_产品需求文档_PRD.md`
  - `document/2_技术架构与数据库设计_TDD.md`
  - `document/3_UI交互设计规范.md`
  - `document/4_API接口定义文档.md`
  - 参考汇总：`specs/001-merge-prd-spec/spec.md`（注意：该文件不属于 OpenSpec 目录，仅作为背景对照）



---
name: /openspec-apply
id: openspec-apply
category: OpenSpec
description: 实现已批准的 OpenSpec Change(变更)，并保持 Task(任务) 状态同步。
---
<!-- OPENSPEC:START -->
**Guardrails(护栏约束)**
- 优先选择直接、最小实现的方案，只有在明确需要时才增加复杂度。
- 严格控制 Change(变更) 的范围，只做与本次目标直接相关的修改。
- 如需更多 OpenSpec 约定或说明，可参考 `openspec/AGENTS.md`（位于 `openspec/` 目录下；如未看到可运行 `ls openspec` 或 `openspec update`）。

**Steps(步骤)**
将以下步骤视为 TODO(待办) 列表，并逐项完成：
1. 阅读 `changes/<id>/proposal.md`、`design.md`(若存在) 与 `tasks.md`，确认 Change(变更) 范围与验收标准。
2. 按顺序完成 `tasks.md` 中的每个 Task(任务)，保持改动最小化且聚焦本次 Change(变更)。
3. 在更新 Task(任务) 状态前，先确认对应工作已经实际完成。
4. 所有工作完成后，再更新 Checklist(任务清单)，确保每一项都标记为 `- [x]` 且与实际情况一致。
5. 如需更多上下文，可通过 `openspec list` 或 `openspec show <item>` 查看相关 Change(变更) 或 Spec(规格文档) 详情。

**Reference(参考)**
- 实现过程中如需从 Proposal(变更方案) 中获取更细粒度的 Delta(差异说明) 信息，可使用：`openspec show <id> --json --deltas-only`。
<!-- OPENSPEC:END -->

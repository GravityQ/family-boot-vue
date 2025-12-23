---
name: /openspec-proposal
id: openspec-proposal
category: OpenSpec
description: 为新的 OpenSpec Change(变更) 搭建 Proposal(方案) 脚手架并进行严格校验。
---
<!-- OPENSPEC:START -->
**Guardrails(护栏约束)**
- 优先采用直接、最小的设计，只在明确需要时才增加复杂度。
- 严格控制 Proposal(变更方案) 的范围，围绕当前 Change(变更) 的目标展开。
- 如需更多 OpenSpec 约定或细节，请参考 `openspec/AGENTS.md`（位于 `openspec/` 目录；如未看到可运行 `ls openspec` 或 `openspec update`）。
- 对所有含糊或模棱两可的地方，要先通过追问澄清，再编辑文件。
- 在 Proposal(方案) 阶段**不要写任何实现代码**，只创建设计类文档（`proposal.md`、`tasks.md`、`design.md`、以及 Spec Delta(规格差异说明)）。真正的实现留到 Apply(应用) 阶段、批准之后再做。

**Steps(步骤)**
1. 阅读 `openspec/project.md`，并运行 `openspec list` 与 `openspec list --specs`，结合相关代码或文档（例如通过 `rg` / `ls`）了解当前真实行为；对任何不清晰处先记录，下文中要补充说明。
2. 选择一个唯一且动词开头的 `change-id`，在 `openspec/changes/<id>/` 下创建 `proposal.md`、`tasks.md`，按需创建 `design.md`。
3. 将 Change(变更) 映射为具体 Capability(能力) 或 Requirement(需求)，对于多范围变更，将其拆解成多个关联清晰、有顺序关系的 Spec Delta(差异说明)。
4. 当解决方案跨越多个系统、引入新模式或需要在行为前先做技术取舍时，在 `design.md` 中记录架构层面的思考与决定。
5. 在 `changes/<id>/specs/<capability>/spec.md` 中编写 Spec Delta(差异说明)（每个 Capability(能力) 单独一个目录），使用 `## ADDED|MODIFIED|REMOVED Requirements`，并保证每个 Requirement(需求) 至少有一个 `#### Scenario(场景):`，必要时补充跨 Capability(能力) 的关联说明。
6. 将 `tasks.md` 写成一份有序的 Task(任务) 列表：每一条都要小且可验证，能带来可感知的进展，并显式包含验证步骤（测试、工具），清晰标识依赖与可并行的工作。
7. 使用 `openspec validate <id> --strict` 做严格校验，在分享 Proposal(方案) 前修复所有问题。

**Reference(参考)**
- 校验失败时，可使用 `openspec show <id> --json --deltas-only` 或 `openspec show <spec> --type spec` 来查看具体错误上下文。
- 在编写新的 Requirement(需求) 前，推荐先用 `rg -n "Requirement:|Scenario:" openspec/specs` 搜索现有需求，避免重复或冲突。
- 在撰写 Proposal(方案) 前，可通过 `rg <keyword>`、`ls` 或直接读取相关文件的方式探索代码库，确保 Spec(规格文档) 与现实实现保持一致。
<!-- OPENSPEC:END -->

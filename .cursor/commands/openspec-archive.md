---
name: /openspec-archive
id: openspec-archive
category: OpenSpec
description: 归档已部署的 OpenSpec Change(变更)，并同步更新 Spec(规格文档)。
---
<!-- OPENSPEC:START -->
**Guardrails(护栏约束)**
- 优先采用直接、最小的实现方式，只有在明确需要时才引入额外复杂度。
- 严格限制 Change(变更) 范围，仅处理与归档相关的工作。
- 如需更多 OpenSpec 使用约定，参考 `openspec/AGENTS.md`（位于 `openspec/` 目录；如未看到可运行 `ls openspec` 或 `openspec update`）。

**Steps(步骤)**
1. 明确要归档的 Change-id：
   - 如果当前命令参数中已经包含 Change-id（例如通过 `<ChangeId>` 占位填充），则在去除多余空白后直接使用该值。
   - 如果对话中只是以标题或摘要形式“模糊提到”某个 Change(变更)，先执行 `openspec list` 找出可能匹配的 ID，与用户确认具体要归档哪一个。
   - 如果对话历史无法唯一指向一个 Change-id，则先不要归档，通过追问获取明确的 Change-id。
2. 使用 `openspec list` 或 `openspec show <id>` 校验 Change-id：
   - 若该 Change(变更) 不存在、已被归档或明显未准备好归档，应立即停止。
3. 运行 `openspec archive <id> --yes`：
   - 让 CLI 自动移动 Change(变更) 到 `changes/archive/` 并应用 Spec(规格文档) 更新。
   - 仅在“纯工具类变更”时才使用 `--skip-specs`，避免漏更新规格真相。
4. 检查命令输出：
   - 确认目标 Spec(规格文档) 已被更新，并且 Change(变更) 确实出现在 `changes/archive/` 中。
5. 使用 `openspec validate --strict` 再次校验：
   - 如有异常，再用 `openspec show <id>` 检查归档结果。

**Reference(参考)**
- 归档前可以使用 `openspec list` 再次确认 Change-id 是否正确。
- 归档后，使用 `openspec list --specs` 检查被更新的 Spec(规格文档)，确保没有新的校验错误。
<!-- OPENSPEC:END -->

# OpenSpec 使用说明

面向使用 OpenSpec 进行规格驱动开发的 AI 编码助手说明文档。

## TL;DR 快速检查清单

- 在修改前先搜索已有工作：`openspec spec list --long`、`openspec list`（全文搜索只用 `rg`）
- 明确范围：是新增 Capability(能力) 还是修改已有 Capability(能力)
- 选择唯一的 `change-id`：使用 kebab-case、动词开头（如 `add-`、`update-`、`remove-`、`refactor-`）
- 脚手架：创建 `proposal.md`、`tasks.md`、`design.md`(如有需要) 以及对应 Capability(能力) 的差异 Spec(规格文档)
- 编写 Delta(差异说明)：使用 `## ADDED|MODIFIED|REMOVED|RENAMED Requirements(需求)`，每个 Requirement(需求) 至少包含一个 `#### Scenario(场景):`
- 校验：执行 `openspec validate [change-id] --strict` 并修复所有问题
- 请求审批：**在 Proposal(变更方案) 被批准前，不要开始实现代码**

## 三阶段工作流

### 阶段 1：创建 Change(变更)

在以下场景需要创建 Proposal(变更方案)：
- 新增功能或能力
- 引入 Break(破坏性变更)（API、数据结构等）
- 调整架构或通用模式
- 性能优化且会改变行为
- 安全机制调整

典型触发语句示例：
- “帮我创建一个变更 Proposal(方案)”
- “帮我规划一个 Change(变更)”
- “我想创建一个 Spec(规格文档) Proposal(方案)”

模糊匹配指引（建议建 Proposal 的情况）：
- 语句里包含：`proposal`、`change`、`spec`
- 且和：`create`、`plan`、`make`、`start`、`help` 之一同时出现

可以**跳过** Proposal 的情况：
- Bug 修复（仅恢复既有 Spec(规格文档) 的预期行为）
- 拼写错误、格式、注释
- 非破坏性的依赖升级
- 纯配置修改
- 为既有行为补充测试

**工作步骤：**
1. 阅读 `openspec/project.md`，运行 `openspec list` 与 `openspec list --specs`，理解当前上下文。
2. 选择唯一且动词开头的 `change-id`，在 `openspec/changes/<id>/` 下创建 `proposal.md`、`tasks.md`、按需创建 `design.md`，并为受影响的 Capability(能力) 创建 Spec Delta(差异说明)。
3. 在 Delta(差异说明) 中，使用 `## ADDED|MODIFIED|REMOVED Requirements(需求)`，每个 Requirement(需求) 至少有一个 `#### Scenario(场景):`。
4. 运行 `openspec validate <id> --strict`，解决所有校验问题后再提交 Proposal(方案)。

### 阶段 2：实现 Change(变更)

将以下步骤视为 Todo(待办) 列表并逐项完成：
1. **阅读 `proposal.md`** —— 搞清楚要做什么、为什么做。
2. **阅读 `design.md`（若存在）** —— 理解关键技术决策和约束。
3. **阅读 `tasks.md`** —— 拿到实现清单与顺序。
4. **按顺序实现 Task(任务)** —— 小步前进，聚焦于本次变更。
5. **确认完成度** —— 在更新任务状态前，确保 `tasks.md` 中的每一项都已经落地。
6. **更新任务勾选状态** —— 全部完成后，将每个 Task(任务) 标记为 `- [x]`，以真实反映进度。
7. **审批闸门** —— 在 Proposal(变更方案) 被审核通过前，不启动实现。

### 阶段 3：归档 Change(变更)

在变更部署完成后，使用单独的 PR 做归档：
- 将 `changes/[name]/` 移动到 `changes/archive/YYYY-MM-DD-[name]/`
- 若实际能力已发生变更，则同步更新 `specs/`
- 对于仅工具层改动，可使用 `openspec archive <change-id> --skip-specs --yes`（始终显式传 `change-id`）
- 运行 `openspec validate --strict`，确认被归档的 Change(变更) 校验通过

## 任意任务前的上下文检查

**Context Checklist(上下文检查清单)：**
- [ ] 阅读相关 Capability(能力) 的 Spec(规格文档)：`specs/[capability]/spec.md`
- [ ] 检查 `changes/` 下是否有对同一 Capability(能力) 的未完成 Change(变更)，避免冲突
- [ ] 阅读 `openspec/project.md` 了解项目级约定
- [ ] 运行 `openspec list` 查看当前所有活动中的 Change(变更)
- [ ] 运行 `openspec list --specs` 查看已有 Capability(能力)

**在创建新 Spec(规格文档) 前：**
- 始终先确认是否已经存在对应 Capability(能力) 的 Spec(规格文档)
- 优先修改已有 Spec(规格文档)，避免重复定义
- 使用 `openspec show [spec]` 查看当前 Spec(规格文档) 状态
- 如需求描述模糊，先补 1–2 个澄清问题再起草 Spec(规格文档)

### 搜索指引

- 枚举所有 Spec(规格文档)：`openspec spec list --long`（脚本可用 `--json`）
- 枚举所有 Change(变更)：`openspec list`（或 `openspec change list --json`，已废弃但仍可用）
- 查看详情：
  - Spec(规格文档)：`openspec show <spec-id> --type spec`（配合 `--json` 做过滤）
  - Change(变更)：`openspec show <change-id> --json --deltas-only`
- 全文搜索（推荐用 ripgrep）：`rg -n "Requirement:|Scenario:" openspec/specs`

## 快速上手

### 常用 CLI 命令

```bash
# 核心命令
openspec list                  # 列出当前所有活动中的 Change(变更)
openspec list --specs          # 列出当前所有 Spec(规格文档)
openspec show [item]           # 显示 Change(变更) 或 Spec(规格文档) 详情
openspec validate [item]       # 校验 Change(变更) 或 Spec(规格文档)
openspec archive <change-id> [--yes|-y]   # 归档已部署的 Change(变更)（加 --yes 以便非交互执行）

# 项目管理
openspec init [path]           # 初始化 OpenSpec 工程
openspec update [path]         # 更新说明文件

# 交互模式
openspec show                  # 交互选择要查看的条目
openspec validate              # 批量校验所有条目

# 调试
openspec show [change] --json --deltas-only
openspec validate [change] --strict
```

### 常用命令参数

- `--json`：以机器可读的 JSON 输出
- `--type change|spec`：在 ID 模糊时，显式指定类型
- `--strict`：开启严格模式，做全面校验
- `--no-interactive`：关闭所有交互式提示
- `--skip-specs`：归档时不自动更新 Spec(规格文档)
- `--yes`/`-y`：跳过确认提示，适合自动化流程

## 目录结构

```
openspec/
├── project.md              # 项目级约定
├── specs/                  # 当前真实状态：已经实现的能力
│   └── [capability]/       # 单一 Capability(能力)
│       ├── spec.md         # Requirements(需求) 与 Scenarios(场景)
│       └── design.md       # 技术模式与实现约定
├── changes/                # Change(变更) 提案：未来想要的变化
│   ├── [change-name]/
│   │   ├── proposal.md     # 变更原因与概要
│   │   ├── tasks.md        # 实现 Checklist(任务清单)
│   │   ├── design.md       # 技术决策（可选）
│   │   └── specs/          # 对各 Capability(能力) 的 Delta(差异说明)
│   │       └── [capability]/
│   │           └── spec.md # 使用 ADDED/MODIFIED/REMOVED 形式的 Requirements(需求)
│   └── archive/            # 已归档的历史 Change(变更)
```

## 创建 Change Proposal(变更方案)

### 决策树

```
有新的需求或想法？
├─ 只是修 Bug 且只是恢复原 Spec(规格文档) 行为？ → 直接修，跳过 Proposal
├─ 只是拼写/格式/注释？ → 直接改
├─ 新功能 / 新 Capability(能力)？ → 创建 Proposal
├─ Break(破坏性变更)？ → 创建 Proposal
├─ 架构层调整？ → 创建 Proposal
└─ 说不清 / 不确定？ → 创建 Proposal（更安全）
```

### Proposal(变更方案) 结构

1. **创建目录：** `changes/[change-id]/`（kebab-case，动词开头，需全局唯一）

2. **编写 `proposal.md`：**
```markdown
# Change: [变更的简短描述]

## Why(为什么)
[1-2 句描述问题或机会]

## What Changes(要做哪些变更)
- [变更项列表]
- [带有 **BREAKING** 标记的破坏性变更]

## Impact(影响范围)
- 受影响的 Specs(规格文档)：[能力列表]
- 受影响的代码：[关键模块或文件]
```

3. **创建 Spec Delta(差异说明)：** `changes/[change-id]/specs/[capability]/spec.md`
```markdown
## ADDED Requirements(新增需求)
### Requirement(需求): New Feature
The system SHALL provide...

#### Scenario(场景): Success case
- **WHEN** user performs action
- **THEN** expected result

## MODIFIED Requirements(修改后的需求)
### Requirement(需求): Existing Feature
[完整的需求内容（更新后）]

## REMOVED Requirements(删除的需求)
### Requirement(需求): Old Feature
**Reason(原因)**: [删除原因]
**Migration(迁移方案)**: [兼容与迁移策略]
```

如一次 Change(变更) 影响多个 Capability(能力)，则在 `changes/[change-id]/specs/<capability>/spec.md` 下为每个 Capability(能力) 单独创建一个 Delta(差异说明) 文件。

4. **创建 `tasks.md`：**
```markdown
## 1. Implementation(实现)
- [ ] 1.1 创建数据库 Schema
- [ ] 1.2 实现 API Endpoint(接口)
- [ ] 1.3 补充前端组件
- [ ] 1.4 补充测试
```

5. **在需要时创建 `design.md`：**

仅在以下情况需要创建 `design.md`，否则可以省略：
- 涉及多服务 / 多模块的跨领域变更，或引入新的架构模式
- 引入新的外部依赖，或进行重要的数据模型调整
- 与安全、性能、迁移相关且复杂度较高
- 存在较大不确定性，需要先对技术方案达成一致

推荐的 `design.md` 最小骨架：
```markdown
## Context(背景)
[背景、约束、相关方]

## Goals / Non-Goals(目标 / 非目标)
- Goals: [...]
- Non-Goals: [...]

## Decisions(关键决策)
- Decision: [做了什么决定，为什么]
- Alternatives considered: [备选方案与取舍原因]

## Risks / Trade-offs(风险与权衡)
- [风险点] → 对应缓解措施

## Migration Plan(迁移方案)
[步骤、回滚方案]

## Open Questions(待决问题)
- [...]
```

## Spec(规格文档) 文件格式

### 关于 Scenario(场景) 的关键约束

**正确写法**（必须用 `####` 作为标题）：
```markdown
#### Scenario(场景): User login success
- **WHEN** valid credentials provided
- **THEN** return JWT token
```

**错误示例**（不要用无标题或仅加粗）：
```markdown
- **Scenario: User login**  ❌
**Scenario**: User login     ❌
### Scenario: User login      ❌
```

每个 Requirement(需求) **必须至少有一个** Scenario(场景)。

### Requirement(需求) 的表述

- 规范性需求使用 `SHALL` / `MUST`（避免无意中使用 `should` / `may`）

### Delta(差异) 操作类型

- `## ADDED Requirements` —— 新增 Capability(能力) 或新增 Requirement(需求)
- `## MODIFIED Requirements` —— 修改既有 Requirement(需求) 的行为、范围或验收条件
- `## REMOVED Requirements` —— 删除或废弃 Requirement(需求)
- `## RENAMED Requirements` —— 仅更名

标题匹配使用 `trim(header)`，会忽略首尾空白。

#### ADDED vs MODIFIED 的使用场景

- **ADDED**：新增一个可以独立存在的 Capability(能力) 或 Requirement(需求)，不要在这里只“补一两句”。
- **MODIFIED**：改变现有 Requirement(需求) 的行为/范围/验收标准。使用时需要拷贝该 Requirement(需求) 的**完整内容**（标题 + 所有 Scenario(场景)）并在此基础上修改。
- **RENAMED**：仅更名时使用；如果同时修改行为，则需要 `RENAMED + MODIFIED`。

常见错误：只在 MODIFIED 区块里添加少量新描述，而不包含原 Requirement(需求) 的全文，会导致归档时丢失原有细节。若没有要改的原内容，直接新加 Requirement(需求) 到 ADDED 即可。

正确编写 MODIFIED Requirement(需求) 的步骤：
1. 在 `openspec/specs/<capability>/spec.md` 中找到目标 Requirement(需求)。
2. 从 `### Requirement: ...` 开始，包含所有 Scenario(场景)，整体拷贝出来。
3. 粘贴到 `## MODIFIED Requirements` 下，再做编辑。
4. 保证 Requirement(需求) 标题文本完全一致（空白忽略），并至少保留一个 `#### Scenario(场景):`。

RENAMED 的示例：
```markdown
## RENAMED Requirements
- FROM: `### Requirement: Login`
- TO: `### Requirement: User Authentication`
```

## 故障排查

### 常见错误

**“Change must have at least one delta”(变更必须至少包含一个 Delta(差异))**
- 检查 `changes/[name]/specs/` 是否存在且包含 `.md` 文件
- 确认文件中是否使用了类似 `## ADDED Requirements` 的操作块

**“Requirement must have at least one scenario”(需求必须至少包含一个场景)**  
- 检查是否使用了 `#### Scenario(场景):` 形式的标题（4 个 `#`）
- 不要用无标题或仅加粗的写法表示 Scenario(场景)

**Scenario(场景) 解析失败但没有明确报错的情况**
- 检查标题格式是否精确是：`#### Scenario: Name`
- 调试时可用：`openspec show [change] --json --deltas-only`

### 验证小技巧

```bash
# 永远优先使用严格模式做完整校验
openspec validate [change] --strict

# 调试 Delta(差异说明) 解析
openspec show [change] --json | jq '.deltas'

# 检查特定 Requirement(需求)
openspec show [spec] --json -r 1
```

## Happy Path(推荐流程) 示例脚本

```bash
# 1) 探索当前状态
openspec spec list --long
openspec list
# 可选的全文搜索：
# rg -n "Requirement:|Scenario:" openspec/specs
# rg -n "^#|Requirement:" openspec/changes

# 2) 选择 change-id 并搭建脚手架
CHANGE=add-two-factor-auth
mkdir -p openspec/changes/$CHANGE/{specs/auth}
printf "## Why\n...\n\n## What Changes\n- ...\n\n## Impact\n- ...\n" > openspec/changes/$CHANGE/proposal.md
printf "## 1. Implementation\n- [ ] 1.1 ...\n" > openspec/changes/$CHANGE/tasks.md

# 3) 编写 Delta(差异说明) 示例
cat > openspec/changes/$CHANGE/specs/auth/spec.md << 'EOF'
## ADDED Requirements
### Requirement: Two-Factor Authentication
Users MUST provide a second factor during login.

#### Scenario: OTP required
- **WHEN** valid credentials are provided
- **THEN** an OTP challenge is required
EOF

# 4) 校验
openspec validate $CHANGE --strict
```

## 多 Capability(能力) 示例

```
openspec/changes/add-2fa-notify/
├── proposal.md
├── tasks.md
└── specs/
    ├── auth/
    │   └── spec.md   # ADDED: Two-Factor Authentication
    └── notifications/
        └── spec.md   # ADDED: OTP email notification
```

`auth/spec.md` 示例：
```markdown
## ADDED Requirements
### Requirement: Two-Factor Authentication
...
```

`notifications/spec.md` 示例：
```markdown
## ADDED Requirements
### Requirement: OTP Email Notification
...
```

## 最佳实践

### 优先保持简单
- 默认新增代码 < 100 行
- 能用单文件搞定的，先用单文件实现
- 没有明确收益时不要引入新框架
- 优先选择“无聊但成熟”的模式

### 何时才允许引入复杂度

仅在以下情况增加复杂度：
- 有性能数据证明当前方案明显不够用
- 有明确的规模需求（例如 > 1000 用户，> 100MB 数据）
- 多个已验证用例确实需要抽象

### 清晰引用

- 引用代码位置时使用 `file.ts:42` 形式
- 引用 Spec(规格文档) 时使用 `specs/auth/spec.md`
- 关联 Change(变更) 与 PR 时，互相加入引用

### Capability(能力) 命名

- 使用动词-名词：如 `user-auth`、`payment-capture`
- 每个 Capability(能力) 聚焦单一目的
- 满足“10 分钟可完全理解”的原则
- 如果描述里必须出现 “AND”，考虑拆分为多个 Capability(能力)

### Change-id 命名

- 使用简短、描述性 kebab-case：如 `add-two-factor-auth`
- 优先动词前缀：`add-`、`update-`、`remove-`、`refactor-`
- 保证全局唯一；若冲突，可追加 `-2`、`-3` 等

## 工具选择速查

| Task(任务) | Tool(工具) | 说明 |
|-----------|-----------|------|
| 按模式查找文件 | Glob | 快速文件名匹配 |
| 搜索代码内容 | Grep | 高性能正则搜索 |
| 读取特定文件 | Read | 直接访问文件内容 |
| 探索未知范围 | Task | 多步骤调查 |

## 错误恢复

### Change(变更) 冲突

1. 运行 `openspec list` 查看所有进行中的 Change(变更)
2. 检查是否有多个 Change(变更) 影响同一 Capability(能力)
3. 与相关 Change(变更) 的负责人沟通协调
4. 必要时考虑合并 Proposal(变更方案)

### 校验失败

1. 使用 `--strict` 重新运行校验
2. 查看 JSON 输出中的错误详情
3. 检查 Spec(规格文档) 文件格式是否符合约定
4. 确保 Scenario(场景) 标题格式正确

### 缺少上下文

1. 优先阅读 `project.md`
2. 查看相关 Capability(能力) 的 Spec(规格文档)
3. 查阅最近归档的 Change(变更) 历史
4. 不确定时，向产品/研发提出澄清问题

## 快速参考

### 阶段标识

- `changes/` —— 处于提案阶段，尚未完全落地的 Change(变更)
-, `specs/` —— 已实现并投入使用的真实能力
- `archive/` —— 已部署且归档的历史 Change(变更)

### 各文件作用

- `proposal.md` —— 为什么要做、要做什么
- `tasks.md` —— 实施步骤与 Checklist(任务清单)
- `design.md` —— 技术决策与权衡
- `spec.md` —— Requirements(需求) 与 Scenarios(场景)，即“系统应该如何表现”

### 常用 CLI 速查

```bash
openspec list              # 当前有哪些 Change(变更) 在进行？
openspec show [item]       # 查看 Change(变更) 或 Spec(规格文档) 详情
openspec validate --strict # 使用严格模式做校验
openspec archive <change-id> [--yes|-y]  # 标记 Change(变更) 已完成并归档（自动流程建议加 --yes）
```

记住：**Spec(规格文档) 是事实真相，Change(变更) 是未来意图**。实现完成后，一定要保持两者同步。 
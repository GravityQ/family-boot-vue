## ADDED Requirements

### Requirement: User-submitted changes are application-based
系统 MUST 对普通用户的增删改采用“申请制”：提交后生成审核记录，在审核通过前不得写入正式业务表（family/member/family_unit）。

#### Scenario: User submits member create application
- **GIVEN** 登录用户为普通用户且已加入目标家族
- **WHEN** 用户提交“新增成员”申请（包含 familyId、name、gender、fatherId 等必要字段）
- **THEN** 系统创建一条待审核的审核记录，并返回“申请已提交”

#### Scenario: User submits member update application with partial fields
- **GIVEN** 登录用户为普通用户且已加入目标家族
- **WHEN** 用户提交“纠错/修改”申请，并在 newValues 中仅包含变更字段
- **THEN** 系统创建一条待审核的审核记录，且 new_json 仅包含变更字段集合

### Requirement: Family admin reviews audit items
系统 MUST 支持家族管理员对本家族的审核记录执行分页查看、对比详情、通过/驳回操作。

#### Scenario: Family admin views pending audit list
- **GIVEN** 登录用户为目标家族管理员，且存在待审审核记录
- **WHEN** 用户分页查询审核记录（status=PENDING）
- **THEN** 系统仅返回该家族的待审记录列表

#### Scenario: Approve audit item applies changes to official data
- **GIVEN** 登录用户为目标家族管理员，且存在待审审核记录
- **WHEN** 管理员执行“通过”
- **THEN** 系统将审核记录对应的变更应用到正式表，并将审核状态更新为 APPROVED，记录审核人/时间

#### Scenario: Reject audit item does not change official data
- **GIVEN** 登录用户为目标家族管理员，且存在待审审核记录
- **WHEN** 管理员执行“驳回”并填写原因
- **THEN** 系统将审核状态更新为 REJECTED，记录驳回原因，且正式表数据不发生变化



## ADDED Requirements

### Requirement: Notify family admins of pending audit items
系统 MUST 在产生待审核记录（PENDING）时通知对应家族管理员存在新的待办。

#### Scenario: Pending audit triggers admin todo notification once
- **GIVEN** 某家族产生一条新的待审审核记录
- **WHEN** 审核记录进入 PENDING 状态
- **THEN** 系统向该家族管理员发送待办通知，且对同一审核记录的同一状态变更 MUST 具备幂等（不重复发送）

### Requirement: Notify applicants of audit results
系统 MUST 在审核记录被通过或驳回后通知申请人，并在驳回时携带原因。

#### Scenario: Applicant receives approved notification
- **GIVEN** 某审核记录已由家族管理员审核通过
- **WHEN** 审核状态变为 APPROVED
- **THEN** 系统向申请人发送“已通过”通知

#### Scenario: Applicant receives rejected notification with reason
- **GIVEN** 某审核记录已由家族管理员驳回且存在驳回原因
- **WHEN** 审核状态变为 REJECTED
- **THEN** 系统向申请人发送“已驳回”通知，并包含驳回原因



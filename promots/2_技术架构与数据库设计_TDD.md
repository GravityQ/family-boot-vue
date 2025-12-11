# 2. 技术架构与数据库设计 (TDD) - 家人在线

**基于框架**: 芋道 (Yudao-Vue-Pro / Spring Boot 3 + JDK 17)
**文档类型**: 技术设计文档
**最后更新**: 2024-05-22

---

## 1. 技术栈选型

### 1.1 后端架构
*   **核心框架**: Spring Boot 3.5.5
*   **ORM**: MyBatis Plus 3.5.12 (MySQL 8.0)
*   **缓存**: Redis 7.0 + Redisson (分布式锁/缓存)
*   **安全**: Spring Security + JWT + RBAC
*   **工具**: MapStruct (对象映射), Lombok, Knife4j (接口文档)
*   **多租户**: 逻辑隔离（基于 `tenant_id` 或 家族ID 隔离）

### 1.2 前端架构
*   **管理后台 (Web)**: Vue3 + Element Plus (基于 yudao-ui-admin-vue3)
*   **移动端 (App/小程序)**: uniapp (Vue3) 或 微信原生小程序 (推荐 uniapp 以便多端发布)
*   **图谱渲染**:
    *   方案 A: `d3.js` (功能最强，定制性高，但学习曲线陡峭)
    *   方案 B: `AntV G6` (阿里开源，适合图编辑，文档齐全 - **推荐**)

---

## 2. 模块设计

在 `yudao` 项目根目录下新增模块 `yudao-module-family`，结构如下：

```
yudao-module-family
├── yudao-module-family-api       # 接口定义 (DTO)
└── yudao-module-family-biz       # 业务实现
    ├── controller  # Admin 和 App 控制器分离
    ├── dal         # Data Access Layer (Mapper/DO)
    ├── service     # 业务逻辑
    └── convert     # MapStruct 转换器
```

---

## 3. 数据库设计 (Schema)

### 3.1 家族表 (family)
核心实体，记录家族元数据。

```sql
CREATE TABLE `family` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(100) NOT NULL COMMENT '家族名称',
  `main_surname` varchar(50) NOT NULL COMMENT '主要姓氏',
  `location` varchar(200) DEFAULT NULL COMMENT '祖籍位置',
  `desc` text COMMENT '家族简介/家训',
  `cover_url` varchar(512) DEFAULT NULL COMMENT '封面图URL',
  `creator_id` bigint NOT NULL COMMENT '创建者UserID',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态: 0待审, 1正常, 2拒绝, 3删除',
  `privacy_level` tinyint DEFAULT '0' COMMENT '隐私: 0公开, 1仅族人可见',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户ID(保留字段)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`id`),
  KEY `idx_name` (`name`),
  KEY `idx_creator` (`creator_id`)
) COMMENT='家族信息表';
```

### 3.2 成员表 (member)
记录自然人信息。**注意**：不直接存储配偶ID，配偶关系通过 `family_unit` 关联。

```sql
CREATE TABLE `member` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `family_id` bigint NOT NULL COMMENT '所属家族ID',
  `name` varchar(50) NOT NULL COMMENT '姓名',
  `gender` tinyint NOT NULL COMMENT '性别: 1男, 0女',
  `generation_level` int DEFAULT NULL COMMENT '字辈/第几世',
  `birth_date` date DEFAULT NULL COMMENT '出生日期',
  `death_date` date DEFAULT NULL COMMENT '去世日期',
  `is_alive` bit(1) DEFAULT b'1' COMMENT '是否健在',
  `avatar_url` varchar(512) DEFAULT NULL COMMENT '头像',
  `biography` text COMMENT '人物行传/生平',
  `father_id` bigint DEFAULT NULL COMMENT '父亲ID (冗余,便于快速构建树)',
  `mother_id` bigint DEFAULT NULL COMMENT '母亲ID (冗余)',
  `parent_family_unit_id` bigint DEFAULT NULL COMMENT '所属的原生家庭单元ID',
  `sort` int DEFAULT '0' COMMENT '排序(长幼顺序)',
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  `deleted` bit(1) DEFAULT b'0',
  PRIMARY KEY (`id`),
  KEY `idx_family` (`family_id`),
  KEY `idx_parent` (`father_id`)
) COMMENT='家族成员表';
```

### 3.3 家庭单元表 (family_unit)
**核心设计**：用于解决多配偶、离异、同父异母等复杂关系。一个家庭单元 = 一段婚姻/伴侣关系。

```sql
CREATE TABLE `family_unit` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `family_id` bigint NOT NULL,
  `husband_id` bigint NOT NULL COMMENT '丈夫ID',
  `wife_id` bigint DEFAULT NULL COMMENT '妻子ID (可为空,如未婚生子)',
  `wife_name` varchar(50) DEFAULT NULL COMMENT '妻子姓名(若妻子未录入系统)',
  `marriage_date` date DEFAULT NULL COMMENT '结婚日期',
  `status` tinyint DEFAULT '1' COMMENT '状态: 1婚姻中, 0离异, 2丧偶',
  `sort` int DEFAULT '0' COMMENT '配偶排序(原配/续弦)',
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  `deleted` bit(1) DEFAULT b'0',
  PRIMARY KEY (`id`),
  KEY `idx_husband` (`husband_id`),
  KEY `idx_wife` (`wife_id`)
) COMMENT='家庭单元(婚姻)表';
```

### 3.4 审核记录表 (audit_log)
实现“普通用户提交 -> 管理员审核”的中间态存储。

```sql
CREATE TABLE `audit_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `family_id` bigint NOT NULL,
  `applicant_id` bigint NOT NULL COMMENT '申请人ID',
  `entity_type` varchar(20) NOT NULL COMMENT 'MEMBER / FAMILY',
  `entity_id` bigint DEFAULT NULL COMMENT '关联实体ID(新增时为空)',
  `operation_type` varchar(20) NOT NULL COMMENT 'CREATE / UPDATE / DELETE',
  `old_json` json DEFAULT NULL COMMENT '修改前数据快照',
  `new_json` json NOT NULL COMMENT '期望修改后数据',
  `audit_status` tinyint DEFAULT '0' COMMENT '0待审, 1通过, 2驳回',
  `audit_comment` varchar(255) DEFAULT NULL COMMENT '驳回原因',
  `auditor_id` bigint DEFAULT NULL COMMENT '审核人ID',
  `audit_time` datetime DEFAULT NULL,
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_status` (`family_id`, `audit_status`)
) COMMENT='业务审核记录表';
```

---

## 4. API 接口设计 (RESTful)

### 4.1 管理端 (Admin API)
*   **权限**: 需校验 `@PreAuthorize` 和 Data Scope。
*   `POST /admin-api/family/create` - 创建家族
*   `PUT /admin-api/family/update` - 更新家族
*   `GET /admin-api/family/audit/page` - 分页获取审核记录
*   `PUT /admin-api/family/audit/approve` - 审核通过
*   `PUT /admin-api/family/audit/reject` - 审核驳回

### 4.2 用户端 (App API)
*   **权限**: 基于 Login User 和 Family Member Role 校验。
*   `GET /app-api/family/tree/get`
    *   **Param**: `familyId`
    *   **Logic**: 递归查询 Member 和 FamilyUnit，组装成 Tree 结构。建议使用 MapStruct 或 手动递归优化性能。
*   `GET /app-api/family/member/get` - 获取详情
*   `POST /app-api/family/member/apply-create` - 申请新增成员
*   `POST /app-api/family/member/apply-update` - 申请修改成员

---

## 5. 关键业务逻辑实现

### 5.1 树形结构构建算法
由于数据库是平铺存储 (`id`, `father_id`, `family_unit_id`)，前端需要树形 JSON。
**后端处理策略**:
1.  **全量加载**: 对于 < 1000 人的家族，一次性查出 `member` 和 `family_unit`。
2.  **内存组装**: 使用 Java Stream API 或递归，将 List 转为 Tree。
    *   Root: `father_id` 为 NULL 的节点。
    *   Child: 遍历 `family_unit` 寻找 `husband_id` = CurrentNode 的记录，再找该 unit 下的 children。
3.  **大树优化**: 对于 > 1000 人，采用“按层级加载”或“以某人为中心的 5 代视图”。

### 5.2 审核数据一致性
*   **Update 操作**: 审核通过前，不修改 `member` 表。
*   **Create 操作**: 审核通过前，不插入 `member` 表。
*   **并发控制**: 使用 Redisson 锁，防止同一成员同时被多人修改提交。


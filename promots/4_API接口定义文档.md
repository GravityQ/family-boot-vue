# 4. API 接口定义文档 (API Reference) - 家人在线

**基础信息**
*   **Base URL**: `/app-api/family/v1` (移动端) | `/admin-api/family` (管理端)
*   **协议**: HTTPS
*   **格式**: JSON
*   **鉴权**: Bearer Token (Authorization Header)
*   **状态**: v1.0.0

---

## 1. 通用数据结构

### 1.1 统一响应结构 (Response Wrapper)
所有接口（除非特殊说明）均返回此结构。

```json
{
  "code": 0,          // 业务状态码: 0-成功, 非0-失败 (如 401, 1002001)
  "data": { ... },    // 业务数据
  "msg": "成功"        // 提示信息
}
```

### 1.2 分页参数 (Page Param)
GET 请求 Query 参数。
*   `pageNo`: 页码 (默认 1)
*   `pageSize`: 每页条数 (默认 10)

---

## 2. 移动端接口 (App API)

主要面向小程序用户，路径前缀 `/app-api/family/v1`。

### 2.1 家族模块 (Family)

#### F01. 创建家族申请
*   **Path**: `POST /family/create`
*   **Auth**: Required
*   **Request Body**:
    ```json
    {
      "name": "颍川陈氏",            // 必填, 家族名 (2-20字)
      "mainSurname": "陈",          // 必填, 主要姓氏
      "location": "河南省许昌市",     // 选填, 祖籍
      "desc": "天下陈氏出颍川...",    // 选填, 简介
      "coverUrl": "https://..."     // 选填, 封面图
    }
    ```
*   **Response**: `data: 1001` (返回新生成的 Family ID)

#### F02. 搜索家族
*   **Path**: `GET /family/page`
*   **Auth**: Optional (未登录也可搜，但详情受限)
*   **Query**:
    *   `name`: 家族名 (模糊匹配)
    *   `surname`: 姓氏
*   **Response**:
    ```json
    {
      "list": [
        {
          "id": 1001,
          "name": "颍川陈氏",
          "memberCount": 520,      // 成员总数
          "location": "河南",
          "coverUrl": "..."
        }
      ],
      "total": 1
    }
    ```

#### F03. 获取家族详情
*   **Path**: `GET /family/get`
*   **Query**: `id=1001`
*   **Logic**: 若用户未加入该家族，隐藏敏感字段。
*   **Response**:
    ```json
    {
      "id": 1001,
      "name": "颍川陈氏",
      "desc": "...",
      "isMember": true,           // 当前用户是否为成员
      "creatorName": "陈大发"
    }
    ```

---

### 2.2 成员与图谱模块 (Member & Tree)

#### M01. 获取全量树形图谱 (核心)
*   **Path**: `GET /member/tree`
*   **Query**: `familyId=1001`
*   **Response**:
    ```json
    {
      "rootId": 1,               // 始祖ID
      "members": [               // 扁平化节点列表 (前端自行组装或后端组装Tree)
        {
          "id": 1,
          "name": "陈始祖",
          "gender": 1,
          "generation": 1,       // 第几世
          "spouses": [           // 配偶列表
            { "id": 2, "name": "王氏", "familyUnitId": 101 }
          ],
          "children": [ 3, 4 ]   // 子女ID列表
        },
        { "id": 3, "name": "陈二代A", "fatherId": 1, ... }
      ]
    }
    ```

#### M02. 获取成员详情
*   **Path**: `GET /member/get`
*   **Query**: `id=100`
*   **Response**:
    ```json
    {
      "id": 100,
      "name": "陈某某",
      "gender": 1,
      "biography": "生平事迹...",
      "photos": ["url1", "url2"],
      "relations": {
        "father": { "id": 50, "name": "陈父" },
        "mother": { "id": 51, "name": "张母" },
        "spouses": [ ... ],
        "children": [ ... ]
      }
    }
    ```

#### M03. 申请添加成员
*   **Path**: `POST /member/apply-create`
*   **Request Body**:
    ```json
    {
      "familyId": 1001,
      "name": "陈新丁",
      "gender": 1,
      "fatherId": 100,          // 必填 (除非是始祖)
      "motherId": 101,          // 选填
      "birthDate": "2023-01-01",
      "familyUnitId": 200       // 可选: 指定归属哪个家庭单元(多配偶场景)
    }
    ```
*   **Response**: `data: true` (提示: "申请已提交")

---

### 2.3 审核模块 (Audit)

#### A01. 提交纠错/修改
*   **Path**: `POST /audit/apply-update`
*   **Request Body**:
    ```json
    {
      "memberId": 100,
      "familyId": 1001,
      "newValues": {            // 仅包含变更的字段
        "birthDate": "1990-05-05",
        "biography": "修正后的简介..."
      },
      "remark": "日期写错了"      // 申请备注
    }
    ```

---

## 3. 管理端接口 (Admin API)

主要面向家族管理员和系统管理员，路径前缀 `/admin-api/family`。

### 3.1 审核工作台

#### AD01. 分页获取待审核记录
*   **Path**: `GET /audit/page`
*   **Query**:
    *   `familyId`: 1001
    *   `status`: 0 (待审)
*   **Response**:
    ```json
    {
      "list": [
        {
          "id": 500,
          "type": "MEMBER_UPDATE",
          "applicantName": "陈小明",
          "time": "2024-05-20 10:00",
          "contentDiff": {
             "old": { "birth": "1990" },
             "new": { "birth": "1991" }
          }
        }
      ]
    }
    ```

#### AD02. 审核通过
*   **Path**: `PUT /audit/approve`
*   **Request Body**:
    ```json
    {
      "id": 500              // 审核记录ID
    }
    ```

#### AD03. 审核驳回
*   **Path**: `PUT /audit/reject`
*   **Request Body**:
    ```json
    {
      "id": 500,
      "reason": "出生日期不能晚于今天"
    }
    ```

### 3.2 成员批量管理

#### AD04. 直接新增成员 (免审)
*   **Path**: `POST /member/create`
*   **Description**: 家族管理员专用，直接写入 Member 表，不生成审核记录。
*   **Body**: 同 `M03`。

#### AD05. 删除成员
*   **Path**: `DELETE /member/delete`
*   **Query**: `id=100`
*   **Check**: 如果该成员有子女，默认禁止删除，需前端二次确认是否“级联删除”或“转移子女”。

---

## 4. 枚举字典 (Dictionaries)

### 4.1 审核状态 (audit_status)
*   `0`: PENDING (待审核)
*   `1`: APPROVED (已通过)
*   `2`: REJECTED (已驳回)
*   `3`: CANCELLED (用户自行撤销)

### 4.2 隐私等级 (privacy_level)
*   `0`: PUBLIC (公开，任何人可搜可看)
*   `1`: PROTECTED (半公开，可搜到家族，但需申请加入才可看图谱)
*   `2`: PRIVATE (私密，不可搜，仅限邀请)


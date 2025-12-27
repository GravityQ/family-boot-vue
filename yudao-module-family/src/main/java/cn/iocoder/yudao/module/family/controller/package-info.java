/**
 * 提供 RESTful API 给前端：
 * 1. admin 包：提供给管理后台 yudao-ui-admin 前端项目
 *    - Controller 路径配置：使用 @RequestMapping("/family/xxx")，不要包含 /admin-api 前缀
 *    - 最终访问路径：/admin-api/family/xxx（由 YudaoWebAutoConfiguration 自动添加 /admin-api 前缀）
 *    - 示例：AdminFamilyController 使用 @RequestMapping("/family")，最终路径为 /admin-api/family
 * 2. app 包：提供给用户 APP yudao-ui-app 前端项目
 *    - Controller 路径配置：使用 @RequestMapping("/family/v1/xxx")，不要包含 /app-api 前缀
 *    - 最终访问路径：/app-api/family/v1/xxx（由 YudaoWebAutoConfiguration 自动添加 /app-api 前缀）
 *    - 示例：AppFamilyController 使用 @RequestMapping("/family/v1")，最终路径为 /app-api/family/v1
 *    - 注意：Controller 和 VO 都要添加 App 前缀，用于和管理后台进行区分
 *
 * 路径配置说明：
 * - 框架会自动根据 Controller 所在包路径（**.controller.admin.** 或 **.controller.app.**）添加对应的前缀
 * - 因此 Controller 的 @RequestMapping 中不应包含 /admin-api 或 /app-api 前缀，避免路径重复
 * - 参考配置类：cn.iocoder.yudao.framework.web.config.YudaoWebAutoConfiguration
 */
package cn.iocoder.yudao.module.family.controller;


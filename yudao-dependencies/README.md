# yudao-dependencies 模块说明文档

## 概述

`yudao-dependencies` 是一个 **Maven BOM (Bill of Materials)** 模块，用于统一管理整个项目的依赖版本。它采用 `<packaging>pom</packaging>` 方式，不包含任何实际代码，只负责版本管理。

**核心价值：**
- 统一版本管理：所有子模块的依赖版本都在此集中定义
- 避免版本冲突：通过 `dependencyManagement` 确保版本一致性
- 简化配置：子模块引用依赖时无需指定版本号
- 便于升级：只需在此处修改版本号，即可升级整个项目的依赖

## 实现原理

### BOM 模式

```xml
<packaging>pom</packaging>
```

使用 `pom` 打包方式，表示这是一个父级依赖管理模块。

### 版本统一管理

在 `<properties>` 中定义所有依赖的版本号：

```xml
<properties>
    <spring.boot.version>3.5.5</spring.boot.version>
    <mybatis-plus.version>3.5.14</mybatis-plus.version>
    <redisson.version>3.51.0</redisson.version>
    <!-- ... 更多版本定义 -->
</properties>
```

### 依赖声明

在 `<dependencyManagement>` 中声明所有依赖及其版本：

```xml
<dependencyManagement>
    <dependencies>
        <!-- 引入 Spring Boot BOM -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-dependencies</artifactId>
            <version>${spring.boot.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
        
        <!-- 声明项目内部模块 -->
        <dependency>
            <groupId>cn.iocoder.boot</groupId>
            <artifactId>yudao-spring-boot-starter-web</artifactId>
            <version>${revision}</version>
        </dependency>
        
        <!-- 声明第三方依赖 -->
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
            <version>${mybatis-plus.version}</version>
        </dependency>
    </dependencies>
</dependencyManagement>
```

### 版本号管理

使用 `flatten-maven-plugin` 插件统一管理版本号：

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>flatten-maven-plugin</artifactId>
    <configuration>
        <flattenMode>bom</flattenMode>
        <updatePomFile>true</updatePomFile>
    </configuration>
</plugin>
```

## 使用方式

### 在父 POM 中引入

```xml
<parent>
    <groupId>cn.iocoder.boot</groupId>
    <artifactId>yudao-dependencies</artifactId>
    <version>2025.10-SNAPSHOT</version>
    <relativePath/>
</parent>
```

### 在子模块中使用

子模块引用依赖时，**无需指定版本号**：

```xml
<dependencies>
    <!-- 版本由 yudao-dependencies 统一管理 -->
    <dependency>
        <groupId>com.baomidou</groupId>
        <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
        <!-- 无需指定 version -->
    </dependency>
</dependencies>
```

## 主要依赖分类

| 分类 | 说明 | 示例依赖 |
|------|------|----------|
| Spring 核心 | Spring Boot 及相关组件 | spring-boot-dependencies |
| Web 相关 | Web 框架、API 文档 | springdoc-openapi, knife4j |
| 数据库相关 | MyBatis、数据源、连接池 | mybatis-plus, druid, dynamic-datasource |
| 缓存相关 | Redis、Redisson | redisson-spring-boot-starter |
| 消息队列 | RocketMQ、Kafka、RabbitMQ | rocketmq-spring-boot-starter |
| 监控相关 | SkyWalking、Spring Boot Admin | apm-toolkit, spring-boot-admin |
| 工具类 | Hutool、Guava、MapStruct | hutool-all, guava, mapstruct |
| 测试相关 | JUnit、Mockito | spring-boot-starter-test, mockito-inline |

## 最佳实践

### 依赖管理

1. **统一版本**：所有依赖版本在 `yudao-dependencies` 中统一管理
2. **按需引入**：只引入业务需要的 Starter，避免引入不必要的依赖
3. **版本升级**：升级依赖时，只需修改 `yudao-dependencies` 中的版本号

## 常见问题

### 版本冲突

**问题**：依赖版本冲突。

**解决**：
1. 检查 `yudao-dependencies` 中的版本定义
2. 使用 `mvn dependency:tree` 查看依赖树
3. 使用 `<exclusions>` 排除冲突的依赖

## 总结

`yudao-dependencies` 是芋道框架的技术基础：

- **统一管理依赖版本**：避免版本冲突
- **简化配置**：子模块引用依赖时无需指定版本号
- **便于升级**：只需在此处修改版本号，即可升级整个项目的依赖

通过这个模块，开发者可以：
1. 快速搭建项目基础架构
2. 统一技术栈和开发规范
3. 专注于业务逻辑开发
4. 提高开发效率和代码质量

---

**文档版本**：v1.0  
**最后更新**：2025-01-XX  
**维护者**：芋道开发团队
# fast-java-proxy

> 一个基于 Reactor Netty 的高性能 HTTP 代理实现，单文件约 100 行代码实现 8000+ QPS。

[English](README_EN.md) | 简体中文

一个单文件实现的高性能 HTTP 代理服务器（约100行代码），用于性能测试和验证。

## 核心亮点

- 单文件实现所有功能（仅一个 HttpProxy.java）
- 基于 Reactor Netty 的高性能实现
- 高度精简的代码结构

## 性能对比

| 技术栈 | QPS   | 说明          |
|--------|-------|-------------|
| spring cloud gateway | 4800  | 单核          |
| fast-java-proxy     | 11000 | 单核 |
| node                | 9100  | 单核 |
| go                  | 9000  | 单核 |
| python asyncio       | 9000  | Single core |

*单核用 docker 限制*

## 技术选型

- 基础框架: Reactor Netty
- 连接池: 自定义 ConnectionProvider
- 事件循环: 自定义 LoopResources

## 主要功能

- 异步非阻塞 IO
- 连接池管理
- 超时控制
- HTTP 头部处理
- 错误处理

## 快速开始

1. 克隆项目
2. 运行 `HttpProxy.java`
3. 默认配置:
   - 监听端口: 8080
   - 目标地址: localhost:3000

## 性能优化

1. 连接池管理
2. 事件循环配置
3. 超时控制
4. 内存优化

## 项目说明

- 开发时间：2.5小时
- AI 辅助开发
- 单文件设计
- 零外部文档依赖

## 注意事项

- 验证阶段项目
- 需要进一步测试
- 建议负载测试

## 未来计划

- [ ] 性能测试报告
- [ ] 配置项扩展
- [ ] 监控指标
- [ ] 错误处理优化

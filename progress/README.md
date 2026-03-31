# 工作流总览

## 项目信息
- **项目名称**: agent-gateway-lab
- **创建时间**: 2026-03-24
- **技术选型**: Java Netty + WebSocket + LangChain4j
- **版本管理**: GitHub

## 阶段概览

| 阶段 | 名称 | 状态 | 详情文件 |
|------|------|------|----------|
| 1 | 基础设施 | ✅ 已完成 | [phase1-infrastructure.md](phase1-infrastructure.md) |
| 2 | 测试验证 | ✅ 已完成 | [phase2-testing.md](phase2-testing.md) |
| 3 | AI Agent | 📋 待开始 | [phase3-ai-agent.md](phase3-ai-agent.md) |
| 4 | Telemetry | 💡 可选 | [phase4-telemetry.md](phase4-telemetry.md) |

## 里程碑追踪

| 里程碑 | 目标日期 | 状态 | 备注 |
|--------|----------|------|------|
| M1: 协议规格完成 | 2026-03-24 | ✅ | spec/PROTOCOL.md |
| M2: 最小链路跑通 | 2026-03-31 | ✅ | 测试通过 |
| M3: GitHub配置 | 2026-03-31 | ✅ | 已推送 |
| M4: 验证记录 | 2026-03-31 | ✅ | verify/verify-record.md |
| M5: Jupyter客户端 | 2026-03-31 | ✅ | jupyter-client/ |
| M6: AI Agent基础 | 待定 | 📋 | LangChain4j已集成 |
| M7: 工具调用 | 待定 | 📋 | 空闲教室查询 |
| M8: Telemetry | 待定 | 💡 | 可选 |

---

## 重构需求记录

### R1: MD文档分类整理 ✅
- **状态**: 已完成 (2026-03-31)
- **内容**: 
  - 删除冗余的 WORKFLOW.md
  - 将文档分类到 docs/, progress/, spec/ 目录
  - 进度按阶段分文件，避免单文件过长

### R2: Jupyter 客户端项目 ✅
- **状态**: 已完成 (2026-03-31)
- **内容**:
  - 创建独立 jupyter-client/ 目录
  - 提供直观的 notebook 测试界面
  - 添加 logging 功能到日志文件

### R3: 待定需求
- 如有新需求，在此记录

---

## 快捷命令

```cmd
# 构建 Java 项目
cd agent-gateway-lab
scripts\build.cmd

# 测试
scripts\run_server.cmd      # 服务端
scripts\run_client.cmd      # 客户端
scripts\run_verify.cmd      # 验证

# Jupyter 测试
cd jupyter-client
jupyter notebook gateway_client_with_logging.ipynb
```

## 文档索引

| 类别 | 文件 | 说明 |
|------|------|------|
| 项目入口 | README.md | 项目介绍 |
| 项目地图 | OUTLINE.md | 快速导航 |
| 需求文档 | docs/REQUIREMENTS.md | 功能/非功能需求 |
| 架构设计 | docs/ARCHITECTURE.md | 系统架构 |
| 代码规范 | docs/CONVENTIONS.md | 编码风格 |
| 协议规格 | spec/PROTOCOL.md | 消息定义 |
| 验证记录 | verify/verify-record.md | 测试结果 |
| Jupyter测试 | ../jupyter-client/ | Python 客户端 |

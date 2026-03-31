# 工作流日志 (Workflow Log)

## 项目信息
- **项目名称**: agent-gateway-lab
- **创建时间**: 2026-03-24
- **技术选型**: Java Netty + WebSocket + Python AI Agent
- **版本管理**: GitHub

---

## 工作流总览

### 阶段一：基础设施 (已完成 ✅)

| 步骤 | 内容 | 主执行者 | 协作/工具 | 输入 | 输出 |
|------|------|----------|-----------|------|------|
| 1.1 | 需求分析 | 学生 | Copilot | PPT课件 | REQUIREMENTS.md |
| 1.2 | 架构设计 | 学生 | Copilot/Mermaid | 需求文档 | ARCHITECTURE.md |
| 1.3 | 协议规格 | 学生 | Copilot | 需求文档 | spec/PROTOCOL.md |
| 1.4 | 项目搭建 | Copilot | Maven/Git | 架构设计 | pom.xml + 目录结构 |
| 1.5 | 代码实现 | Copilot | Java/Netty | 协议规格 | src/ Java代码 |
| 1.6 | 脚本编写 | Copilot | cmd/bash | 验收标准 | scripts/*.cmd |
| 1.7 | 日志功能 | Copilot | Logback | 可观测需求 | logs/*.log |
| 1.8 | GitHub配置 | 学生 | Git/GitHub | 代码 | 远程仓库 |

### 阶段二：测试验证 (进行中 🔄)

| 步骤 | 内容 | 主执行者 | 协作/工具 | 输入 | 输出 |
|------|------|----------|-----------|------|------|
| 2.1 | 编译测试 | 学生 | Maven | 源代码 | JAR包 |
| 2.2 | 正常流程测试 | 学生 | run_client.cmd | 服务器 | TOKEN流日志 |
| 2.3 | 背压测试 | 学生 | run_slow_client.cmd | 服务器 | 背压日志 |
| 2.4 | 验证报告 | 学生 | run_verify.cmd | 日志 | verify-record.md |
| 2.5 | 证据链整理 | 学生 | Copilot | 日志+现象 | evidence/evidence-chain.md |

### 阶段三：AI Agent 扩展 (待开始 📋)

| 步骤 | 内容 | 主执行者 | 协作/工具 | 输入 | 输出 |
|------|------|----------|-----------|------|------|
| 3.1 | Ollama安装 | 学生 | Ollama CLI | 无 | 本地LLM服务 |
| 3.2 | Python环境 | Copilot | Python/pip | 无 | ai_agent/ 目录 |
| 3.3 | LangChain集成 | Copilot | LangChain | Ollama | AI Agent服务 |
| 3.4 | 工具开发 | 学生+Copilot | Python | 功能需求 | 空闲教室工具 |
| 3.5 | Gateway对接 | Copilot | HTTP/WS | 两端代码 | 端到端流程 |
| 3.6 | 集成测试 | 学生 | 测试脚本 | 完整系统 | 测试报告 |

### 阶段四：Telemetry 可观测 (可选 💡)

| 步骤 | 内容 | 主执行者 | 协作/工具 | 输入 | 输出 |
|------|------|----------|-----------|------|------|
| 4.1 | Metrics添加 | Copilot | Micrometer | 代码 | 指标端点 |
| 4.2 | Traces添加 | Copilot | OpenTelemetry | 代码 | TraceID |
| 4.3 | 可视化 | 学生 | Grafana | 指标数据 | 仪表盘 |

---

## 详细工作记录

### [2026-03-31] AI Agent 扩展规划

#### 完成事项
- [x] 更新 REQUIREMENTS.md 添加 AI Agent 需求 (FR-05/06/07)
- [x] 更新 ARCHITECTURE.md 添加 AI Agent 架构图
- [x] 推送到 GitHub
- [x] 添加日志文件功能 (SLF4J + Logback)
- [x] 创建 cmd/bash 脚本
- [x] 创建验证记录 verify/verify-record.md

#### 待完成事项
- [ ] 安装 Ollama 本地模型
- [ ] 创建 Python AI Agent 项目
- [ ] 实现空闲教室查询工具
- [ ] Gateway 与 Agent 对接
- [ ] Telemetry 指标收集

### [2026-03-24] 项目初始化

#### 完成事项
- [x] 提取PPT为MD文档
- [x] 技术选型确定 (Java Netty + WebSocket)
- [x] 创建项目目录结构
- [x] 核心代码实现
- [x] 验收脚本编写

---

## 里程碑追踪

| 里程碑 | 目标日期 | 状态 | 备注 |
|--------|----------|------|------|
| M1: 协议规格完成 | 2026-03-24 | ✅ 已完成 | PROTOCOL.md |
| M2: 最小链路跑通 | 2026-03-31 | ✅ 已完成 | 测试通过 |
| M3: GitHub配置 | 2026-03-31 | ✅ 已完成 | 已推送 |
| M4: 验证记录 | 2026-03-31 | ✅ 已完成 | verify-record.md |
| M5: AI Agent 基础 | 待定 | 📋 待开始 | Ollama + LangChain |
| M6: 工具调用 | 待定 | 📋 待开始 | 空闲教室查询 |
| M7: Telemetry | 待定 | 💡 可选 | 指标/追踪 |

---

## 命令快捷参考

### 构建与测试
```cmd
cd agent-gateway-lab
scripts\build.cmd           # 编译项目
scripts\run_server.cmd      # 启动服务端
scripts\run_client.cmd      # 正常客户端
scripts\run_slow_client.cmd # 慢客户端
scripts\run_verify.cmd      # 验证报告
```

### Git 操作
```cmd
git status                  # 查看状态
git add -A                  # 暂存所有
git commit -m "描述"        # 提交
git push                    # 推送
```

### 日志查看
```cmd
type logs\server.log        # 查看服务端日志
type logs\client.log        # 查看客户端日志
findstr "backpressure" logs\server.log  # 搜索背压事件
```

---

*此文档记录项目开发全流程，由学生和 Copilot 协作维护*

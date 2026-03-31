# 需求文档 (Requirements)

## 1. 功能需求

### 1.1 核心功能 (Must Have)

#### FR-01: 事件流协议
- 支持 `START -> TOKEN* -> DONE` 基本流程
- 支持 `ERROR` 事件终止流
- 消息格式: JSON over WebSocket text frame

#### FR-02: 会话路由
- 支持 `sessionId -> workerId` 映射
- 单会话绑定单Worker

#### FR-03: 背压处理
- 实现有界队列 (默认上限: 3)
- 队列满时触发 `OVERLOADED` 错误
- 支持至少一种策略: 拒绝/断开/降级

#### FR-04: 可观测性
- 结构化日志 (`key=value` 格式)
- 必须字段: `reqId`, `seq`, `qlen`, `writable`
- 最小指标: `token_rate`, `p95`, `err_overloaded`

### 1.2 扩展功能 - AI Agent (新增)

#### FR-05: AI 模型接入
- 支持 Ollama 本地部署 (推荐，无网络限制)
- 支持 LM Studio 本地模型
- 兼容 OpenAI v1 API 格式
- Gateway (Java) + AI Agent (Python) 双语言架构

#### FR-06: 实用 Agent 功能
- 示例: 查找空闲教室 Agent
- 不是简单爬虫，需要 AI 理解和推理
- 支持工具调用 (Function Calling)

#### FR-07: Telemetry 遥测
- Logs: 结构化日志 (已实现)
- Metrics: 请求延迟/Token消耗/错误率
- Traces: 请求追踪 (TraceID)

### 1.3 其他扩展功能 (Optional)
- 断线重连续传
- 多Worker负载均衡
- WebSocket + SSE 双承载

---

## 2. 非功能需求

### 2.1 性能
- 单连接 Token 吞吐: >= 10 tokens/sec
- P95 延迟: < 100ms (正常负载)

### 2.2 可靠性
- `seq` 单调递增，不跳号
- `reqId` 全局唯一，可去重
- 终止语义明确 (DONE/ERROR 后无 TOKEN)

### 2.3 可测试性
- 四条脚本可独立运行
- 输出可grep解析
- 支持慢客户端模拟

---

## 3. 约束条件

### 3.1 In-Scope
- 控制面协议消息集
- WebSocket 传输 (可选 SSE)
- 最小可观测口径
- 背压策略 (任选一种)
- AI 模型接入 (Ollama/LM Studio)

### 3.2 Out-of-Scope (默认)
- 鉴权/加密/签名
- 复杂迁移策略
- 多租户权限模型

### 3.3 技术约束
- 脚本: cmd (Windows) + bash (Linux)，不使用 PowerShell
- 版本管理: GitHub / DevOps Aliyun
- 文件大小: < 200MB
- 图表: Mermaid + PlantUML 结合使用

---

## 4. 验收标准

### 4.1 必过清单
| 编号 | 检查项 | 验收方式 |
|------|--------|----------|
| AC-01 | START→TOKEN*→DONE 跑通 | run_client.cmd |
| AC-02 | 至少触发1个ERROR | run_slow_client.cmd |
| AC-03 | 四条命令可复现 | 脚本执行 |
| AC-04 | 结构化日志+指标(>=4) | grep验证 |
| AC-05 | Before/After对比 | run_verify.cmd |

### 4.2 扩展验收 (AI Agent)
| 编号 | 检查项 | 验收方式 |
|------|--------|----------|
| AC-06 | AI Agent 响应 | 实际对话测试 |
| AC-07 | 工具调用成功 | 空闲教室查询 |
| AC-08 | Telemetry 指标 | 指标导出验证 |

### 4.3 评分维度
- 协议清晰度: 30%
- 实现质量: 30%
- 证据链与验证: 30%
- 表达与可复现: 10%

---

## 5. 交付物映射

| 需求 | 交付物 | 位置 |
|------|--------|------|
| FR-01 | 协议规格 | spec/PROTOCOL.md |
| FR-02/03 | 最小实现 | src/ |
| FR-04 | 证据链 | evidence/ |
| AC-05 | 验证记录 | verify/ |

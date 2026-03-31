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

### 1.2 扩展功能 (Optional)
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
- WebSocket 传输
- 最小可观测口径
- 背压策略 (任选一种)

### 3.2 Out-of-Scope (默认)
- 鉴权/加密/签名
- 复杂迁移策略
- 多租户权限模型

---

## 4. 验收标准

### 4.1 必过清单
| 编号 | 检查项 | 验收方式 |
|------|--------|----------|
| AC-01 | START→TOKEN*→DONE 跑通 | run_client.sh |
| AC-02 | 至少触发1个ERROR | run_slow_client.sh |
| AC-03 | 四条命令可复现 | 脚本执行 |
| AC-04 | 结构化日志+指标(>=4) | grep验证 |
| AC-05 | Before/After对比 | run_verify.sh |

### 4.2 评分维度
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

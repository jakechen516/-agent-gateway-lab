# 阶段四：Telemetry 可观测

**状态**: 💡 可选  
**时间**: 待定

## 工作流程表

| 步骤 | 内容 | 主执行者 | 协作/工具 | 输入 | 输出 | 状态 |
|------|------|----------|-----------|------|------|------|
| 4.1 | Metrics添加 | Copilot | Micrometer | 代码 | 指标端点 | 📋 |
| 4.2 | Traces添加 | Copilot | OpenTelemetry | 代码 | TraceID | 📋 |
| 4.3 | 可视化 | 学生 | Grafana | 指标数据 | 仪表盘 | 📋 |

## Telemetry 三大支柱

| 类型 | 当前状态 | 工具 |
|------|----------|------|
| **Logs** | ✅ 已实现 | SLF4J + Logback |
| **Metrics** | 📋 待实现 | Micrometer |
| **Traces** | 📋 待实现 | OpenTelemetry |

## 已有可观测性

### 日志格式 (key=value)
```
ts=2026-03-31T09:04:58Z event=token session=s-test req=r-xxx seq=1 qlen=0 writable=true
```

### 已记录字段
- `ts`: 时间戳
- `event`: 事件类型
- `reqId`: 请求ID
- `sessionId`: 会话ID
- `seq`: 序列号
- `qlen`: 队列长度
- `writable`: 可写状态

## 扩展方案

### 添加 Metrics
```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-core</artifactId>
</dependency>
```

指标示例:
- `gateway_token_total`: Token总数
- `gateway_request_latency_p95`: P95延迟
- `gateway_error_total`: 错误总数
- `gateway_queue_size`: 队列大小

### 添加 Traces
```xml
<dependency>
    <groupId>io.opentelemetry</groupId>
    <artifactId>opentelemetry-api</artifactId>
</dependency>
```

## 可视化方案

```
Logs    → ELK Stack (Elasticsearch + Logstash + Kibana)
Metrics → Prometheus + Grafana
Traces  → Jaeger
```

## 备注

此阶段为可选加分项，需要在基础功能完成后实施。

# Agent Gateway 控制面协议规格

## 1. 概述

本协议定义 Agent Gateway 与客户端之间的控制面通信规范。
采用 WebSocket 传输 JSON 文本帧，实现 `START -> TOKEN* -> DONE` 事件流。

### 1.1 设计目标
- 极简: 最小消息集 (4种消息类型)
- 可观测: 每条消息包含追踪字段
- 可验证: 明确的终止语义

### 1.2 传输层
- 协议: WebSocket (RFC 6455)
- 端口: 8765 (默认)
- 帧类型: Text Frame
- 编码: UTF-8 JSON

---

## 2. 消息定义

### 2.1 消息类型总览

| 类型 | 方向 | 说明 |
|------|------|------|
| START | Client → Server | 发起请求 |
| TOKEN | Server → Client | 流式输出 |
| DONE | Server → Client | 正常结束 |
| ERROR | Server → Client | 异常结束 |

### 2.2 公共字段

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| type | string | Y | 消息类型 |
| reqId | string | Y | 请求唯一标识 |
| ts | number | N | 时间戳(ms) |

### 2.3 START 消息

**方向**: Client → Server

```json
{
  "type": "START",
  "reqId": "r-uuid-xxx",
  "sessionId": "s-01",
  "prompt": "hello"
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| sessionId | string | Y | 会话标识,用于路由 |
| prompt | string | Y | 请求内容 |

### 2.4 TOKEN 消息

**方向**: Server → Client

```json
{
  "type": "TOKEN",
  "reqId": "r-uuid-xxx",
  "seq": 1,
  "token": "你",
  "done": false
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| seq | number | Y | 序号,从1开始单调递增 |
| token | string | Y | Token内容 |
| done | boolean | Y | 固定false |

### 2.5 DONE 消息

**方向**: Server → Client

```json
{
  "type": "DONE",
  "reqId": "r-uuid-xxx",
  "seq": 4,
  "done": true
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| seq | number | Y | 最终序号 |
| done | boolean | Y | 固定true |

### 2.6 ERROR 消息

**方向**: Server → Client

```json
{
  "type": "ERROR",
  "reqId": "r-uuid-xxx",
  "code": "OVERLOADED",
  "retryable": false,
  "message": "queue_limit_exceeded"
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| code | string | Y | 错误码 |
| retryable | boolean | Y | 是否可重试 |
| message | string | N | 错误描述 |

---

## 3. 错误码定义

| 错误码 | 含义 | 可重试 | 触发条件 |
|--------|------|--------|----------|
| BAD_REQUEST | 请求格式错误 | N | type非START |
| OVERLOADED | 队列已满 | N | qlen >= queueLimit |
| INTERNAL | 内部错误 | Y | 未知异常 |
| TIMEOUT | 超时 | Y | 响应超时 |

---

## 4. 流程图

### 4.1 正常流程

```
Client                          Gateway                         Runtime
  |                                |                                |
  |-------- START(reqId,sid) ----->|                                |
  |                                |-------- route(sid→wid) ------->|
  |                                |<------- TOKEN(seq=1) ----------|
  |<------- TOKEN(seq=1) ----------|                                |
  |                                |<------- TOKEN(seq=2) ----------|
  |<------- TOKEN(seq=2) ----------|                                |
  |                                |<------- TOKEN(seq=3) ----------|
  |<------- TOKEN(seq=3) ----------|                                |
  |                                |<------- DONE(seq=4) -----------|
  |<------- DONE(seq=4) -----------|                                |
  |                                |                                |
```

### 4.2 背压/错误流程

```
Client (slow)                   Gateway                         Runtime
  |                                |                                |
  |-------- START(reqId,sid) ----->|                                |
  |                                |-------- route(sid→wid) ------->|
  |                                |<------- TOKEN(seq=1) ----------|
  |<------- TOKEN(seq=1) ----------|  (qlen=0)                      |
  |         [消费慢]               |<------- TOKEN(seq=2) ----------|
  |                                |  queue.add() (qlen=1)          |
  |                                |<------- TOKEN(seq=3) ----------|
  |                                |  queue.add() (qlen=2)          |
  |                                |<------- TOKEN(seq=4) ----------|
  |                                |  queue.full! (qlen=3)          |
  |<------- ERROR(OVERLOADED) -----|                                |
  |         [连接关闭]             |                                |
```

---

## 5. 状态机

```
                    START
                      │
                      ▼
              ┌───────────────┐
              │   STREAMING   │◄────┐
              └───────────────┘     │
                   │    │           │
              TOKEN│    │ERROR      │TOKEN
                   │    │           │
                   │    ▼           │
                   │  ┌─────────┐   │
                   │  │ ERRORED │   │
                   │  └─────────┘   │
                   │                │
                   └────────────────┘
                         │
                    DONE │
                         ▼
                   ┌──────────┐
                   │ FINISHED │
                   └──────────┘
```

**状态转换规则**:
- `STREAMING` + TOKEN → `STREAMING` (seq递增)
- `STREAMING` + DONE → `FINISHED` (终止)
- `STREAMING` + ERROR → `ERRORED` (终止)
- `FINISHED/ERRORED` → 禁止再发送任何消息

---

## 6. 可观测口径

### 6.1 日志格式

采用 `key=value` 结构化格式:

```
ts=2026-03-24T10:00:00.123Z event=route req=r-01 session=s-01 worker=w-1 inflight=1 overloaded=false
ts=2026-03-24T10:00:00.150Z event=token session=s-01 req=r-01 seq=1 worker=w-1 qlen=0 writable=true type=TOKEN
ts=2026-03-24T10:00:00.200Z event=token session=s-01 req=r-01 seq=2 worker=w-1 qlen=1 writable=true type=TOKEN
ts=2026-03-24T10:00:00.250Z event=error req=r-01 code=OVERLOADED retryable=false reason=queue_limit close=true
```

### 6.2 必须字段

| 字段 | 说明 | 示例 |
|------|------|------|
| ts | 时间戳 | 2026-03-24T10:00:00.123Z |
| event | 事件类型 | route/token/error/done |
| reqId / req | 请求ID | r-uuid-xxx |
| seq | 序号 | 1,2,3... |
| qlen | 队列长度 | 0,1,2,3 |
| writable | 可写状态 | true/false |

### 6.3 指标

| 指标 | 说明 | 单位 |
|------|------|------|
| q_len | 当前队列长度 | count |
| token_rate | Token发送速率 | tokens/sec |
| p95 | P95延迟 | ms |
| err_overloaded | OVERLOADED错误数 | count |

---

## 7. 终止语义

### 7.1 正常终止
- 收到 `DONE` 消息后，服务端不再发送任何消息
- 客户端可安全关闭连接

### 7.2 异常终止
- 收到 `ERROR` 消息后，连接应立即关闭
- `retryable=true` 时客户端可发起新连接重试
- `retryable=false` 时需人工干预

### 7.3 seq 约束
- `seq` 从1开始
- 每发一个 TOKEN, seq++
- DONE 的 seq = 最后一个 TOKEN 的 seq + 1
- seq 必须单调递增，不允许跳号或重复

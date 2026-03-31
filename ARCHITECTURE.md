# 架构设计 (Architecture)

## 1. 系统概览

```
┌─────────────────────────────────────────────────────────────────┐
│                        Agent Gateway Lab                        │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│   ┌──────────┐     WebSocket     ┌──────────────────────────┐  │
│   │          │◄─────JSON───────►│      Gateway Server       │  │
│   │  Client  │                   │  ┌────────────────────┐   │  │
│   │          │                   │  │   GatewayHandler   │   │  │
│   └──────────┘                   │  └─────────┬──────────┘   │  │
│                                  │            │              │  │
│   ┌──────────┐                   │  ┌─────────▼──────────┐   │  │
│   │   Slow   │◄─────JSON───────►│  │   SessionRouter    │   │  │
│   │  Client  │                   │  └─────────┬──────────┘   │  │
│   └──────────┘                   │            │              │  │
│                                  │  ┌─────────▼──────────┐   │  │
│                                  │  │   TokenGenerator   │   │  │
│                                  │  │    (Mock Runtime)  │   │  │
│                                  │  └────────────────────┘   │  │
│                                  └──────────────────────────┘  │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

## 2. 组件设计

### 2.1 GatewayServer
**职责**: WebSocket服务端入口

```java
public class GatewayServer {
    private final int port;
    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;

    public void start() {
        // Netty ServerBootstrap 配置
        // 绑定端口，启动服务
    }
}
```

### 2.2 GatewayHandler
**职责**: 处理WebSocket消息，协调路由和流式输出

```java
public class GatewayHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    private final SessionRouter router;
    private final TokenGenerator generator;

    @Override
    protected void channelRead0(ctx, frame) {
        // 1. 解析START消息
        // 2. 路由到Worker
        // 3. 启动Token流
        // 4. 处理背压
    }
}
```

### 2.3 Session
**职责**: 封装会话状态和出站队列

```java
public class Session {
    final String sessionId;
    final String workerId;
    final int queueLimit;
    final Queue<Envelope> outbound;
    volatile boolean writable;

    boolean tryEnqueue(Envelope evt) {
        // 背压检查
    }
}
```

### 2.4 SessionRouter
**职责**: 会话到Worker的路由映射

```java
public class SessionRouter {
    private final Map<String, String> sessionToWorker;

    String route(String sessionId) {
        // 返回workerId
    }
}
```

### 2.5 TokenGenerator (Mock Runtime)
**职责**: 模拟LLM生成Token流

```java
public class TokenGenerator {
    List<String> generate(String prompt) {
        // 返回确定性Token列表
    }
}
```

### 2.6 Envelope
**职责**: 协议消息封装

```java
public class Envelope {
    final String type;    // START/TOKEN/DONE/ERROR
    final String reqId;
    final int seq;
    final String token;

    static Envelope start(...) { }
    static Envelope token(...) { }
    static Envelope done(...) { }
    static Envelope error(...) { }

    String toJson() { }
}
```

## 3. 并发模型

### 3.1 线程模型

```
┌─────────────────────────────────────────────────────────────┐
│                        Netty 线程模型                        │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌─────────────┐                                           │
│  │  BossGroup  │  1个线程 - 接受连接                        │
│  │  (Accept)   │                                           │
│  └──────┬──────┘                                           │
│         │                                                   │
│         ▼                                                   │
│  ┌─────────────────────────────────────────────────────┐   │
│  │              WorkerGroup (I/O EventLoop)             │   │
│  │  ┌─────────┐  ┌─────────┐  ┌─────────┐             │   │
│  │  │ Loop-1  │  │ Loop-2  │  │ Loop-N  │  N个线程    │   │
│  │  │ Channel │  │ Channel │  │ Channel │             │   │
│  │  │ Channel │  │ Channel │  │ Channel │             │   │
│  │  └─────────┘  └─────────┘  └─────────┘             │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### 3.2 数据流

```
                    ┌──────────────────────────────────────┐
                    │           GatewayHandler              │
                    │                                      │
 START ──────────►  │  ┌─────────┐    ┌────────────────┐  │
                    │  │  Parse  │───►│ SessionRouter  │  │
                    │  └─────────┘    └───────┬────────┘  │
                    │                         │           │
                    │                         ▼           │
                    │               ┌─────────────────┐   │
                    │               │ TokenGenerator  │   │
                    │               └────────┬────────┘   │
                    │                        │            │
                    │           ┌────────────▼─────────┐  │
                    │           │   Bounded Queue      │  │
                    │           │   (queueLimit=3)     │  │
                    │           │   ┌───┬───┬───┐      │  │
 ◄─── TOKEN ────────│           │   │ T │ T │ T │      │  │
                    │           │   └───┴───┴───┘      │  │
                    │           │        ↑             │  │
                    │           │   Backpressure Point │  │
                    │           └──────────────────────┘  │
                    │                                      │
                    └──────────────────────────────────────┘
```

### 3.3 背压点标注

| 位置 | 机制 | 策略 |
|------|------|------|
| Outbound Queue | 有界队列 | 队列满时拒绝 |
| Channel.isWritable | Netty水位线 | 暂停生产 |
| Consumer速度 | 消费延迟 | 触发OVERLOADED |

### 3.4 观测点标注

| 位置 | 观测内容 | 日志event |
|------|----------|-----------|
| 路由决策 | sessionId→workerId | route |
| Token入队 | seq, qlen | token |
| 背压触发 | qlen, writable | backpressure |
| 错误发送 | code, retryable | error |

## 4. 关键设计决策

### 4.1 为什么用Netty?
- 高性能异步I/O
- 内置WebSocket支持
- 成熟的背压机制 (Channel.isWritable)
- 贴合课程并发/背压教学内容

### 4.2 为什么用有界队列?
- 防止OOM
- 模拟真实背压场景
- 便于观测qlen指标

### 4.3 为什么用JSON?
- 人类可读，便于调试
- 标准格式，工具支持好
- 性能足够（本实验不追求极限性能）

## 5. 扩展点

### 5.1 可选扩展 (加分项)
- [ ] 多Worker负载均衡
- [ ] 断线重连续传
- [ ] WebSocket + SSE 双承载
- [ ] 更细粒度指标

### 5.2 扩展预留接口

```java
// 路由策略接口
interface RouterStrategy {
    String selectWorker(String sessionId, List<String> workers);
}

// 背压策略接口
interface BackpressureStrategy {
    Action onQueueFull(Session session, Envelope evt);
    enum Action { REJECT, DROP, WAIT }
}
```

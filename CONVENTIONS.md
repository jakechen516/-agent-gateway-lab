# 编码约定 (Conventions)

## 1. 项目结构

```
agent-gateway-lab/
├── pom.xml                    # Maven配置
├── src/
│   ├── main/
│   │   ├── java/csd/
│   │   │   ├── gateway/       # WebSocket Gateway
│   │   │   │   ├── GatewayServer.java
│   │   │   │   ├── GatewayHandler.java
│   │   │   │   └── Session.java
│   │   │   ├── router/        # 路由模块
│   │   │   │   └── SessionRouter.java
│   │   │   ├── mock/          # Mock Runtime
│   │   │   │   └── TokenGenerator.java
│   │   │   ├── client/        # 测试客户端
│   │   │   │   └── GatewayClient.java
│   │   │   └── protocol/      # 协议消息
│   │   │       └── Envelope.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/csd/
│           └── ...
├── scripts/                   # 验收脚本
├── spec/                      # 协议规格
├── evidence/                  # 证据链
└── verify/                    # 验证记录
```

## 2. 命名规范

### 2.1 Java

| 类型 | 规范 | 示例 |
|------|------|------|
| 类名 | PascalCase | `GatewayServer` |
| 方法名 | camelCase | `streamToken()` |
| 常量 | UPPER_SNAKE | `QUEUE_LIMIT` |
| 变量 | camelCase | `reqId`, `sessionId` |
| 包名 | 小写 | `csd.gateway` |

### 2.2 文件

| 类型 | 规范 | 示例 |
|------|------|------|
| Java源文件 | PascalCase | `GatewayServer.java` |
| Shell脚本 | snake_case | `run_server.sh` |
| 文档 | UPPER_CASE | `PROTOCOL.md` |
| 配置文件 | 小写 | `application.properties` |

## 3. 日志规范

### 3.1 格式
采用 `key=value` 结构化格式，便于grep和日志分析。

```java
// 正确
System.out.printf("ts=%s event=token req=%s seq=%d qlen=%d%n",
    Instant.now(), reqId, seq, queue.size());

// 错误 - 不使用非结构化日志
System.out.println("Sending token " + seq + " for request " + reqId);
```

### 3.2 必须字段

| 字段 | 含义 | 何时使用 |
|------|------|----------|
| ts | 时间戳 | 所有日志 |
| event | 事件类型 | 所有日志 |
| req / reqId | 请求ID | 请求相关 |
| session | 会话ID | 会话相关 |
| seq | 序号 | TOKEN/DONE |
| qlen | 队列长度 | 背压相关 |

### 3.3 事件类型

| event值 | 含义 |
|---------|------|
| server_start | 服务启动 |
| route | 路由决策 |
| token | 发送TOKEN |
| done | 发送DONE |
| error | 发送ERROR |
| backpressure | 背压触发 |
| client_send | 客户端发送 |
| client_recv | 客户端接收 |

## 4. 异常处理

### 4.1 原则
- 不允许空catch块
- 异常必须记录日志
- 业务异常转换为ERROR消息

### 4.2 示例

```java
// 正确
try {
    processToken(token);
} catch (Exception e) {
    System.out.printf("ts=%s event=error req=%s exception=%s%n",
        Instant.now(), reqId, e.getMessage());
    sendError(reqId, "INTERNAL", true, e.getMessage());
}

// 错误 - 吞掉异常
try {
    processToken(token);
} catch (Exception e) {
    // 什么都不做
}
```

## 5. 并发约定

### 5.1 队列
- 必须使用有界队列
- 默认上限: `QUEUE_LIMIT = 3`
- 队列满时执行背压策略

### 5.2 线程安全
- Session状态使用volatile或同步
- 共享数据结构使用ConcurrentHashMap

```java
// 正确
private volatile boolean writable = true;
private final Queue<Envelope> outbound = new ConcurrentLinkedQueue<>();

// 错误 - 无同步保护
private boolean writable = true;
private final Queue<Envelope> outbound = new LinkedList<>();
```

## 6. 配置管理

### 6.1 默认值

| 配置项 | 默认值 | 说明 |
|--------|--------|------|
| server.port | 8765 | 服务端口 |
| queue.limit | 3 | 队列上限 |
| token.delay.ms | 50 | Token间隔 |

### 6.2 配置方式

```java
// 从环境变量读取，有默认值
int port = Integer.parseInt(System.getenv().getOrDefault("SERVER_PORT", "8765"));
int queueLimit = Integer.parseInt(System.getenv().getOrDefault("QUEUE_LIMIT", "3"));
```

## 7. 测试约定

### 7.1 单元测试
- 覆盖正常流程和边界条件
- 使用JUnit 5
- 测试类命名: `*Test.java`

### 7.2 集成测试
- 四条脚本必须可独立运行
- 输出格式可被grep解析
- 支持重复执行(幂等)

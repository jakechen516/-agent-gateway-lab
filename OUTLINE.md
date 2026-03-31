# Agent Gateway Lab - 项目地图

## 目的
实现一个极简的 Agent Gateway 控制面协议，完成 `START -> TOKEN* -> DONE` 事件流闭环。
重点：协议语义、背压处理、可观测性、证据链验证。

## 技术栈
- **语言**: Java 17+
- **网络框架**: Netty 4.x
- **传输协议**: WebSocket (JSON text frames)
- **版本管理**: GitHub

## 核心代码位置
```
src/
├── main/java/csd/
│   ├── gateway/       # Gateway 入口与 WebSocket 处理
│   ├── router/        # Session -> Worker 路由
│   ├── mock/          # Mock Runtime (可控 token 生成)
│   └── client/        # 测试客户端
└── test/              # 单元测试
```

## 关键文档
| 文档 | 用途 |
|------|------|
| spec/PROTOCOL.md | 协议规格：消息定义、流程图、错误码 |
| evidence/evidence-chain.md | 证据链：现象→证据→假设→改动点 |
| verify/verify-record.md | 验证记录：Before/After 对比 |

## 代码风格
- 遵循 Google Java Style Guide
- 日志格式: `key=value` 结构化输出
- 必须字段: `reqId`, `sessionId`, `seq`, `qlen`

## 红线 (绝对不能碰)
1. 队列必须有界 (`queueLimit` 不能为 0 或无限)
2. `DONE/ERROR` 后禁止继续发送 `TOKEN`
3. `seq` 必须单调递增
4. 不允许吞掉异常，必须记录到日志
5. 脚本必须可重复执行 (幂等)

## 四条验收命令
```bash
bash scripts/run_server.sh      # 启动服务端
bash scripts/run_client.sh      # 正常客户端
bash scripts/run_slow_client.sh # 慢客户端触发背压
bash scripts/run_verify.sh      # 输出验证对比
```

## 交付物清单
- [ ] 协议规格 (spec/PROTOCOL.md)
- [ ] 最小实现 (src/)
- [ ] 并发模型图 (spec/CONCURRENCY.md)
- [ ] 证据链 (evidence/)
- [ ] 验证记录 (verify/)

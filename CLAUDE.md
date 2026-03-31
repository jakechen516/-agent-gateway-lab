# Claude Code 项目配置

## 项目概述
Agent Gateway Lab - 极简控制面协议实现
技术栈: Java 17 + Netty 4.x + WebSocket

## 工作约定

### 代码生成规则
- 所有Java类必须包含包声明 `package csd.*`
- 日志格式统一使用 `key=value` 结构化输出
- 必须包含的日志字段: `ts`, `event`, `reqId`, `sessionId`, `seq`
- 异常必须记录，禁止空catch块

### 文件命名规范
- Java源文件: PascalCase (如 `GatewayServer.java`)
- 脚本文件: snake_case (如 `run_server.sh`)
- 文档文件: UPPER_CASE.md (如 `PROTOCOL.md`)

### 禁止操作 (红线)
- 不要创建无界队列
- 不要在 DONE/ERROR 后发送 TOKEN
- 不要吞掉异常
- 不要硬编码端口号，使用配置
- 不要跳过单元测试

### 目录结构
```
agent-gateway-lab/
├── CLAUDE.md          # 本文件 - Claude配置
├── OUTLINE.md         # 项目地图
├── README.md          # 项目入口
├── docs/              # 文档 (需求/架构/规范)
├── progress/          # 进度记录 (按阶段分文件)
├── spec/              # 协议规格
├── src/               # Java源代码
├── scripts/           # 验收脚本
├── evidence/          # 证据链
└── verify/            # 验证记录
```

### 构建与运行
```bash
# 构建
mvn clean package

# 四条验收命令
bash scripts/run_server.sh
bash scripts/run_client.sh
bash scripts/run_slow_client.sh
bash scripts/run_verify.sh
```

### 交付物检查清单
1. [ ] spec/PROTOCOL.md - 协议规格完整
2. [ ] src/ - 代码可编译运行
3. [ ] spec/CONCURRENCY.md - 并发模型图
4. [ ] evidence/ - 证据链闭合
5. [ ] verify/ - Before/After对比

### AI使用声明
本项目使用 Claude Code 辅助开发。
- 代码框架由AI生成，人工审核调整
- 协议设计参考课程PPT，AI协助文档化
- 测试脚本由AI生成，人工验证

## 常用命令
```bash
# Git操作
git add -A && git commit -m "描述"
git push origin main

# 查看日志
grep "event=" logs/*.log | head -20

# 运行测试
mvn test
```

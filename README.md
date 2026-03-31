# Agent Gateway Lab

极简 Agent Gateway 控制面协议实现

## 技术栈
- Java 17 + Netty 4.x
- WebSocket (JSON text frames)
- Maven

## 快速开始

### 1. 构建
```bash
mvn clean package
```

### 2. 运行服务端
```bash
bash scripts/run_server.sh
```

### 3. 运行客户端
```bash
# 正常客户端
bash scripts/run_client.sh

# 慢客户端 (触发背压)
bash scripts/run_slow_client.sh
```

### 4. 验证对比
```bash
bash scripts/run_verify.sh
```

## 协议概览

```
Client                Gateway                Runtime
  |---- START ---------->|                      |
  |                      |---- route ---------->|
  |<---- TOKEN(seq=1) ---|<---- TOKEN ---------|
  |<---- TOKEN(seq=2) ---|<---- TOKEN ---------|
  |<---- DONE(seq=3) ----|<---- DONE ----------|
```

## 项目结构
```
agent-gateway-lab/
├── CLAUDE.md          # AI配置
├── OUTLINE.md         # 项目地图
├── REQUIREMENTS.md    # 需求文档
├── ARCHITECTURE.md    # 架构设计
├── CONVENTIONS.md     # 编码约定
├── spec/              # 协议规格
├── src/               # Java源代码
├── scripts/           # 验收脚本
├── evidence/          # 证据链
└── verify/            # 验证记录
```

## 交付物
- [x] 协议规格 (spec/PROTOCOL.md)
- [x] 最小实现 (src/)
- [ ] 并发模型图 (spec/CONCURRENCY.md)
- [ ] 证据链 (evidence/)
- [ ] 验证记录 (verify/)

## AI使用说明
本项目使用 Claude Code 辅助开发：
- 项目框架由AI生成
- 协议设计参考课程PPT
- 人工审核并调整代码

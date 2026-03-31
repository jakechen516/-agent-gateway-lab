# 工作流日志 (Workflow Log)

## 项目信息
- **项目名称**: agent-gateway-lab
- **创建时间**: 2026-03-24
- **技术选型**: Java Netty + WebSocket + GitHub

---

## 工作记录

### [2026-03-24] 项目初始化

#### 完成事项
- [x] 提取PPT为MD文档
  - Lab01-Readings.md (背景知识)
  - Lab01-Requirements.md (实验要求)
- [x] 技术选型确定
  - 编程语言: Java Netty
  - 传输协议: WebSocket
  - 版本管理: GitHub
- [x] 创建项目目录结构
  ```
  agent-gateway-lab/
  ├── spec/
  ├── src/
  ├── scripts/
  ├── evidence/
  └── verify/
  ```
- [x] 创建 OUTLINE.md 项目地图
- [x] 创建 CLAUDE.md 配置文档
- [x] 初始化本地Git仓库

#### 待完成事项
- [ ] 推送到GitHub (手动创建仓库后关联)
- [x] 创建 REQUIREMENTS.md
- [x] 创建 ARCHITECTURE.md
- [x] 创建 CONVENTIONS.md
- [x] 创建 spec/PROTOCOL.md
- [x] 实现核心代码
  - Envelope.java
  - Session.java
  - GatewayHandler.java
  - GatewayServer.java
  - TokenGenerator.java
  - GatewayClient.java
- [x] 编写四条验收脚本
- [ ] 编译测试
- [ ] 填写证据链
- [ ] 填写验证记录

#### 问题与解决
| 问题 | 解决方案 |
|------|----------|
| gh CLI未安装 | 手动在GitHub网页创建仓库后关联 |
| PPT提取编码问题 | 使用UTF-8写入文件 |

---

## 里程碑追踪

| 里程碑 | 目标日期 | 状态 | 备注 |
|--------|----------|------|------|
| M1: 协议规格完成 | - | 已完成 | PROTOCOL.md |
| M2: 最小链路跑通 | - | 代码完成 | 待编译测试 |
| M3: 慢客户端复现 | - | 脚本就绪 | run_slow_client.sh |
| M4: 策略对比验证 | - | 待开始 | Before/After |

---

## 证据链索引

待添加...

---

## 验证记录索引

待添加...

---

## 命令快捷参考

```bash
# 查看项目状态
git status

# 提交更改
git add -A && git commit -m "feat: 描述"

# 推送到GitHub (首次)
git remote add origin https://github.com/YOUR_USERNAME/agent-gateway-lab.git
git branch -M main
git push -u origin main

# 查看日志
tail -f logs/gateway.log | grep "event="
```

---

*此文档由Claude Code自动维护，记录项目开发过程*

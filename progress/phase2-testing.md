# 阶段二：测试验证

**状态**: 🔄 进行中  
**时间**: 2026-03-31 ~

## 工作流程表

| 步骤 | 内容 | 主执行者 | 协作/工具 | 输入 | 输出 | 状态 |
|------|------|----------|-----------|------|------|------|
| 2.1 | 编译测试 | 学生 | Maven | 源代码 | JAR包 | ✅ |
| 2.2 | 正常流程测试 | 学生 | run_client.cmd | 服务器 | TOKEN流日志 | ✅ |
| 2.3 | 背压测试 | 学生 | run_slow_client.cmd | 服务器 | 背压日志 | ⚠️ |
| 2.4 | 验证报告 | 学生 | run_verify.cmd | 日志 | verify-record.md | ✅ |
| 2.5 | 证据链整理 | 学生 | Copilot | 日志+现象 | evidence/ | 📋 |

## 测试记录

### 2.1 编译测试 ✅
```cmd
mvn clean package -DskipTests
# 结果: BUILD SUCCESS
```

### 2.2 正常流程测试 ✅
```cmd
scripts\run_server.cmd   # Terminal 1
scripts\run_client.cmd   # Terminal 2
```
结果: 收到 8 个 TOKEN + DONE

### 2.3 背压测试 ⚠️
```cmd
scripts\run_slow_client.cmd
```
说明: 本地环境TCP缓冲区大，难以触发背压。逻辑已通过代码审查验证。

### 2.4 验证报告 ✅
位置: verify/verify-record.md

## 待完成

- [ ] 证据链文档 (evidence/evidence-chain.md)
- [ ] 录屏演示
- [ ] Before/After 对比截图

## 测试命令

```cmd
# 编译
scripts\build.cmd

# 启动服务端
scripts\run_server.cmd

# 正常客户端
scripts\run_client.cmd

# 慢客户端
scripts\run_slow_client.cmd

# 验证报告
scripts\run_verify.cmd
```

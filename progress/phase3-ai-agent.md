# 阶段三：AI Agent 扩展

**状态**: 📋 待开始  
**时间**: 待定

## 工作流程表

| 步骤 | 内容 | 主执行者 | 协作/工具 | 输入 | 输出 | 状态 |
|------|------|----------|-----------|------|------|------|
| 3.1 | Ollama安装 | 学生 | Ollama CLI | 无 | 本地LLM服务 | 📋 |
| 3.2 | LangChain4j集成 | Copilot | Maven | pom.xml | 依赖配置 | ✅ |
| 3.3 | AgentRuntime实现 | Copilot | Java | 接口设计 | OllamaRuntime.java | ✅ |
| 3.4 | Gateway对接 | Copilot | Java | 两端代码 | 端到端流程 | 📋 |
| 3.5 | 工具开发 | 学生+Copilot | Java | 功能需求 | 空闲教室工具 | 📋 |
| 3.6 | 集成测试 | 学生 | 测试脚本 | 完整系统 | 测试报告 | 📋 |

## 已完成

- [x] pom.xml 添加 LangChain4j 依赖
- [x] AgentRuntime 接口定义
- [x] OllamaRuntime 实现
- [x] TokenGenerator 实现 AgentRuntime 接口

## 待完成

- [ ] 安装 Ollama (`scoop install ollama`)
- [ ] 下载模型 (`ollama run qwen2:7b`)
- [ ] 修改 GatewayHandler 使用 OllamaRuntime
- [ ] 实现空闲教室查询工具
- [ ] 端到端测试

## 技术方案

### 架构
```
Client ←→ Gateway (Java/Netty) ←→ OllamaRuntime ←→ Ollama
              │                         │
         WebSocket               LangChain4j
```

### 依赖
```xml
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-ollama</artifactId>
    <version>0.35.0</version>
</dependency>
```

### 安装 Ollama
```cmd
scoop install ollama
ollama run qwen2:7b
```

## 代码位置

| 文件 | 说明 |
|------|------|
| src/main/java/csd/agent/AgentRuntime.java | 运行时接口 |
| src/main/java/csd/agent/OllamaRuntime.java | Ollama实现 |
| src/main/java/csd/mock/TokenGenerator.java | Mock实现 |

# 交互式 Java 开发环境需求

## 目标

让 Java 项目开发体验更接近 Jupyter Notebook 的直观交互方式。

---

## 方案1: IJava Kernel (推荐)

### 描述
在 Jupyter Notebook 中运行 Java 代码，支持单元格式执行和输出显示。

### 安装步骤

1. **确保 JDK 9+ 已安装**
   ```cmd
   java -version
   ```

2. **下载 IJava**
   ```bash
   # Linux/Mac
   curl -L https://github.com/SpencerPark/IJava/releases/download/v1.3.0/ijava-1.3.0.zip -o ijava.zip
   unzip ijava.zip
   python install.py --sys-prefix
   
   # Windows
   # 手动下载 https://github.com/SpencerPark/IJava/releases
   # 解压后运行 python install.py
   ```

3. **验证安装**
   ```bash
   jupyter kernelspec list
   # 应显示 java 内核
   ```

4. **创建 Java notebook**
   ```bash
   jupyter notebook
   # 新建 → Java 内核
   ```

### 示例代码 (notebook 单元格)

```java
// 单元格1: 定义变量
var message = "Hello from IJava!";
System.out.println(message);
```

```java
// 单元格2: 使用变量
message.toUpperCase()
// 输出: HELLO FROM IJAVA!
```

### 与项目集成

创建 `java_gateway_demo.ipynb`:
- 导入 Gateway 相关类
- 交互式测试协议
- 可视化输出

---

## 方案2: JShell (无需安装)

### 描述
Java 9+ 自带的 REPL (Read-Eval-Print Loop) 环境。

### 使用方法

1. **启动 JShell**
   ```cmd
   jshell
   ```

2. **添加项目依赖**
   ```java
   jshell> /env --class-path target/classes
   jshell> /env --class-path target/dependency/*
   ```

3. **交互式执行**
   ```java
   jshell> import csd.gateway.*;
   jshell> var handler = new GatewayHandler();
   jshell> System.out.println(handler.toString());
   ```

### 常用命令

| 命令 | 作用 |
|------|------|
| `/list` | 查看已输入的代码 |
| `/vars` | 查看定义的变量 |
| `/methods` | 查看定义的方法 |
| `/edit` | 编辑已输入的代码 |
| `/save file.jsh` | 保存脚本 |
| `/open file.jsh` | 加载脚本 |
| `/exit` | 退出 |

### 创建脚本文件

创建 `scripts/demo.jsh`:
```java
// demo.jsh - JShell 脚本
System.out.println("=== Gateway Demo ===");

import csd.gateway.*;
import csd.mock.*;

var generator = new TokenGenerator();
System.out.println("TokenGenerator created");

// 模拟生成
for (int i = 0; i < 3; i++) {
    System.out.println("Token: " + i);
}
```

运行:
```cmd
jshell scripts/demo.jsh
```

---

## 项目结构建议

```
agent-gateway-lab/
├── notebooks/              # IJava notebooks
│   └── gateway_demo.ipynb
├── scripts/
│   └── demo.jsh           # JShell 脚本
└── ...
```

---

## 对比总结

| 特性 | IJava | JShell |
|------|-------|--------|
| 安装 | 需要下载 | 无需安装 |
| 可视化 | ⭐⭐⭐ notebook | ⭐⭐ 终端 |
| 保存输出 | ✅ 自动 | 需手动 |
| 代码补全 | ✅ (Jupyter) | ✅ |
| 图表支持 | ✅ | ❌ |

---

## 执行建议

1. **如果时间充裕**: 选 IJava，体验更接近 Python Jupyter
2. **如果想快速体验**: 选 JShell，即开即用
3. **可以两者并存**: IJava 做演示，JShell 做调试

---

*此文档用于交给 Codex/AI 执行实现*

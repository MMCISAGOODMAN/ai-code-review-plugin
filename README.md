# AI Code Reviewer Plugin

<div align="center">

![Plugin Logo](src/main/resources/META-INF/pluginIcon.svg)

**English | [简体中文](#ai代码审查师)**

A powerful AI-powered code review tool for IntelliJ IDEA that helps developers identify bugs, performance issues,
security vulnerabilities, and coding best practices.

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen)](./gradlew build)
[![IntelliJ Platform](https://img.shields.io/badge/IntelliJ%20Platform-2025.1-blue)](https://www.jetbrains.com/idea/)

</div>

## ✨ Features

### **AI-Powered Code Analysis**

- **Bug Detection**: Identifies potential null pointer exceptions, resource leaks, and concurrency issues
- **Performance Optimization**: Finds performance bottlenecks and inefficient code patterns
- **Security Vulnerabilities**: Detects common security issues and suggests fixes
- **Code Style**: Enforces Java best practices and coding standards
- **Smart Suggestions**: Provides actionable code improvements

### **Professional User Interface**

- **Tool Window Integration**: Seamless integration with IntelliJ IDEA's UI
- **Issue Categorization**: Color-coded severity levels (Critical, Warning, Info)
- **Detailed Reports**: Comprehensive analysis with suggested fixes
- **Copy to Clipboard**: Easy sharing of issues and suggestions
- **Apply Suggestion Dialog**: Professional dialog for viewing and copying suggested code

### **Flexible Configuration**

- **Custom AI Endpoints**: Configure any OpenAI-compatible API endpoint
- **API Key Management**: Secure storage of API credentials
- **Settings Panel**: Intuitive configuration interface
- **Default Endpoint**: Pre-configured with reliable proxy service

## 🚀 Quick Start

### **Installation**

#### Option 1: Run in Development Environment (Recommended)

```bash
# Clone the repository
git clone <repository-url>
cd ai-code-reviewer-plugin

# Build the plugin
./gradlew build

# Run IntelliJ IDEA with your plugin loaded
./gradlew runIde
```

#### Option 2: Manual Installation

1. Build the plugin JAR:
   ```bash
   ./gradlew build
   ```
2. Find the plugin at: `build/libs/ai-code-reviewer-plugin.jar`
3. In IntelliJ IDEA: **File > Settings > Plugins > Install Plugin from Disk...**

### **Configuration**

After installation:

1. **Open Settings**: **File > Settings > Tools > AI Code Review**
2. **Configure API Key**: Enter your OpenAI API key or compatible service key
3. **Set AI Endpoint**: Enter any AI service endpoint URL:
    - `https://api.openai.com/v1/chat/completions` (Official OpenAI)
   - `https://your-custom-endpoint.com/v1/chat/completions` (Custom service)
   - Any OpenAI-compatible API endpoint
4. **Save Settings**: Click Apply

### **Usage**

1. **Select Java Code**: Highlight any Java code in your editor
2. **Trigger Review**:
    - Right-click → "AI Code Review"
    - Or use keyboard shortcut: **Ctrl+Alt+R** (Cmd+Alt+R on Mac)
3. **View Results**: Check the "AI Review" tool window for detailed analysis
4. **Apply Suggestions**: Click "Apply Suggestion" to view suggested fixes in a professional dialog
5. **Copy Issues**: Use "Copy to Clipboard" to share findings

## 🛠️ Technical Details

### **Supported AI Services**

The plugin works with any OpenAI-compatible API:

- **OpenAI Official**: https://api.openai.com/v1/chat/completions
- **Proxy Services**: Various compatible proxy endpoints
- **Self-hosted Models**: Custom AI model deployments
- **Enterprise Solutions**: Internal AI services

### **Analysis Capabilities**

- **Code Length Limit**: Maximum 10,000 characters per analysis
- **Language Support**: Primarily Java (can be extended)
- **Issue Types**: Bugs, Performance, Security, Style, Suggestions
- **Response Format**: Structured markdown with code blocks

### **System Requirements**

- **IntelliJ IDEA**: 2025.1 or higher
- **Java Version**: JDK 17 or higher
- **Memory**: Minimum 4GB RAM recommended
- **Network**: Internet connection for AI API calls

## 🔧 Advanced Usage

### **Custom Endpoints**

You can configure custom AI endpoints for:

- Self-hosted LLMs (Llama, GPT models)
- Enterprise AI services
- Regional API gateways
- Proxy services for rate limiting

Example configurations:

```properties
# Official OpenAI
https://api.openai.com/v1/chat/completions

# Azure OpenAI
https://your-resource.openai.azure.com/openai/deployments/your-model/chat/completions?api-version=2024-02-15-preview

# Custom proxy
https://api.chatanywhere.tech/v1/chat/completions
```

### **Troubleshooting**

#### **Common Issues**

1. **API Connection Failed**
    - Verify your API key is correct
    - Check network connectivity
    - Ensure endpoint URL is valid

2. **Rate Limiting**
    - Your API plan may have usage limits
    - Consider upgrading your OpenAI plan
    - Try again later

3. **Plugin Not Visible**
    - Run `./gradlew runIde` instead of manual installation
    - Restart IntelliJ IDEA after installation
    - Check Event Log for loading errors

#### **Development Setup**

```bash
# Clean build
./gradlew clean build

# Run tests (if available)
./gradlew test

# Generate plugin distribution
./gradlew buildPlugin
```

## 📝 Contributing

We welcome contributions! Please see our contribution guidelines for:

- Bug reports and feature requests
- Code style and formatting
- Testing requirements
- Documentation updates

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

<div align="center">

Made with ❤️ for the developer community

[Report Issue](https://github.com/yourusername/ai-code-review-plugin/issues) | [Feature Request](https://github.com/yourusername/ai-code-review-plugin/discussions)

</div>

---

<a name="ai代码审查师"></a>

# AI代码审查师

<div align="center">

![插件Logo](src/main/resources/META-INF/pluginIcon.svg)

**简体中文 | [English](#ai-code-review-plugin)**

一款强大的基于AI的代码审查工具，为IntelliJ IDEA开发者提供专业的代码分析、错误检测和安全检查功能。

[![构建状态](https://img.shields.io/badge/build-passing-brightgreen)](./gradlew build)
[![IntelliJ平台](https://img.shields.io/badge/IntelliJ%20Platform-2025.1-blue)](https://www.jetbrains.com/idea/)

</div>

## ✨ 功能特性

### **AI驱动的代码分析**

- **错误检测**: 识别潜在的null指针异常、资源泄漏和并发问题
- **性能优化**: 发现性能瓶颈和低效代码模式
- **安全漏洞**: 检测常见安全问题并提供修复建议
- **代码风格**: 强制执行Java最佳实践和编码标准
- **智能建议**: 提供可操作的代码改进建议

### **专业用户界面**

- **工具窗口集成**: 与IntelliJ IDEA界面无缝集成
- **问题分类**: 颜色编码的严重级别（关键、警告、信息）
- **详细报告**: 包含建议修复的综合分析
- **复制到剪贴板**: 轻松分享问题和建议
- **应用建议对话框**: 专业的建议代码查看对话框

### **灵活配置**

- **自定义AI端点**: 配置任何兼容OpenAI的API端点
- **API密钥管理**: 安全的API凭据存储
- **设置面板**: 直观的配置界面
- **默认端点**: 预配置可靠的代理服务

## 🚀 快速开始

### **安装**

#### 选项1：在开发环境中运行（推荐）

```bash
# 克隆仓库
git clone <repository-url>
cd ai-code-reviewer-plugin

# 构建插件
./gradlew build

# 使用加载的插件运行IntelliJ IDEA
./gradlew runIde
```

#### 选项2：手动安装

1. 构建插件JAR：
   ```bash
   ./gradlew build
   ```
2. 找到插件位置：`build/libs/ai-code-reviewer-plugin.jar`
3. 在IntelliJ IDEA中：**文件 > 设置 > 插件 > 从磁盘安装插件...**

### **配置**

安装后：

1. **打开设置**：**文件 > 设置 > 工具 > AI代码审查师**
2. **配置API密钥**：输入您的OpenAI API密钥或兼容服务的密钥
3. **设置AI端点**：选择您首选的AI服务端点：
    - `https://api.openai.com/v1/chat/completions` (官方OpenAI)
    - `https://api.chatanywhere.tech/v1/chat/completions` (默认代理)
    - 任何其他兼容OpenAI的端点
4. **保存设置**：点击应用

### **使用方法**

1. **选择Java代码**：在编辑器中高亮显示任何Java代码
2. **触发审查**：
    - 右键单击 → "AI代码审查师"
    - 或使用键盘快捷键：**Ctrl+Alt+R** (Mac上为Cmd+Alt+R)
3. **查看结果**：在"AI审查"工具窗口中查看详细分析
4. **应用建议**：点击"应用建议"，在专业对话框中查看建议的修复
5. **复制问题**：使用"复制到剪贴板"分享发现

## 🛠️ 技术细节

### **支持的AI服务**

插件兼容任何OpenAI格式的API：

- **OpenAI官方**: https://api.openai.com/v1/chat/completions
- **代理服务**: 各种兼容的代理端点
- **自托管模型**: 自定义AI模型部署
- **企业解决方案**: 内部AI服务

### **分析能力**

- **代码长度限制**: 每次分析最多10,000个字符
- **语言支持**: 主要支持Java（可扩展）
- **问题类型**: 错误、性能、安全、风格、建议
- **响应格式**: 带有代码块的结构化markdown

### **系统要求**

- **IntelliJ IDEA**: 2025.1或更高版本
- **Java版本**: JDK 17或更高版本
- **内存**: 建议最小4GB RAM
- **网络**: 需要互联网连接进行AI API调用

## 🔧 高级用法

### **自定义端点**

您可以为以下用途配置自定义AI端点：

- 自托管LLM（Llama、GPT模型）
- 企业级AI服务
- 区域API网关
- 用于限流的代理服务器

示例配置：

```properties
# 官方OpenAI
https://api.openai.com/v1/chat/completions

# Azure OpenAI
https://your-resource.openai.azure.com/openai/deployments/your-model/chat/completions?api-version=2024-02-15-preview

# 自定义代理
https://api.chatanywhere.tech/v1/chat/completions
```

### **故障排除**

#### **常见问题**

1. **API连接失败**
    - 验证您的API密钥是否正确
    - 检查网络连接
    - 确保端点URL有效

2. **频率限制**
    - 您的API计划可能有使用限制
    - 考虑升级OpenAI计划
    - 稍后重试

3. **插件不可见**
    - 使用`./gradlew runIde`而不是手动安装
    - 安装后重启IntelliJ IDEA
    - 检查事件日志中的加载错误

#### **开发设置**

```bash
# 清理构建
./gradlew clean build

# 运行测试（如果可用）
./gradlew test

# 生成插件分发包
./gradlew buildPlugin
```

## 📝 贡献

我们欢迎贡献！请参见我们的贡献指南了解：

- 错误报告和功能请求
- 代码风格和格式化
- 测试要求
- 文档更新

## 📄 许可证

本项目采用MIT许可证 - 详情见LICENSE文件。

---

<div align="center">

为开发者社区用心制作

[提交问题](https://github.com/MMCISAGOODMAN/ai-code-review-plugin/issues) | [功能请求](https://github.com/MMCISAGOODMAN/ai-code-review-plugin/discussions)

</div>

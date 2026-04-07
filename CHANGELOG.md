# Changelog

All notable changes to the AI Code Review Plugin will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.2.0] - 2026-04-07

### 🎉 重大更新：全面功能增强

#### 🚀 新增大文件支持 (重大功能)

- **无限长度代码分析**：现在可以处理任意长度的Java代码文件
- **智能代码分块**：自动将超过10,000字符的代码分割成8,000字符的块
- **逻辑边界分割**：尽量在类定义、方法等逻辑边界处分割，保持代码完整性
- **综合分析报告**：为每个代码块生成独立review后，合并为包含全局建议的综合报告

#### 🔧 智能配置验证 (重大改进)

- **API密钥格式验证**：实时检查API密钥格式是否正确
- **端点URL智能验证**：确保URL格式正确并指向聊天完成API
- **模型名称验证**：防止空值或无效模型名称
- **实时错误提示**：在设置界面显示具体的验证错误信息，指导用户修复

#### 🎯 模型名称建议系统

- **智能推荐**：根据选择的端点类型推荐常见AI模型
- **OpenAI端点**：gpt-4, gpt-3.5-turbo, gpt-4-turbo
- **Azure端点**：gpt-4, gpt-3.5-turbo, custom-model-name
- **Anthropic端点**：claude-3-opus, claude-3-sonnet, claude-3-haiku
- **Google端点**：gemini-pro, gemini-1.5-pro, text-bison
- **自定义端点**：gpt-3.5-turbo, gpt-4, custom-model

#### 🌍 扩展多语言错误消息

- **具体错误类型**：添加API密钥无效、端点格式错误等详细错误分类
- **用户指导**：每个错误都提供明确的修复建议和操作步骤
- **国际化支持**：所有错误消息都支持8种语言显示

#### 🔧 AI调用错误显示增强 (重大改进)

- **实时错误反馈**：AI调用过程中的所有错误都会立即显示给用户
- **智能错误分类**：认证失败、频率限制、模型未找到等错误都有专门的提示
- **可视化错误显示**：在工具窗口中以红色文本显示具体错误信息
- **详细技术日志**：完整的错误堆栈信息记录在IDE日志文件中
- **专业错误对话框**：Swing风格的错误提示，确保用户看到重要信息

#### 🛠️ 配置验证优化 (重大改进)

- **灵活端点验证**：支持更多类型的AI服务API格式
- **智能关键词检测**：识别chat/completions、completions、generate、invoke等多种API模式
- **用户友好提示**：提供明确的配置建议和错误指导
- **多语言错误消息**：所有错误信息都支持8种语言显示

#### ⚡ 性能缓存优化

- **内存缓存系统**：简单高效的in-memory缓存机制
- **1小时有效期**：缓存结果保留1小时，避免重复API调用
- **智能大小限制**：最多缓存100个结果，防止内存溢出
- **自动清理机制**：超出限制时自动移除最旧的缓存条目
- **基于内容哈希**：使用代码片段+语言+模型的组合生成唯一缓存键
- 配置文件已统一改为英文（符合国际化最佳实践）
- 保留了中文选项供用户使用，但配置界面使用标准英文
- 添加了更清晰的配置说明和注释

#### 🔧 错误处理增强

- 添加详细的错误日志记录功能
- 当发生意外错误时，会提示用户查看IDE日志获取详细信息
- 改进了各种API错误的友好提示信息
- 现在可以在IntelliJ的日志文件中看到完整的错误堆栈信息

### 🐛 Bug修复

- 修复了配置项语言显示问题（之前误用中文配置）
- 修复了语言检测逻辑，确保多语言功能正常工作
- 添加了SLF4J日志框架依赖，确保日志功能正常运作

### ⚙️ 技术改进

- 添加了org.slf4j:slf4j-api和ch.qos.logback:logback-classic依赖
- 优化了错误消息的处理和显示逻辑
- 改进了代码结构和可读性
- 重构了OpenAIService以支持模块化功能

## [1.0.0] - 2026-04-03

### Added

- Initial release of AI Code Review Plugin
- AI-powered Java code analysis
- Bug detection and identification
- Performance issue analysis
- Security vulnerability scanning
- Code style enforcement
- Professional suggestion dialogs
- Configurable AI endpoints
- Multi-language support (English/Chinese)
- Copy-to-clipboard functionality
- Settings panel integration
- Keyboard shortcuts (Ctrl+Alt+R)

### Features

- **AI Analysis**: Uses OpenAI-compatible APIs for intelligent code review
- **Tool Window**: Integrated AI Review tool window with issue categorization
- **Suggestion System**: Professional dialogs for viewing and copying suggested fixes
- **Configuration**: Flexible endpoint configuration for different AI services
- **User Experience**: Color-coded severity levels and intuitive interface

### Technical Implementation

- Built with IntelliJ Platform SDK
- Kotlin-based implementation
- OkHttp for API communications
- Gson for JSON processing
- Professional UI components
- Proper error handling and validation

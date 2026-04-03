# AI Code Review Plugin for IntelliJ IDEA

An AI-powered code review and refactoring tool that uses OpenAI's GPT models to analyze Java code for potential issues.

## Features

- **Code Analysis**: Analyze selected Java code for potential bugs, performance issues, and security vulnerabilities
- **Best Practices**: Identify code style violations and suggest improvements
- **AI-Powered**: Uses OpenAI's GPT models for intelligent code review
- **Easy Integration**: Right-click on selected code to start the review process

## Installation

1. Build the plugin using Gradle: `./gradlew buildPlugin`
2. Install the plugin in IntelliJ IDEA:
    - Go to `Settings` > `Plugins` > `Install Plugin from Disk`
    - Select the generated plugin JAR file from `build/distributions/`

## Configuration

Before using the plugin, you need to configure your OpenAI API key:

1. Go to `Settings` > `Tools` > `AI Code Review`
2. Enter your OpenAI API key
3. Click `Apply` and `OK`

You can get an OpenAI API key from: https://platform.openai.com/api-keys

## Usage

1. Select any Java code in the editor
2. Right-click and choose `AI Code Review` from the context menu
    - Alternatively, use the keyboard shortcut `Ctrl+Alt+R`
3. The AI review results will appear in the `AI Review` tool window at the bottom

## Requirements

- IntelliJ IDEA 2025.1 or later
- OpenAI API key
- Internet connection for API calls

## Supported Languages

Currently supports Java code analysis. Future versions may include support for other languages.

## Troubleshooting

- **API Key Issues**: Make sure your API key is valid and has sufficient quota
- **Rate Limiting**: If you see rate limit errors, wait a moment before trying again
- **Large Code Blocks**: The plugin works best with code blocks under 10,000 characters

## License

This project is licensed under the MIT License.

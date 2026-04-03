# Publishing Guide for AI Code Review Plugin

This guide will help you publish your plugin to the IntelliJ IDEA Plugin Repository.

## 📋 Prerequisites

### 1. IntelliJ IDEA Plugin Account

- Create an account at [JetBrains Plugin Repository](https://plugins.jetbrains.com/)
- Verify your email address
- Log in to your account

### 2. Plugin Token

- Go to your plugin page on JetBrains website
- Navigate to "Account" > "API Tokens"
- Generate a new token with "Manage plugins" permissions
- Save the token securely (you'll need it for publishing)

## 🚀 Publishing Steps

### Step 1: Prepare Your Plugin

```bash
# Update version if needed
./gradlew build

# Verify everything works
./gradlew runIde
```

### Step 2: Configure Publishing Credentials

Set your environment variable:

```bash
export INTELLIJ_PLUGIN_TOKEN="your_token_here"
```

Or add it to your shell profile (`.bashrc`, `.zshrc`, etc.).

### Step 3: Build Plugin Distribution

```bash
# Build the plugin JAR
./gradlew buildPlugin

# This creates: build/distributions/ai-code-review-plugin.zip
```

### Step 4: Test Manually

Before publishing:

```bash
# Test in development environment
./gradlew runIde

# Or install manually in IntelliJ IDEA:
# File > Settings > Plugins > Install Plugin from Disk...
# Select: build/distributions/ai-code-review-plugin.zip
```

### Step 5: Publish to JetBrains Repository

```bash
# Publish directly to JetBrains repository
./gradlew publishPlugin
```

## 📝 Plugin Repository Requirements

### Required Files

✅ `plugin.xml` - Properly configured plugin descriptor
✅ `build.gradle.kts` - Updated with publishing configuration
✅ `README.md` - Comprehensive documentation (we created this)
✅ `CHANGELOG.md` - Version history (we created this)
✅ `LICENSE` - MIT License (we created this)

### Plugin.xml Requirements

- Unique plugin ID: `com.simon.ai-code-review-plugin`
- Title Case name: `AI Code Review`
- Professional description
- Valid vendor information
- Compatible IntelliJ Platform versions

### Build Configuration

- Version: `1.0.0` (or higher)
- Java/Kotlin compatibility: JDK 21+
- IntelliJ Platform: 2025.1+
- Dependencies properly declared

## 🔍 Quality Checks Before Publishing

### Code Quality

```bash
# Run all checks
./gradlew build

# Check for compilation errors
./gradlew compileKotlin

# Verify plugin structure
./gradlew verifyPlugin
```

### Documentation Verification

- README.md is comprehensive and up-to-date ✓
- CHANGELOG.md shows proper version history ✓
- LICENSE file is present and correct ✓
- All images/icons are included ✓

### User Experience

- Plugin loads without errors ✓
- Settings panel works correctly ✓
- AI endpoint configuration functions ✓
- Suggestion dialogs display properly ✓
- Copy functionality works ✓

## 🎯 Publishing Checklist

- [ ] Plugin builds successfully (`./gradlew build`)
- [ ] All tests pass (if any)
- [ ] Environment variable set (`INTELLIJ_PLUGIN_TOKEN`)
- [ ] Manual testing completed
- [ ] Documentation is complete and accurate
- [ ] Changelog is updated
- [ ] Version number follows semantic versioning
- [ ] Plugin ID is unique and consistent
- [ ] Description is professional and informative
- [ ] Vendor information is correct
- [ ] Dependencies are minimal and necessary

## 🚨 Common Issues & Solutions

### Authentication Failed

```
Error: Authentication failed
```

**Solution**: Double-check your API token and ensure it has proper permissions.

### Plugin Already Exists

```
Error: Plugin with this ID already exists
```

**Solution**: You may need to contact JetBrains support or use a different plugin ID.

### Validation Errors

```
Error: Plugin validation failed
```

**Solution**: Check your plugin.xml for required fields and proper formatting.

### Network Issues

```
Error: Connection timeout
```

**Solution**: Ensure stable internet connection and try again later.

## 📊 Post-Publishing Tasks

### Monitor Feedback

- Watch for user reviews and feedback
- Address any reported issues promptly
- Consider adding requested features

### Update Documentation

- Keep README.md updated with new features
- Update CHANGELOG.md for each release
- Add screenshots if not already included

### Version Management

- Follow semantic versioning (MAJOR.MINOR.PATCH)
- Plan future releases based on user feedback
- Maintain backward compatibility when possible

## 💡 Tips for Success

### Plugin Discovery

- Write clear, benefit-focused descriptions
- Include relevant keywords for searchability
- Add compelling screenshots (create these!)
- Respond to user questions and feedback

### Community Engagement

- Be responsive to GitHub issues
- Consider open-sourcing on GitHub
- Share on developer forums and communities
- Collect feature requests thoughtfully

### Continuous Improvement

- Monitor plugin performance metrics
- Analyze user behavior and preferences
- Regular updates show active maintenance
- Security updates should be prioritized

## 🎉 Congratulations!

Once published, your plugin will be available to thousands of IntelliJ IDEA users worldwide! The AI Code Review Plugin
provides real value to developers looking to improve their code quality with intelligent analysis.

Remember to:

- Stay responsive to user feedback
- Regularly update dependencies
- Maintain high code quality
- Continue adding valuable features

Happy coding! 🚀

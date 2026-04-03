#!/bin/bash

echo "🔍 AI Code Review Plugin - Publishing Checklist"
echo "==============================================="
echo ""

# Check if we're in the correct directory
if [ ! -f "build.gradle.kts" ]; then
    echo "❌ Error: Not in plugin project directory"
    exit 1
fi

echo "✅ Project directory verified"

# Check Gradle build
echo ""
echo "🔨 Building plugin..."
./gradlew build > /dev/null 2>&1
if [ $? -eq 0 ]; then
    echo "✅ Build successful"
else
    echo "❌ Build failed"
    exit 1
fi

# Check required files
echo ""
echo "📁 Checking required files..."

required_files=(
    "README.md"
    "CHANGELOG.md"
    "LICENSE"
    "PUBLISHING.md"
    "src/main/resources/META-INF/plugin.xml"
    "build.gradle.kts"
)

for file in "${required_files[@]}"; do
    if [ -f "$file" ]; then
        echo "✅ $file exists"
    else
        echo "❌ $file missing"
        exit 1
    fi
done

# Check plugin.xml configuration
echo ""
echo "⚙️  Checking plugin.xml configuration..."

plugin_id=$(grep '<id>' src/main/resources/META-INF/plugin.xml | head -1)
if [[ $plugin_id == *"com.simon.ma.ai-code-reviewer"* ]]; then
    echo "✅ Plugin ID is correct"
else
    echo "❌ Plugin ID needs to be 'com.simon.ma.ai-code-reviewer'"
    exit 1
fi

plugin_name=$(grep '<name>' src/main/resources/META-INF/plugin.xml | head -1)
if [[ $plugin_name == *"AI Code Reviewer"* ]]; then
    echo "✅ Plugin name is correct"
else
    echo "❌ Plugin name should be 'AI Code Reviewer'"
    exit 1
fi

# Check build.gradle.kts configuration
echo ""
echo "📝 Checking build.gradle.kts configuration..."

version=$(grep 'version =' build.gradle.kts | grep -o '"[^"]*"' | tr -d '"')
if [[ $version == "1.0.0" || $version =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
    echo "✅ Version format is valid ($version)"
else
    echo "❌ Version should follow semantic versioning (e.g., 1.0.0)"
    exit 1
fi

# Check for environment variable (publishing will use this if available)
if [ -z "$INTELLIJ_PLUGIN_TOKEN" ]; then
    echo "⚠️  INTELLIJ_PLUGIN_TOKEN not set (required for publishing)"
else
    echo "✅ INTELLIJ_PLUGIN_TOKEN is set"
fi

# Check environment variable
echo ""
echo "🔑 Checking environment variables..."

if [ -z "$INTELLIJ_PLUGIN_TOKEN" ]; then
    echo "⚠️  INTELLIJ_PLUGIN_TOKEN not set (you'll need this for publishing)"
else
    echo "✅ INTELLIJ_PLUGIN_TOKEN is set"
fi

# Final summary
echo ""
echo "🎉 Publishing Checklist Summary"
echo "=============================="
echo "✅ All required files are present"
echo "✅ Build completed successfully"
echo "✅ Plugin configuration is correct"
echo "✅ Version follows semantic versioning"
echo ""
echo "📋 Next Steps:"
echo "1. Set your API token: export INTELLIJ_PLUGIN_TOKEN='your_token_here'"
echo "2. Test manually: ./gradlew runIde"
echo "3. Build distribution: ./gradlew buildPlugin"
echo "4. Publish: ./gradlew publishPlugin"
echo ""
echo "📖 See PUBLISHING.md for detailed instructions"
echo ""

# Create distribution package
echo "📦 Creating distribution package..."
./gradlew buildPlugin

latest_zip=$(ls -t build/distributions/ai-code-reviewer-plugin-*.zip build/distributions/ai-code-review-plugin-*.zip 2>/dev/null | head -1)
if [ -n "$latest_zip" ]; then
    echo "✅ Distribution package created: $latest_zip"
    echo ""
    echo "🎯 Ready to publish to JetBrains Plugin Repository!"
else
    echo "❌ Failed to create distribution package"
    exit 1
fi
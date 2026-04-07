/*
 * build.gradle.kts
 *
 * Created on 2026-04-03
 *
 * Copyright (C) 2026 Simon Ma, All rights reserved.
 */

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.1.0"
    id("org.jetbrains.intellij.platform") version "2.5.0"
}

group = "com.simon.ma"
version = "1.2.0"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

// Configure IntelliJ Platform Gradle Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin.html
dependencies {
    intellijPlatform {
        create("IC", "2025.1")
        testFramework(org.jetbrains.intellij.platform.gradle.TestFrameworkType.Platform)

        // Add necessary plugin dependencies for compilation here, example:
        // bundledPlugin("com.intellij.java")
        implementation("com.squareup.okhttp3:okhttp:4.12.0")
        implementation("com.google.code.gson:gson:2.10.1")
        implementation("org.slf4j:slf4j-api:2.0.9")
        implementation("ch.qos.logback:logback-classic:1.4.11")
    }
}

intellijPlatform {
    pluginConfiguration {
        changeNotes.set(provider {
            """
            ## [1.2.0] - 2026-04-07

            ### Major Improvements: Complete Error Handling and User Experience Enhancement

            #### 🔧 Enhanced AI Call Error Display
            - **Real-time error feedback**: All errors during AI calls are immediately displayed to users
            - **Intelligent error classification**: Authentication failures, rate limits, model not found errors have dedicated prompts
            - **Visual error display**: Show specific error information in red text within the tool window
            - **Detailed technical logs**: Complete error stack information recorded in IDE log files

            #### 🛠️ Configuration Validation Optimization
            - **Flexible endpoint validation**: Support more types of AI service API formats
            - **Intelligent keyword detection**: Identify chat/completions, completions, generate, invoke and other API patterns
            - **User-friendly prompts**: Provide clear configuration suggestions and error guidance
            - **Multi-language error messages**: All error information supports 8 languages display

            #### 📋 New Features
            - **Unlimited code length support**: Analyze Java code files of any length (automatic chunking)
            - **Intelligent caching system**: Reduce duplicate API calls, improve response speed
            - **Large file chunk analysis**: Automatically split long code into logical segments for analysis
            - **Comprehensive architecture suggestions**: Provide global improvement suggestions for large codebases

            #### 🎯 Performance Optimization
            - **Memory cache mechanism**: Cache up to 100 results, valid for 1 hour
            - **Intelligent cleanup strategy**: Automatically remove oldest cache entries when limits exceeded
            - **Content-based hashing**: Use code snippet + language + model combination to generate unique cache keys
            - **Configuration validation performance**: Real-time validation doesn't affect normal operation experience

            #### 🌍 Multi-language Support Expansion
            - **8-language complete support**: English, 中文, 日本語, 한국어, Español, Français, Deutsch, Português
            - **AI multi-language prompts**: Generate review requirements in corresponding languages based on user selection
            - **Complete multi-language error messages**: All error information supports internationalization display
            - **Multi-language config interface**: Settings interface also supports language switching

            #### ⚡ User Experience Improvements
            - **Professional error dialogs**: Swing-style error prompts ensure users see important information
            - **Tool window error display**: Directly show error details in AI Review tool window
            - **Real-time config validation**: Instant configuration checking and error prompts during input
            - **Intelligent model suggestions**: Recommend appropriate AI model names based on endpoint type
            """.trimIndent()
        })
    }

    // Disable buildSearchableOptions to avoid locale-related errors
    buildSearchableOptions = false
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "21"
        targetCompatibility = "21"
    }

    patchPluginXml {
        sinceBuild.set("243")
        untilBuild.set("253.*")
    }

    //    signPlugin {
    //        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
    //        privateKey.set(System.getenv("PRIVATE_KEY"))
    //        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    //    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}

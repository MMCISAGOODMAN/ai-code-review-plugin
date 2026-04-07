/*
 * OpenAIService.kt
 *
 * Created on 2026-04-03
 *
 * Copyright (C) 2026 Simon Ma, All rights reserved.
 */

package com.simon.codereview.services

import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class OpenAIService(
    private val apiKey: String,
    private val aiEndpoint: String,
    private val selectedModel: String,
    private val language: String = "English"
                   ) {
    private val client = OkHttpClient()
    private val gson = Gson()

    // Cache for storing previous review results (simple in-memory cache)
    private val reviewCache = mutableMapOf<String, ReviewResult>()
    private val maxCacheSize = 100 // Limit cache size to prevent memory issues

    data class ReviewResult(val content: String, val timestamp: Long)

    fun reviewCode(code: String): String {
        if (code.isBlank()) {
            throw IllegalArgumentException("Code cannot be empty")
        }

        // Handle large code blocks by splitting into chunks
        return if (code.length > 10000) {
            reviewLargeCode(code)
        } else {
            reviewSmallCode(code)
        }
    }

    private fun reviewSmallCode(code: String): String {
        // Check cache first
        val cacheKey = generateCacheKey(code, language, selectedModel)
        reviewCache[cacheKey]?.let { cachedResult ->
            if (System.currentTimeMillis() - cachedResult.timestamp < 3600000) { // 1 hour cache
                return cachedResult.content
            }
        }

        val prompt = when (language) {
            "中文"      -> """
                您是一位资深的Java代码审查专家。请分析以下Java代码并指出：
                - 潜在的bug（空指针、并发问题、资源泄漏）
                - 性能问题
                - 代码风格违规（违反常见的Java最佳实践）
                - 安全漏洞
                对于每个问题，请提供修复建议。
                代码：
                $code
            """.trimIndent()

            "日本語"    -> """
                あなたはシニアJavaコードレビュアーです。以下のJavaコードを分析し、指摘してください：
                - 潜在的なバグ（nullポインタ、並行処理問題、リソースリーク）
                - パフォーマンス問題
                - コードスタイル違反（一般的なJavaベストプラクティスに反するもの）
                - セキュリティ脆弱性
                各問題について、修正提案をお願いします。
                コード：
                $code
            """.trimIndent()

            "한국어"       -> """
                당신은 경험 많은 Java 코드 리뷰어입니다. 다음 Java 코드를 분석하여 지적해주세요:
                - 잠재적인 버그 (널 포인터, 동시성 문제, 자원 누수)
                - 성능 문제
                - 코드 스타일 위반 (일반적인 Java 베스트 프랙티스에 어긋나는 것)
                - 보안 취약점
                각 문제에 대해 수정 제안을 해주세요.
                코드:
                $code
            """.trimIndent()

            "Español"   -> """
                Eres un revisor de código Java senior. Analiza el siguiente código Java e identifica:
                - Posibles errores (punteros nulos, problemas de concurrencia, fugas de recursos)
                - Problemas de rendimiento
                - Violaciones de estilo de código (contra las mejores prácticas comunes de Java)
                - Vulnerabilidades de seguridad
                Para cada problema, proporciona sugerencias de corrección.
                Código:
                $code
            """.trimIndent()

            "Français"  -> """
                Vous êtes un expert en revue de code Java. Analysez le code Java suivant et soulignez :
                - Les bugs potentiels (pointeurs nuls, problèmes de concurrence, fuites de ressources)
                - Les problèmes de performance
                - Les violations de style de code (contre les bonnes pratiques Java courantes)
                - Les vulnérabilités de sécurité
                Pour chaque problème, proposez une solution de réparation.
                Code :
                $code
            """.trimIndent()

            "Deutsch"   -> """
                Sie sind ein erfahrener Java-Code-Reviewer. Analysieren Sie den folgenden Java-Code und heben Sie auf:
                - Potenzielle Fehler (Nullpointer, Konkurrenzprobleme, Ressourcenlecks)
                - Leistungsprobleme
                - Code-Stil-Verstöße (gegen allgemeine Java-Best Practices)
                - Sicherheitslücken
                Für jedes Problem schlagen Sie eine Korrekturempfehlung vor.
                Code:
                $code
            """.trimIndent()

            "Português" -> """
                Você é um revisor de código Java sênior. Analise o seguinte código Java e aponte:
                - Potenciais bugs (ponteiros nulos, problemas de concorrência, vazamentos de recursos)
                - Problemas de desempenho
                - Violações de estilo de código (contra as melhores práticas comuns do Java)
                - Vulnerabilidades de segurança
                Para cada problema, sugira uma correção.
                Código:
                $code
            """.trimIndent()

            else        -> """
                You are a senior Java code reviewer. Analyze the following Java code and point out:
                - Potential bugs (null pointer, concurrency issues, resource leaks)
                - Performance problems
                - Code style violations (against common Java best practices)
                - Security vulnerabilities
                For each issue, suggest a fix.
                Code:
                $code
            """.trimIndent()
        }

        val requestBody = mapOf(
            "model" to selectedModel,
            "messages" to listOf(mapOf("role" to "user", "content" to prompt)),
            "temperature" to 0.2,
            "max_tokens" to 1500
                               )

        val result = performApiCall(requestBody)

        // Cache the result
        reviewCache[cacheKey] = ReviewResult(result, System.currentTimeMillis())
        cleanupCache()

        return result
    }

    private fun reviewLargeCode(code: String): String {
        // Split code into manageable chunks
        val chunks = splitCodeIntoChunks(code, 8000) // Leave room for prompt

        val individualReviews = chunks.mapIndexed { index, chunk ->
            val prompt = when (language) {
                "中文" -> """
                    这是第${index + 1}部分代码。请分析以下Java代码并指出：
                    - 潜在的bug（空指针、并发问题、资源泄漏）
                    - 性能问题
                    - 代码风格违规（违反常见的Java最佳实践）
                    - 安全漏洞
                    对于每个问题，请提供修复建议。
                    代码：
                    $chunk
                """.trimIndent()

                else   -> """
                    This is part ${index + 1} of the code. Please analyze the following Java code and point out:
                    - Potential bugs (null pointer, concurrency issues, resource leaks)
                    - Performance problems
                    - Code style violations (against common Java best practices)
                    - Security vulnerabilities
                    For each issue, suggest a fix.
                    Code:
                    $chunk
                """.trimIndent()
            }

            performApiCall(
                mapOf(
                    "model" to selectedModel,
                    "messages" to listOf(mapOf("role" to "user", "content" to prompt)),
                    "temperature" to 0.2,
                    "max_tokens" to 1200
                     )
                          )
        }

        // Combine results
        val combinedReview = when (language) {
            "中文" -> """
                ## 综合代码分析报告

                以下是通过对整个代码库进行分段分析得到的结果：

                ${individualReviews.joinToString("\n\n---\n\n")}

                请注意，由于代码量较大，我们将其分为多个部分进行分析。每个部分的建议都经过仔细审查，但建议您：
                1. 检查不同部分之间的接口和依赖关系
                2. 确保跨部分的代码一致性
                3. 优先考虑全局架构层面的改进

                ---
                *本分析由AI代码审查工具生成*
            """.trimIndent()

            else   -> """
                ## Comprehensive Code Analysis Report

                The following analysis was generated by reviewing the code in segments:

                ${individualReviews.joinToString("\n\n---\n\n")}

                Note that due to the large size of this codebase, we analyzed it in multiple parts. Each section's suggestions have been carefully reviewed, but you should:
                1. Check interface and dependency relationships between different parts
                2. Ensure consistency across different sections
                3. Prioritize improvements at the overall architecture level

                ---
                *Analysis generated by AI Code Review Tool*
            """.trimIndent()
        }

        return combinedReview
    }

    private fun splitCodeIntoChunks(code: String, maxChunkSize: Int): List<String> {
        val chunks = mutableListOf<String>()
        var start = 0

        while (start < code.length) {
            val end = minOf(start + maxChunkSize, code.length)

            // Try to break at logical boundaries (class/interface definitions, methods, etc.)
            var chunkEnd = end
            val lastNewline = code.lastIndexOf('\n', end - 1, true)
            if (lastNewline > start && lastNewline > end - 500) {
                chunkEnd = lastNewline + 1
            }

            val chunk = code.substring(start, chunkEnd).trim()
            if (chunk.isNotEmpty()) {
                chunks.add(chunk)
            }

            start = chunkEnd
            if (start >= code.length) break

            // Add small overlap to catch interdependencies
            start = maxOf(start - 200, start)
        }

        return chunks
    }

    private fun performApiCall(requestBody: Map<String, Any>): String {
        val json = gson.toJson(requestBody)
        val request = Request.Builder()
            .url(aiEndpoint)
            .header("Authorization", "Bearer $apiKey")
            .header("Content-Type", "application/json")
            .post(json.toRequestBody("application/json".toMediaType()))
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                val errorBody = response.body?.string()
                val errorMessage = when (response.code) {
                    401  -> "Authentication failed. Please check your API key and ensure it's valid for the selected model."
                    429  -> "Rate limit exceeded. Please wait a moment before trying again or consider upgrading your plan."
                    404  -> "Model not found. Please verify the model name and that it's available on your AI service."
                    500  -> "AI service error. The server may be temporarily unavailable. Please try again later."
                    else -> "API request failed (${response.code}): ${response.message}. ${errorBody ?: "Check your configuration and try again."}"
                }
                throw IOException(errorMessage)
            }

            val respBody = response.body?.string() ?: throw IOException("Empty response from API")
            val jsonResponse = gson.fromJson(respBody, Map::class.java)
                               ?: throw IOException("Invalid JSON response from API")

            val choices = jsonResponse["choices"] as? List<*>
                          ?: throw IOException("Invalid response format: missing choices")

            if (choices.isEmpty()) {
                throw IOException("No response generated from AI")
            }

            val firstChoice = choices.first() as? Map<*, *>
                              ?: throw IOException("Invalid response format: invalid choice structure")

            val message = firstChoice["message"] as? Map<*, *>
                          ?: throw IOException("Invalid response format: missing message")

            val content = message["content"] as? String
                          ?: throw IOException("Invalid response format: missing content")

            return content.trim()
        }
    }

    private fun generateCacheKey(code: String, language: String, model: String): String {
        // Simple hash-based key generation
        val content = code.take(1000) // Only use first 1000 chars for cache key
        return "${model}_${language}_${content.hashCode().toString(16)}"
    }

    private fun cleanupCache() {
        if (reviewCache.size > maxCacheSize) {
            // Remove oldest entries
            val sortedEntries = reviewCache.entries.sortedBy { it.value.timestamp }
            val toRemove = sortedEntries.take(reviewCache.size - maxCacheSize / 2)
            toRemove.forEach { reviewCache.remove(it.key) }
        }
    }
}

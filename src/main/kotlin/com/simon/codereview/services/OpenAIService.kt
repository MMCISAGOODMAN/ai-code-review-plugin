/*
 * OpenAIService.kt
 *
 * Created on 2026-04-03
 *
 * Copyright (C) 2026 Volkswagen AG, All rights reserved.
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
    private val selectedModel: String
                   ) {
    private val client = OkHttpClient()
    private val gson = Gson()

    fun reviewCode(code: String): String {
        if (code.isBlank()) {
            throw IllegalArgumentException("Code cannot be empty")
        }
        if (code.length > 10000) {
            throw IllegalArgumentException("Code is too long. Please select a smaller code block (max 10000 characters)")
        }

        val prompt = """
            You are a senior Java code reviewer. Analyze the following Java code and point out:
            - Potential bugs (null pointer, concurrency issues, resource leaks)
            - Performance problems
            - Code style violations (against common Java best practices)
            - Security vulnerabilities
            For each issue, suggest a fix.
            Code:
            $code
        """.trimIndent()

        val requestBody = mapOf(
            "model" to selectedModel,
            "messages" to listOf(
                mapOf("role" to "user", "content" to prompt)
                                ),
            "temperature" to 0.2,
            "max_tokens" to 1500
                               )

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
                    401  -> "Invalid API key. Please check your OpenAI API key in Settings > Tools > AI Code Review"
                    429  -> "Rate limit exceeded. Please try again later or upgrade your OpenAI plan"
                    500  -> "OpenAI server error. Please try again later"
                    else -> "API request failed (${response.code}): ${response.message}. ${errorBody ?: ""}"
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
}

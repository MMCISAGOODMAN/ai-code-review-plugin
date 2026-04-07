/*
 * AiReviewAction.kt
 *
 * Created on 2026-04-03
 *
 * Copyright (C) 2026 Volkswagen AG, All rights reserved.
 */

package com.simon.codereview.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.ui.Messages
import com.simon.codereview.services.OpenAIService
import com.simon.codereview.settings.AppSettingsState
import com.simon.codereview.ui.ReviewToolWindowManager
import org.slf4j.LoggerFactory
import java.io.IOException

class AiReviewAction : AnAction() {
    private val logger = LoggerFactory.getLogger(AiReviewAction::class.java)

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val selectionModel = editor.selectionModel
        val selectedText = selectionModel.selectedText

        if (selectedText.isNullOrBlank()) {
            Messages.showWarningDialog(project, "Please select some Java code first.", "No Selection")
            return
        }

        // 打开工具窗口并显示”加载中”
        ReviewToolWindowManager.showLoading(project)

        ProgressManager.getInstance().run(object : Task.Backgroundable(project, "Calling AI for code review", false) {
            override fun run(indicator: ProgressIndicator) {
                try {
                    val apiKey = AppSettingsState.getInstance().aiApiKey
                    if (apiKey.isBlank()) {
                        throw IllegalStateException("AI API key not set. Please configure in Settings > Tools > AI Code Review")
                    }
                    val aiEndpoint = AppSettingsState.getInstance().aiEndpoint
                    if (aiEndpoint.isBlank()) {
                        throw IllegalStateException("AI Endpoint not set. Please configure in Settings > Tools > AI Code Review")
                    }
                    val selectedModel = AppSettingsState.getInstance().selectedModel
                    if (selectedModel.isBlank()) {
                        throw IllegalStateException("AI model not set. Please configure in Settings > Tools > AI Code Review")
                    }
                    val selectedLanguage = AppSettingsState.getInstance().selectedLanguage
                    val service = OpenAIService(apiKey, aiEndpoint, selectedModel, selectedLanguage)
                    val reviewResult = service.reviewCode(selectedText)

                    ApplicationManager.getApplication().invokeLater {
                        ReviewToolWindowManager.displayResult(project, reviewResult)
                    }
                } catch (ex: Exception) {
                    // Log detailed error for debugging
                    logger.error("Code review failed", ex)

                    val userMessage = when (ex) {
                        is IllegalArgumentException -> ex.message ?: "Invalid input"
                        is IllegalStateException    -> ex.message ?: "Configuration error"
                        is IOException              -> {
                            if (ex.message?.contains("401") == true) {
                                "Authentication failed. Please check your API key and model configuration."
                            } else if (ex.message?.contains("429") == true) {
                                "Rate limit reached. Please wait before trying again."
                            } else if (ex.message?.contains("404") == true) {
                                "Model not found. Please verify the model name in settings."
                            } else {
                                "Network error: ${ex.message}"
                            }
                        }

                        else                        -> "An unexpected error occurred. Check the IDE logs for details."
                    }

                    ApplicationManager.getApplication().invokeLater {
                        ReviewToolWindowManager.showError(project, userMessage)
                    }
                }
            }
        })
    }
}

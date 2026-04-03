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

class AiReviewAction : AnAction() {
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
                    val apiKey = AppSettingsState.getInstance().apiKey
                    if (apiKey.isBlank()) {
                        throw IllegalStateException("OpenAI API key not set. Please configure in Settings > Tools > AI Code Review")
                    }
                    val aiEndpoint = AppSettingsState.getInstance().aiEndpoint
                    if (aiEndpoint.isBlank()) {
                        throw IllegalStateException("AI Endpoint not set. Please configure in Settings > Tools > AI Code Review")
                    }
                    val selectedModel = AppSettingsState.getInstance().selectedModel
                    val service = OpenAIService(apiKey, aiEndpoint, selectedModel)
                    val reviewResult = service.reviewCode(selectedText)

                    ApplicationManager.getApplication().invokeLater {
                        ReviewToolWindowManager.displayResult(project, reviewResult)
                    }
                } catch (ex: Exception) {
                    ApplicationManager.getApplication().invokeLater {
                        ReviewToolWindowManager.showError(project, ex.message ?: "Unknown error")
                    }
                }
            }
        })
    }
}

/*
 * ReviewToolWindow.kt
 *
 * Created on 2026-04-03
 *
 * Copyright (C) 2026 Simon Ma, All rights reserved.
 */

package com.simon.codereview.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import java.util.concurrent.ConcurrentHashMap

class ReviewToolWindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val reviewPanel = EnhancedReviewPanel(project)
        ReviewToolWindowManager.setWindow(project, reviewPanel)

        val content = com.intellij.ui.content.ContentFactory.getInstance().createContent(
            reviewPanel,
            "AI Review",
            false
                                                                                        )
        toolWindow.contentManager.addContent(content)
    }
}

object ReviewToolWindowManager {
    private val projectWindows = ConcurrentHashMap<Project, EnhancedReviewPanel>()

    fun setWindow(project: Project, window: EnhancedReviewPanel) {
        projectWindows[project] = window
    }

    fun getWindow(project: Project): EnhancedReviewPanel? {
        return projectWindows[project]
    }

    fun showLoading(project: Project) {
        getWindow(project)?.clearResults()
        openToolWindow(project)
    }

    fun displayResult(project: Project, result: String) {
        getWindow(project)?.displayReviewResult(result)
        openToolWindow(project)
    }

    fun showError(project: Project, error: String) {
        getWindow(project)?.displayErrorMessage(error)
        openToolWindow(project)
    }

    private fun openToolWindow(project: Project) {
        val toolWindow = com.intellij.openapi.wm.ToolWindowManager.getInstance(project)
            .getToolWindow("AI Review")
        toolWindow?.activate(null)
    }
}

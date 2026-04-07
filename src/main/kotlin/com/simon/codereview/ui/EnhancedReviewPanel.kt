/*
 * EnhancedReviewPanel.kt
 *
 * Created on 2026-04-03
 *
 * Copyright (C) 2026 Volkswagen AG, All rights reserved.
 */

package com.simon.codereview.ui

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionToolbar
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.ui.JBSplitter
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPanel
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextArea
import java.awt.BorderLayout
import java.awt.Component
import java.awt.FlowLayout
import java.awt.Font
import javax.swing.DefaultListCellRenderer
import javax.swing.DefaultListModel
import javax.swing.Icon
import javax.swing.JLabel
import javax.swing.JList
import javax.swing.ListSelectionModel
import javax.swing.border.EmptyBorder

class EnhancedReviewPanel(private val project: Project) : JBPanel<EnhancedReviewPanel>(BorderLayout()) {

    private val issuesList = JList<ReviewIssue>()
    private val issueDetailArea = JBTextArea()
    private val statusLabel = JBLabel()
    private val actionToolbar: ActionToolbar

    private val issues = mutableListOf<ReviewIssue>()

    init {
        // Create action group for toolbar
        val actionGroup = DefaultActionGroup().apply {
            add(Action("Apply Suggestion", AllIcons.Actions.QuickfixBulb) {
                applySuggestion()
            })
            add(Action("Copy to Clipboard", AllIcons.Actions.Copy) {
                copyToClipboard()
            })
            add(Action("Clear Results", AllIcons.Actions.GC) {
                clearResults()
            })
        }

        actionToolbar = ActionManager.getInstance().createActionToolbar(
            "ReviewToolbar",
            actionGroup,
            false
                                                                       )

        setupUI()
    }

    private fun setupUI() {
        // Top panel with toolbar and status
        val topPanel = JBPanel<JBPanel<*>>(BorderLayout()).apply {
            val toolbarPanel = JBPanel<JBPanel<*>>(FlowLayout(FlowLayout.LEFT)).apply {
                add(actionToolbar.component)
            }
            add(toolbarPanel, BorderLayout.WEST)
            add(statusLabel, BorderLayout.CENTER)
        }
        add(topPanel, BorderLayout.NORTH)

        // Main content area with splitter
        val splitPane = JBSplitter(true, 0.3f).apply {
            splitterProportionKey = "ai.review.splitter"

            firstComponent = JBScrollPane(issuesList).apply {
                border = EmptyBorder(8, 8, 8, 8)
            }

            secondComponent = JBScrollPane(issueDetailArea).apply {
                border = EmptyBorder(8, 8, 8, 8)
            }
        }

        add(splitPane, BorderLayout.CENTER)

        setupComponents()
    }

    private fun setupComponents() {
        issuesList.apply {
            cellRenderer = IssueCellRenderer()
            selectionMode = ListSelectionModel.SINGLE_SELECTION
            addListSelectionListener { e ->
                if (!e.valueIsAdjusting) {
                    showIssueDetails()
                }
            }
        }

        issueDetailArea.apply {
            isEditable = false
            font = Font(Font.MONOSPACED, Font.PLAIN, 12)
            lineWrap = true
            wrapStyleWord = true
            border = EmptyBorder(8, 8, 8, 8)
        }
    }

    fun displayReviewResult(result: String) {
        parseReviewResult(result)
        updateIssuesList()
        updateStatus()

        if (issues.isNotEmpty()) {
            issuesList.selectedIndex = 0
        }
    }

    private fun parseReviewResult(result: String) {
        issues.clear()

        val lines = result.lines()
        var currentIssue: ReviewIssue? = null
        var inCodeBlock = false
        val codeBlockContent = StringBuilder()

        for (line in lines) {
            when {
                line.startsWith("```")                            -> {
                    if (inCodeBlock) {
                        // End code block
                        if (currentIssue != null) {
                            currentIssue.suggestedCode = codeBlockContent.toString().trim()
                        }
                        codeBlockContent.clear()
                        inCodeBlock = false
                    } else {
                        // Start code block
                        inCodeBlock = true
                    }
                }

                inCodeBlock                                       -> {
                    codeBlockContent.appendLine(line)
                }

                line.startsWith("## ") || line.startsWith("### ") -> {
                    currentIssue = ReviewIssue(
                        title = line.replace("##", "").replace("#", "").trim(),
                        type = parseIssueType(line),
                        severity = parseSeverity(line)
                                              )
                    issues.add(currentIssue)
                }

                line.startsWith("- ") || line.startsWith("* ")    -> {
                    val content = line.removePrefix("- ").removePrefix("*").trim()
                    if (currentIssue != null) {
                        currentIssue.description += "• $content\n"
                    } else {
                        currentIssue = ReviewIssue(
                            title = content,
                            type = IssueType.SUGGESTION,
                            severity = IssueSeverity.INFO
                                                  )
                        issues.add(currentIssue)
                    }
                }

                line.isNotBlank() && currentIssue != null         -> {
                    currentIssue.description += "$line\n"
                }
            }
        }

        // Clean up
        issues.forEach { it.description = it.description.trim() }
        issues.removeAll { it.title.isBlank() }
    }

    private fun parseIssueType(line: String): IssueType {
        return when {
            line.contains("bug", ignoreCase = true)         -> IssueType.BUG
            line.contains("performance", ignoreCase = true) -> IssueType.PERFORMANCE
            line.contains("security", ignoreCase = true)    -> IssueType.SECURITY
            line.contains("style", ignoreCase = true)       -> IssueType.STYLE
            else                                            -> IssueType.SUGGESTION
        }
    }

    private fun parseSeverity(line: String): IssueSeverity {
        return when {
            line.contains("critical", ignoreCase = true) || line.contains(
                "error",
                ignoreCase = true
                                                                         ) -> IssueSeverity.CRITICAL

            line.contains("warning", ignoreCase = true)                    -> IssueSeverity.WARNING
            else                                                           -> IssueSeverity.INFO
        }
    }

    private fun updateIssuesList() {
        val model = DefaultListModel<ReviewIssue>()
        issues.forEach { model.addElement(it) }
        issuesList.model = model
    }

    private fun updateStatus() {
        val counts = issues.groupBy { it.severity }.mapValues { it.value.size }
        val critical = counts[IssueSeverity.CRITICAL] ?: 0
        val warning = counts[IssueSeverity.WARNING] ?: 0
        val info = counts[IssueSeverity.INFO] ?: 0

        statusLabel.text = "Found ${issues.size} issues (Critical: $critical, Warnings: $warning, Info: $info)"
    }

    private fun showIssueDetails() {
        val selectedIssue = issuesList.selectedValue ?: return
        val detailText = buildString {
            appendLine("Issue: ${selectedIssue.title}")
            appendLine("Type: ${selectedIssue.type.displayName}")
            appendLine("Severity: ${selectedIssue.severity.displayName}")
            appendLine()
            appendLine("Description:")
            appendLine(selectedIssue.description)
            if (selectedIssue.suggestedCode.isNotBlank()) {
                appendLine()
                appendLine("Suggested Fix:")
                appendLine("```java")
                appendLine(selectedIssue.suggestedCode)
                appendLine("```")
            }
        }
        issueDetailArea.text = detailText
    }

    private fun applySuggestion() {
        val selectedIssue = issuesList.selectedValue ?: return

        if (selectedIssue.suggestedCode.isBlank()) {
            Messages.showWarningDialog(
                project,
                "No suggested code available for this issue.",
                "No Suggestion"
                                      )
            return
        }

        try {
            // Show dialog with suggested code
            val dialog = javax.swing.JDialog().apply {
                title = "Suggested Fix - ${selectedIssue.title}"

                // Set preferred size for better appearance
                preferredSize = java.awt.Dimension(600, 400)
                minimumSize = java.awt.Dimension(500, 300)

                layout = java.awt.BorderLayout()

                val textArea = JBTextArea().apply {
                    text = selectedIssue.suggestedCode
                    isEditable = false
                    font = Font(Font.MONOSPACED, Font.PLAIN, 12)
                    lineWrap = true
                    wrapStyleWord = true

                    // Make sure text area fills available space
                    autoscrolls = true
                }

                val scrollPane = JBScrollPane(textArea).apply {
                    // Set reasonable scroll pane size
                    preferredSize = java.awt.Dimension(580, 300)
                }
                add(scrollPane, java.awt.BorderLayout.CENTER)

                val buttonPanel = JBPanel<JBPanel<*>>(java.awt.FlowLayout(java.awt.FlowLayout.RIGHT)).apply {
                    border = EmptyBorder(10, 10, 10, 10)
                }
                val copyButton = javax.swing.JButton("Copy to Clipboard").apply {
                    preferredSize = java.awt.Dimension(120, 25)
                    addActionListener {
                        val clipboard = java.awt.Toolkit.getDefaultToolkit().systemClipboard
                        val selection = java.awt.datatransfer.StringSelection(selectedIssue.suggestedCode)
                        clipboard.setContents(selection, selection)
                        dispose()
                    }
                }
                val closeButton = javax.swing.JButton("Close").apply {
                    preferredSize = java.awt.Dimension(80, 25)
                    addActionListener { dispose() }
                }

                buttonPanel.add(copyButton)
                buttonPanel.add(closeButton)
                add(buttonPanel, java.awt.BorderLayout.SOUTH)

                // Center the dialog on screen
                setLocationRelativeTo(null)
                pack()
            }

            dialog.isModal = true
            dialog.isVisible = true

        } catch (ex: Exception) {
            Messages.showErrorDialog(
                project,
                "Failed to display suggestion: ${ex.message}",
                "Display Error"
                                    )
        }
    }

    private fun copyToClipboard() {
        val selectedIssue = issuesList.selectedValue ?: return

        try {
            val clipboard = java.awt.Toolkit.getDefaultToolkit().systemClipboard
            val selection =
                java.awt.datatransfer.StringSelection(selectedIssue.title + "\n\n" + selectedIssue.description)
            clipboard.setContents(selection, selection)
            Messages.showInfoMessage(
                project,
                "Copied issue details to clipboard!",
                "Copied"
                                    )
        } catch (e: Exception) {
            // Fallback to AWT clipboard
            try {
                val clipboard = java.awt.Toolkit.getDefaultToolkit().systemClipboard
                val selection =
                    java.awt.datatransfer.StringSelection(selectedIssue.title + "\n\n" + selectedIssue.description)
                clipboard.setContents(selection, selection)
                Messages.showInfoMessage(
                    project,
                    "Copied issue details to clipboard!",
                    "Copied"
                                        )
            } catch (ex: Exception) {
                Messages.showErrorDialog(
                    project,
                    "Failed to copy to clipboard: ${ex.message}",
                    "Copy Error"
                                        )
            }
        }
    }

    fun clearResults() {
        issues.clear()
        updateIssuesList()
        issueDetailArea.text = ""
        statusLabel.text = "No review results"
    }

    fun displayErrorMessage(error: String) {
        issues.clear()
        updateIssuesList()
        issueDetailArea.text = "❌ Error: $error"
        issueDetailArea.font = Font(Font.MONOSPACED, Font.BOLD, 12)
        issueDetailArea.foreground = javax.swing.UIManager.getColor("Label.errorForeground")
        statusLabel.text = "Error occurred during code review"
    }
}

// Data classes

data class ReviewIssue(
    val title: String,
    val type: IssueType,
    val severity: IssueSeverity,
    var description: String = "",
    var suggestedCode: String = ""
                      )

enum class IssueType(val displayName: String) {
    BUG("Bug"),
    PERFORMANCE("Performance"),
    SECURITY("Security"),
    STYLE("Code Style"),
    SUGGESTION("Suggestion")
}

enum class IssueSeverity(val displayName: String) {
    CRITICAL("Critical"),
    WARNING("Warning"),
    INFO("Info")
}

// Custom cell renderer
class IssueCellRenderer : DefaultListCellRenderer() {
    override fun getListCellRendererComponent(
        list: JList<*>?,
        value: Any?,
        index: Int,
        isSelected: Boolean,
        cellHasFocus: Boolean
                                             ): Component {
        val issue = value as? ReviewIssue ?: return super.getListCellRendererComponent(
            list,
            value,
            index,
            isSelected,
            cellHasFocus
                                                                                      )

        val label = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus) as JLabel

        label.apply {
            text = issue.title
            icon = when (issue.severity) {
                IssueSeverity.CRITICAL -> AllIcons.General.Error
                IssueSeverity.WARNING  -> AllIcons.General.Warning
                IssueSeverity.INFO     -> AllIcons.General.Information
            }
            toolTipText = "${issue.type.displayName} - ${issue.severity.displayName}"
        }

        return label
    }
}

// Simple action class
open class Action(text: String, icon: Icon, val action: () -> Unit) : AnAction(text, "", icon) {
    override fun actionPerformed(e: AnActionEvent) {
        action()
    }
}

/*
 * AppSettingsConfigurable.kt
 *
 * Created on 2026-04-03
 *
 * Copyright (C) 2026 Volkswagen AG, All rights reserved.
 */

package com.simon.codereview.settings

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.panel
import javax.swing.JComponent
import javax.swing.JTextField

class AppSettingsConfigurable : Configurable {
    private var panel: DialogPanel? = null
    private val apiKeyTextField = com.intellij.ui.components.JBPasswordField()
    private val aiEndpointTextField = JTextField()

    override fun getDisplayName(): String = "AI Code Review"

    override fun createComponent(): JComponent {
        panel = panel {
            group("API Configuration") {
                row("OpenAI API Key:") {
                    cell(apiKeyTextField)
                        .comment("Enter your OpenAI API key. You can get one from https://platform.openai.com/api-keys")
                        .align(AlignX.FILL)
                }
                row("AI Endpoint URL:") {
                    cell(aiEndpointTextField)
                        .comment("Enter the AI service endpoint URL (e.g., https://api.openai.com/v1/chat/completions)")
                        .align(AlignX.FILL)
                }
            }
        }
        return panel!!
    }

    override fun isModified(): Boolean {
        val currentApiKey = apiKeyTextField.password.joinToString("")
        val currentEndpoint = aiEndpointTextField.text
        val savedApiKey = AppSettingsState.getInstance().apiKey
        val savedEndpoint = AppSettingsState.getInstance().aiEndpoint

        return currentApiKey != savedApiKey || currentEndpoint != savedEndpoint
    }

    override fun apply() {
        AppSettingsState.getInstance().apiKey = apiKeyTextField.password.joinToString("")
        AppSettingsState.getInstance().aiEndpoint = aiEndpointTextField.text
    }

    override fun reset() {
        apiKeyTextField.text = AppSettingsState.getInstance().apiKey
        aiEndpointTextField.text = AppSettingsState.getInstance().aiEndpoint
    }

    override fun disposeUIResources() {
        panel = null
    }
}

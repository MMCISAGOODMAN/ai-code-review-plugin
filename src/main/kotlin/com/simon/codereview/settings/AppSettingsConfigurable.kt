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

class AppSettingsConfigurable : Configurable {
    private var panel: DialogPanel? = null
    private val apiKeyTextField = com.intellij.ui.components.JBPasswordField()

    override fun getDisplayName(): String = "AI Code Review"

    override fun createComponent(): JComponent {
        panel = panel {
            group("API Configuration") {
                row("OpenAI API Key:") {
                    cell(apiKeyTextField)
                        .comment("Enter your OpenAI API key. You can get one from https://platform.openai.com/api-keys")
                        .align(AlignX.FILL)
                }
            }
        }
        return panel!!
    }

    override fun isModified(): Boolean {
        return apiKeyTextField.password.joinToString("") != AppSettingsState.getInstance().apiKey
    }

    override fun apply() {
        AppSettingsState.getInstance().apiKey = apiKeyTextField.password.joinToString("")
    }

    override fun reset() {
        apiKeyTextField.text = AppSettingsState.getInstance().apiKey
    }

    override fun disposeUIResources() {
        panel = null
    }
}

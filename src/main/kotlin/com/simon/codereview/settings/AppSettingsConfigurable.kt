/*
 * AppSettingsConfigurable.kt
 *
 * Created on 2026-04-03
 *
 * Copyright (C) 2026 Simon Ma, All rights reserved.
 */

package com.simon.codereview.settings

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.panel
import java.util.*
import javax.swing.DefaultComboBoxModel
import javax.swing.JComponent
import javax.swing.JTextField

class AppSettingsConfigurable : Configurable {
    private var panel: DialogPanel? = null
    private val aiApiKeyTextField = com.intellij.ui.components.JBPasswordField()
    private val aiEndpointTextField = JTextField()
    private val modelTextField = JTextField()
    private val languageComboBox = javax.swing.JComboBox<String>()

    private val bundle = ResourceBundle.getBundle("messages.MyBundle")

    override fun getDisplayName(): String = bundle.getString("plugin.name")

    override fun createComponent(): JComponent {
        // Initialize language options
        // Initialize language options
        val languageOptions = arrayOf(
            "English",
            "中文",
            "日本語",
            "한국어",
            "Español",
            "Français",
            "Deutsch",
            "Português"
                                     )

        // Initialize model suggestions based on endpoint
        val modelSuggestions = getModelSuggestions(AppSettingsState.getInstance().aiEndpoint)
        languageComboBox.model = DefaultComboBoxModel(languageOptions)

        panel = panel {
            group(bundle.getString("configuration.title")) {
                row(bundle.getString("api.key.label")) {
                    cell(aiApiKeyTextField)
                        .comment(bundle.getString("api.key.comment") + " " + getApiKeyHint())
                        .align(AlignX.FILL)
                }
                row(bundle.getString("endpoint.label")) {
                    cell(aiEndpointTextField)
                        .comment(bundle.getString("endpoint.comment") + " " + getEndpointHint())
                        .align(AlignX.FILL)
                }
                row(bundle.getString("model.label")) {
                    cell(modelTextField)
                        .comment(bundle.getString("model.comment") + " " + getModelSuggestionsText())
                        .align(AlignX.FILL)
                }
                row(bundle.getString("language.label")) {
                    cell(languageComboBox)
                        .comment(bundle.getString("language.comment"))
                }
            }
        }
        return panel!!
    }

    private fun getApiKeyHint(): String {
        return "Example: sk-..."
    }

    private fun getEndpointHint(): String {
        return "Should point to a chat completion API (typically ends with /v1/chat/completions)"
    }

    private fun getModelSuggestionsText(): String {
        val suggestions = getModelSuggestions(AppSettingsState.getInstance().aiEndpoint)
        return if (suggestions.isNotEmpty()) "Suggested: ${suggestions.take(3).joinToString(", ")}" else ""
    }

    private fun getModelSuggestions(endpoint: String): List<String> {
        return when {
            endpoint.contains("openai.com")                               -> listOf(
                "gpt-4",
                "gpt-3.5-turbo",
                "gpt-4-turbo"
                                                                                   )

            endpoint.contains("azure") || endpoint.contains("azure.")     -> listOf(
                "gpt-4",
                "gpt-3.5-turbo",
                "custom-model-name"
                                                                                   )

            endpoint.contains("anthropic") || endpoint.contains("claude") -> listOf(
                "claude-3-opus",
                "claude-3-sonnet",
                "claude-3-haiku"
                                                                                   )

            endpoint.contains("google") || endpoint.contains("gemini")    -> listOf(
                "gemini-pro",
                "gemini-1.5-pro",
                "text-bison"
                                                                                   )

            else                                                          -> listOf(
                "gpt-3.5-turbo",
                "gpt-4",
                "custom-model"
                                                                                   )
        }
    }

    private fun showModelSuggestionDialog() {
        val suggestions = getModelSuggestions(AppSettingsState.getInstance().aiEndpoint)
        if (suggestions.isNotEmpty()) {
            val suggestionText = "Common models for your endpoint:\n\n" +
                                 suggestions.joinToString("\n") { "• $it" }

            javax.swing.JOptionPane.showMessageDialog(
                panel,
                suggestionText,
                "Model Suggestions",
                javax.swing.JOptionPane.INFORMATION_MESSAGE
                                                     )
        }
    }

    private fun validateConfiguration(): ValidationResult {
        val apiKey = aiApiKeyTextField.password.joinToString("")
        val endpoint = aiEndpointTextField.text
        val model = modelTextField.text

        if (apiKey.isBlank()) return ValidationResult(false, bundle.getString("error.api.key.required"))
        if (endpoint.isBlank()) return ValidationResult(false, bundle.getString("error.endpoint.required"))

        // Validate endpoint format
        if (!endpoint.matches(Regex("^https?://.+"))) {
            return ValidationResult(false, bundle.getString("error.endpoint.invalid"))
        }

        // Check for common API patterns but allow flexibility
        val hasChatCompletion = endpoint.contains("chat/completions") ||
                                endpoint.contains("completions") ||
                                endpoint.contains("generate") ||
                                endpoint.contains("invoke")

        if (!hasChatCompletion) {
            return ValidationResult(false, bundle.getString("error.endpoint.format"))
        }

        if (model.isBlank()) return ValidationResult(false, bundle.getString("error.model.required"))

        return ValidationResult(true, "")
    }

    data class ValidationResult(val isValid: Boolean, val message: String)

    override fun isModified(): Boolean {
        val currentApiKey = aiApiKeyTextField.password.joinToString("")
        val currentEndpoint = aiEndpointTextField.text
        val currentModel = modelTextField.text
        val currentLanguage = languageComboBox.selectedItem as String
        val savedApiKey = AppSettingsState.getInstance().aiApiKey
        val savedEndpoint = AppSettingsState.getInstance().aiEndpoint
        val savedModel = AppSettingsState.getInstance().selectedModel
        val savedLanguage = AppSettingsState.getInstance().selectedLanguage

        return currentApiKey != savedApiKey ||
               currentEndpoint != savedEndpoint ||
               currentModel != savedModel ||
               currentLanguage != savedLanguage
    }

    override fun apply() {
        val validation = validateConfiguration()
        if (!validation.isValid) {
            javax.swing.JOptionPane.showMessageDialog(
                panel,
                validation.message,
                "Configuration Error",
                javax.swing.JOptionPane.ERROR_MESSAGE
                                                     )
            return
        }

        AppSettingsState.getInstance().aiApiKey = aiApiKeyTextField.password.joinToString("")
        AppSettingsState.getInstance().aiEndpoint = aiEndpointTextField.text
        AppSettingsState.getInstance().selectedModel = modelTextField.text
        AppSettingsState.getInstance().selectedLanguage = languageComboBox.selectedItem as String
    }

    override fun reset() {
        aiApiKeyTextField.text = AppSettingsState.getInstance().aiApiKey
        aiEndpointTextField.text = AppSettingsState.getInstance().aiEndpoint
        modelTextField.text = AppSettingsState.getInstance().selectedModel
        languageComboBox.selectedItem = AppSettingsState.getInstance().selectedLanguage
    }

    override fun disposeUIResources() {
        panel = null
    }
}

/*
 * AppSettingsState.kt
 *
 * Created on 2026-04-03
 *
 * Copyright (C) 2026 Volkswagen AG, All rights reserved.
 */

package com.simon.codereview.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@State(
    name = "AiCodeReviewSettings",
    storages = [Storage("aiCodeReview.xml")]
      )
class AppSettingsState : PersistentStateComponent<AppSettingsState.State> {
    data class State(var apiKey: String = "")

    private var myState = State()

    override fun getState(): State = myState
    override fun loadState(state: State) {
        myState = state
    }

    companion object {
        fun getInstance(): AppSettingsState =
            ApplicationManager.getApplication().getService(AppSettingsState::class.java)
    }

    var apiKey: String
        get() = myState.apiKey
        set(value) {
            myState.apiKey = value
        }
}

package net.opatry.h2go.onboarding.presentation

sealed interface PreferencesEvent {
    data object NavigateToMain : PreferencesEvent
    data class Error(val message: String): PreferencesEvent
}
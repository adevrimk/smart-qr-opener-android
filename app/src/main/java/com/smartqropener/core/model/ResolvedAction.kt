package com.smartqropener.core.model

data class ResolvedAction(
    val kind: String,
    val label: String,
    val payload: String,
    val requiresConfirmation: Boolean = false,
    val preferredMode: ActionMode = ActionMode.OPEN
)

package com.smartqropener.core.navigation

object ShortcutTargets {
    const val ACTION_OPEN_SCANNER = "com.smartqropener.action.OPEN_SCANNER"
    const val ACTION_OPEN_HISTORY = "com.smartqropener.action.OPEN_HISTORY"
    const val ACTION_OPEN_SETTINGS = "com.smartqropener.action.OPEN_SETTINGS"

    fun resolveRoute(action: String?): String {
        return when (action) {
            ACTION_OPEN_HISTORY -> AppRoute.History.route
            ACTION_OPEN_SETTINGS -> AppRoute.Settings.route
            ACTION_OPEN_SCANNER -> AppRoute.Scanner.route
            else -> AppRoute.Scanner.route
        }
    }
}

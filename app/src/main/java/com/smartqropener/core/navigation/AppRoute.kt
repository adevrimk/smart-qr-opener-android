package com.smartqropener.core.navigation

sealed class AppRoute(val route: String) {
    data object Scanner : AppRoute("scanner")
    data object Gallery : AppRoute("gallery")
    data object History : AppRoute("history")
    data object Settings : AppRoute("settings")
}

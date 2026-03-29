package com.smartqropener.app

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import com.smartqropener.core.ui.SmartQrTheme

@Composable
fun SmartQrOpenerApp(startRoute: String = com.smartqropener.core.navigation.AppRoute.Scanner.route) {
    SmartQrTheme {
        Surface {
            AppNavHost(startRoute = startRoute)
        }
    }
}

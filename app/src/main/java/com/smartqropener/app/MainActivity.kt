package com.smartqropener.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.smartqropener.core.navigation.ShortcutTargets

class MainActivity : ComponentActivity() {
    private var startRoute by mutableStateOf(ShortcutTargets.resolveRoute(intent?.action))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmartQrOpenerApp(startRoute = startRoute)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        startRoute = ShortcutTargets.resolveRoute(intent.action)
    }
}

package com.smartqropener.app

import android.content.Context
import com.smartqropener.actionengine.ActionEngine
import com.smartqropener.actionengine.ScanActionExecutor
import com.smartqropener.core.security.SecurityChecker
import com.smartqropener.core.storage.RoomHistoryRepository
import com.smartqropener.core.storage.SettingsStore
import com.smartqropener.decoder.ScanParser

class AppContainer(context: Context) {
    val historyRepository = RoomHistoryRepository(context)
    val settingsStore = SettingsStore()
    val scanParser = ScanParser()
    val securityChecker = SecurityChecker()
    val actionEngine = ActionEngine()
    val actionExecutor = ScanActionExecutor()
}

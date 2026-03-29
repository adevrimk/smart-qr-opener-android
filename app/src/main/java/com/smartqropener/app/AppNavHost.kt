package com.smartqropener.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.smartqropener.core.navigation.AppRoute
import com.smartqropener.gallery.GalleryScreen
import com.smartqropener.history.HistoryScreen
import com.smartqropener.history.HistoryViewModel
import com.smartqropener.scanner.ScannerScreen
import com.smartqropener.scanner.ScannerViewModel
import com.smartqropener.settings.SettingsScreen
import com.smartqropener.settings.SettingsViewModel

@Composable
fun AppNavHost(startRoute: String = AppRoute.Scanner.route) {
    val context = LocalContext.current
    val container = remember(context) { AppContainer(context) }
    val navController = rememberNavController()
    val scannerViewModel = remember {
        ScannerViewModel(
            parser = container.scanParser,
            securityChecker = container.securityChecker,
            actionEngine = container.actionEngine,
            historyRepository = container.historyRepository,
            settingsStore = container.settingsStore,
        )
    }
    val historyViewModel = remember { HistoryViewModel(container.historyRepository) }
    val settingsViewModel = remember { SettingsViewModel(container.settingsStore) }
    val settingsState by settingsViewModel.settings.collectAsState()

    androidx.compose.runtime.LaunchedEffect(startRoute) {
        if (startRoute != AppRoute.Scanner.route) {
            navController.navigate(startRoute) {
                launchSingleTop = true
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = AppRoute.Scanner.route,
    ) {
        composable(AppRoute.Scanner.route) {
            ScannerScreen(
                viewModel = scannerViewModel,
                actionExecutor = container.actionExecutor,
                onOpenGallery = { navController.navigate(AppRoute.Gallery.route) },
                onOpenHistory = { navController.navigate(AppRoute.History.route) },
                onOpenSettings = { navController.navigate(AppRoute.Settings.route) },
            )
        }
        composable(AppRoute.Gallery.route) {
            GalleryScreen(
                scannerViewModel = scannerViewModel,
                onBack = { navController.popBackStack() },
            )
        }
        composable(AppRoute.History.route) {
            HistoryScreen(
                viewModel = historyViewModel,
                actionExecutor = container.actionExecutor,
                settings = settingsState,
                onBack = { navController.popBackStack() },
            )
        }
        composable(AppRoute.Settings.route) {
            SettingsScreen(
                settings = settingsState,
                onBack = { navController.popBackStack() },
                onVibrationChanged = settingsViewModel::setVibration,
                onSoundChanged = settingsViewModel::setSound,
                onSafeModeChanged = settingsViewModel::setSafeMode,
                onSaveHistoryChanged = settingsViewModel::setSaveHistory,
                onTrustedDomainsChanged = settingsViewModel::setTrustedDomainsCsv,
                onUrlActionModeChanged = settingsViewModel::setUrlActionMode,
                onWifiActionModeChanged = settingsViewModel::setWifiActionMode,
                onAuthActionModeChanged = settingsViewModel::setAuthActionMode,
                onContactActionModeChanged = settingsViewModel::setContactActionMode,
                onEventActionModeChanged = settingsViewModel::setEventActionMode,
                onOtpActionModeChanged = settingsViewModel::setOtpActionMode,
                onTelActionModeChanged = settingsViewModel::setTelActionMode,
                onEmailActionModeChanged = settingsViewModel::setEmailActionMode,
                onSmsActionModeChanged = settingsViewModel::setSmsActionMode,
                onGeoActionModeChanged = settingsViewModel::setGeoActionMode,
                onTextActionModeChanged = settingsViewModel::setTextActionMode,
            )
        }
    }
}

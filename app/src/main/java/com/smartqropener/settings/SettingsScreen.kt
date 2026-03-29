package com.smartqropener.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.smartqropener.core.model.ActionMode
import com.smartqropener.core.model.UserSettings

@Composable
fun SettingsScreen(
    settings: UserSettings,
    onBack: () -> Unit,
    onVibrationChanged: (Boolean) -> Unit,
    onSoundChanged: (Boolean) -> Unit,
    onSafeModeChanged: (Boolean) -> Unit,
    onSaveHistoryChanged: (Boolean) -> Unit,
    onTrustedDomainsChanged: (String) -> Unit,
    onUrlActionModeChanged: (ActionMode) -> Unit,
    onWifiActionModeChanged: (ActionMode) -> Unit,
    onAuthActionModeChanged: (ActionMode) -> Unit,
    onContactActionModeChanged: (ActionMode) -> Unit,
    onEventActionModeChanged: (ActionMode) -> Unit,
    onOtpActionModeChanged: (ActionMode) -> Unit,
    onTelActionModeChanged: (ActionMode) -> Unit,
    onEmailActionModeChanged: (ActionMode) -> Unit,
    onSmsActionModeChanged: (ActionMode) -> Unit,
    onGeoActionModeChanged: (ActionMode) -> Unit,
    onTextActionModeChanged: (ActionMode) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text("Settings", style = MaterialTheme.typography.headlineMedium)
                Text(
                    "Keep the opener fast, safe, and predictable.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
                Button(onClick = onBack) {
                    Text("Back")
                }
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                ToggleRow(
                    title = "Vibration",
                    description = "Use a small haptic cue on scan.",
                    checked = settings.vibration,
                    onCheckedChange = onVibrationChanged,
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
                ToggleRow(
                    title = "Sound",
                    description = "Play a soft sound on scan.",
                    checked = settings.sound,
                    onCheckedChange = onSoundChanged,
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
                ToggleRow(
                    title = "Safe mode",
                    description = "Ask before opening suspicious results.",
                    checked = settings.safeMode,
                    onCheckedChange = onSafeModeChanged,
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
                ToggleRow(
                    title = "Save history",
                    description = "Keep scans in your local history.",
                    checked = settings.saveHistory,
                    onCheckedChange = onSaveHistoryChanged,
                )
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text("Trust mode", style = MaterialTheme.typography.titleLarge)
                Text(
                    "Trusted domains skip extra warnings. Separate entries with commas.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                OutlinedTextField(
                    value = settings.trustedDomainsCsv,
                    onValueChange = onTrustedDomainsChanged,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Trusted domains") },
                    placeholder = { Text("openai.com, github.com") },
                )
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Text("Custom actions", style = MaterialTheme.typography.titleLarge)
                Text(
                    "Choose the default action per QR type.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                ActionModeSelector(
                    title = "URL",
                    description = "Links and bare domains",
                    value = settings.urlActionMode,
                    onValueChanged = onUrlActionModeChanged,
                )
                ActionModeSelector(
                    title = "Wi-Fi",
                    description = "Wi-Fi config payloads",
                    value = settings.wifiActionMode,
                    onValueChanged = onWifiActionModeChanged,
                )
                ActionModeSelector(
                    title = "Auth / FIDO",
                    description = "Login and passkey flows",
                    value = settings.authActionMode,
                    onValueChanged = onAuthActionModeChanged,
                )
                ActionModeSelector(
                    title = "OTP",
                    description = "Authenticator seeds",
                    value = settings.otpActionMode,
                    onValueChanged = onOtpActionModeChanged,
                )
                ActionModeSelector(
                    title = "Contact",
                    description = "VCARD and MECARD payloads",
                    value = settings.contactActionMode,
                    onValueChanged = onContactActionModeChanged,
                )
                ActionModeSelector(
                    title = "Event",
                    description = "Calendar invites",
                    value = settings.eventActionMode,
                    onValueChanged = onEventActionModeChanged,
                )
                ActionModeSelector(
                    title = "Phone",
                    description = "TEL links",
                    value = settings.telActionMode,
                    onValueChanged = onTelActionModeChanged,
                )
                ActionModeSelector(
                    title = "Email",
                    description = "mailto links",
                    value = settings.emailActionMode,
                    onValueChanged = onEmailActionModeChanged,
                )
                ActionModeSelector(
                    title = "SMS",
                    description = "Message links",
                    value = settings.smsActionMode,
                    onValueChanged = onSmsActionModeChanged,
                )
                ActionModeSelector(
                    title = "Geo",
                    description = "Map coordinates",
                    value = settings.geoActionMode,
                    onValueChanged = onGeoActionModeChanged,
                )
                ActionModeSelector(
                    title = "Text",
                    description = "Plain scan text",
                    value = settings.textActionMode,
                    onValueChanged = onTextActionModeChanged,
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun ToggleRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(description, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        }
    }
}

@Composable
private fun ActionModeSelector(
    title: String,
    description: String,
    value: ActionMode,
    onValueChanged: (ActionMode) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(description, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ActionMode.values().forEach { mode ->
                FilterChip(
                    selected = value == mode,
                    onClick = { onValueChanged(mode) },
                    label = { Text(mode.name.lowercase().replaceFirstChar { it.uppercase() }) },
                )
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.22f))
    }
}

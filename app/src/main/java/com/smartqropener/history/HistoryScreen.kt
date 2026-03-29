package com.smartqropener.history

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.smartqropener.actionengine.ScanActionExecutor
import com.smartqropener.core.model.ActionMode
import com.smartqropener.core.model.HistoryItem
import com.smartqropener.core.model.ResolvedAction
import com.smartqropener.core.model.ScanType
import com.smartqropener.core.model.UserSettings
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    actionExecutor: ScanActionExecutor,
    settings: UserSettings,
    onBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text("History", style = MaterialTheme.typography.headlineMedium)
                    Text(
                        "${uiState.filteredItems.size} visible / ${uiState.totalCount} saved",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        InfoPill(text = "${uiState.favoriteCount} favorites")
                        InfoPill(text = "${uiState.riskyCount} risky")
                        InfoPill(text = "${uiState.actionableCount} actionable")
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(onClick = onBack) {
                            Text("Back")
                        }
                        Button(
                            onClick = viewModel::clear,
                            enabled = uiState.items.isNotEmpty(),
                        ) {
                            Text("Clear all")
                        }
                    }
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    OutlinedTextField(
                        value = uiState.query,
                        onValueChange = viewModel::onQueryChanged,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        label = { Text("Search history") },
                        placeholder = { Text("Type a domain, number, or label") },
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            "Filter",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            HistoryFilter.values().forEach { filter ->
                                FilterChip(
                                    selected = uiState.filter == filter,
                                    onClick = { viewModel.onFilterChanged(filter) },
                                    label = { Text(filterLabel(filter)) },
                                )
                            }
                        }
                    }
                }
            }
        }

        if (uiState.filteredItems.isEmpty()) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Text("No matching scans.")
                        Text(
                            "Try another keyword or switch the filter.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }

        items(uiState.filteredItems, key = { it.id }) { item ->
            HistoryCard(
                context = context,
                item = item,
                actionExecutor = actionExecutor,
                settings = settings,
                onToggleFavorite = { viewModel.toggleFavorite(item) },
                onDelete = { viewModel.delete(item) },
            )
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun HistoryCard(
    context: android.content.Context,
    item: HistoryItem,
    actionExecutor: ScanActionExecutor,
    settings: UserSettings,
    onToggleFavorite: () -> Unit,
    onDelete: () -> Unit,
) {
    val resolvedAction = item.toResolvedAction(settings)
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                InfoPill(text = item.type.name.lowercase(Locale.getDefault()))
                InfoPill(text = item.source.name.lowercase(Locale.getDefault()))
                if (item.isFavorite) {
                    InfoPill(text = "favorite")
                }
                if (item.isSuspicious) {
                    InfoPill(text = "risk")
                }
            }
            Text(item.normalizedValue, style = MaterialTheme.typography.bodyLarge)
            if (!item.displayText.isNullOrBlank()) {
                Text("Action: ${item.displayText}", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text("Source: ${item.source.name.lowercase(Locale.getDefault())}")
            Text("Time: ${formatTime(item.createdAt)}")
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = {
                        resolvedAction?.let {
                            actionExecutor.perform(context, it)
                        }
                    },
                    enabled = resolvedAction != null,
                ) {
                    Text("Open")
                }
                Button(onClick = onToggleFavorite) {
                    Text(if (item.isFavorite) "Unfavorite" else "Favorite")
                }
                Button(onClick = onDelete) {
                    Text("Delete")
                }
            }
        }
    }
}

private fun HistoryItem.toResolvedAction(settings: UserSettings): ResolvedAction? {
    return when (type) {
        ScanType.URL -> ResolvedAction(
            kind = "url",
            label = "Open link",
            payload = normalizedValue,
            isSuspicious,
            preferredMode = settings.urlActionMode,
        )
        ScanType.FIDO -> ResolvedAction(
            kind = "fido",
            label = "Open FIDO link",
            payload = normalizedValue,
            isSuspicious,
            preferredMode = settings.authActionMode,
        )
        ScanType.OTP -> ResolvedAction(
            kind = "otp",
            label = "Open OTP",
            payload = normalizedValue,
            isSuspicious,
            preferredMode = settings.otpActionMode,
        )
        ScanType.WIFI -> ResolvedAction(
            kind = "wifi",
            label = "Open Wi-Fi",
            payload = normalizedValue,
            isSuspicious,
            preferredMode = settings.wifiActionMode,
        )
        ScanType.CONTACT -> ResolvedAction(
            kind = "contact",
            label = "Add contact",
            payload = normalizedValue,
            isSuspicious,
            preferredMode = settings.contactActionMode,
        )
        ScanType.EVENT -> ResolvedAction(
            kind = "event",
            label = "Add event",
            payload = normalizedValue,
            isSuspicious,
            preferredMode = settings.eventActionMode,
        )
        ScanType.TEL -> ResolvedAction(
            kind = "tel",
            label = "Call number",
            payload = normalizedValue,
            isSuspicious,
            preferredMode = settings.telActionMode,
        )
        ScanType.EMAIL -> ResolvedAction(
            kind = "email",
            label = "Compose email",
            payload = normalizedValue,
            isSuspicious,
            preferredMode = settings.emailActionMode,
        )
        ScanType.SMS -> ResolvedAction(
            kind = "sms",
            label = "Compose message",
            payload = normalizedValue,
            isSuspicious,
            preferredMode = settings.smsActionMode,
        )
        ScanType.GEO -> ResolvedAction(
            kind = "geo",
            label = "Open map",
            payload = normalizedValue,
            isSuspicious,
            preferredMode = settings.geoActionMode,
        )
        ScanType.TEXT, ScanType.UNKNOWN -> ResolvedAction(
            kind = "text",
            label = "Copy text",
            payload = normalizedValue,
            isSuspicious,
            preferredMode = if (settings.textActionMode == ActionMode.OPEN) {
                ActionMode.COPY
            } else {
                settings.textActionMode
            },
        )
    }
}

private fun filterLabel(filter: HistoryFilter): String {
    return when (filter) {
        HistoryFilter.ALL -> "All"
        HistoryFilter.FAVORITES -> "Favorites"
        HistoryFilter.RISKY -> "Risky"
        HistoryFilter.URL -> "URL"
        HistoryFilter.AUTH -> "Auth"
        HistoryFilter.COMMUNICATION -> "Comms"
        HistoryFilter.ORGANIZER -> "Organizer"
        HistoryFilter.WIFI -> "Wi-Fi"
        HistoryFilter.TEXT -> "Text"
    }
}

@Composable
private fun InfoPill(text: String) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(999.dp),
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

private fun formatTime(epochMillis: Long): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    return formatter.format(Date(epochMillis))
}

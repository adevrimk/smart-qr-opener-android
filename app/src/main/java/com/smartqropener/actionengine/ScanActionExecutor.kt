package com.smartqropener.actionengine

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.CalendarContract
import android.provider.ContactsContract
import android.provider.Settings
import com.smartqropener.core.model.ActionMode
import com.smartqropener.core.model.ResolvedAction
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

enum class OpenOutcome {
    OPENED,
    FALLBACK_USED,
    COPIED,
    SHARED,
    FAILED
}

class ScanActionExecutor {
    fun copy(context: Context, value: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("scan_result", value))
    }

    fun share(context: Context, value: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, value)
        }
        context.startActivity(Intent.createChooser(intent, "Share scan result"))
    }

    fun perform(context: Context, action: ResolvedAction): OpenOutcome {
        return when (action.preferredMode) {
            ActionMode.COPY -> {
                copy(context, action.payload)
                OpenOutcome.COPIED
            }
            ActionMode.SHARE -> {
                share(context, action.payload)
                OpenOutcome.SHARED
            }
            ActionMode.OPEN -> open(context, action)
        }
    }

    fun open(context: Context, action: ResolvedAction): OpenOutcome {
        return try {
            val directIntent = when (action.kind) {
                "url" -> Intent(Intent.ACTION_VIEW, Uri.parse(normalizeUrl(action.payload))).apply {
                    addCategory(Intent.CATEGORY_BROWSABLE)
                }
                "fido" -> Intent(Intent.ACTION_VIEW, Uri.parse(action.payload)).apply {
                    addCategory(Intent.CATEGORY_BROWSABLE)
                }
                "otp" -> Intent(Intent.ACTION_VIEW, Uri.parse(action.payload)).apply {
                    addCategory(Intent.CATEGORY_BROWSABLE)
                }
                "tel" -> Intent(Intent.ACTION_DIAL, Uri.parse(action.payload))
                "email" -> Intent(Intent.ACTION_SENDTO, Uri.parse(action.payload))
                "sms" -> Intent(Intent.ACTION_SENDTO, Uri.parse(action.payload))
                "geo" -> Intent(Intent.ACTION_VIEW, Uri.parse(action.payload)).apply {
                    addCategory(Intent.CATEGORY_BROWSABLE)
                }
                "wifi" -> Intent(Settings.ACTION_WIFI_SETTINGS)
                "contact" -> buildContactInsertIntent(action.payload)
                "event" -> buildCalendarInsertIntent(action.payload)
                else -> return OpenOutcome.FAILED
            }.apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            context.startActivity(
                if (action.kind == "url" || action.kind == "geo" || action.kind == "fido" || action.kind == "otp") {
                    Intent.createChooser(directIntent, "Open result")
                } else {
                    directIntent
                },
            )
            OpenOutcome.OPENED
        } catch (_: ActivityNotFoundException) {
            fallback(context, action)
        } catch (_: SecurityException) {
            fallback(context, action)
        }
    }

    private fun fallback(context: Context, action: ResolvedAction): OpenOutcome {
        return when (action.kind) {
            "url" -> {
                val browserIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(normalizeUrl(action.payload)),
                ).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                if (browserIntent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(browserIntent)
                    OpenOutcome.FALLBACK_USED
                } else {
                    copy(context, action.payload)
                    OpenOutcome.COPIED
                }
            }
            "geo" -> {
                val mapsUrl = buildMapsUrl(action.payload)
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(mapsUrl)).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                if (browserIntent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(browserIntent)
                    OpenOutcome.FALLBACK_USED
                } else {
                    copy(context, action.payload)
                    OpenOutcome.COPIED
                }
            }
            "fido", "otp" -> {
                val fallbackIntent = Intent(Intent.ACTION_VIEW, Uri.parse(action.payload)).apply {
                    addCategory(Intent.CATEGORY_BROWSABLE)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                if (fallbackIntent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(fallbackIntent)
                    OpenOutcome.FALLBACK_USED
                } else {
                    copy(context, action.payload)
                    OpenOutcome.COPIED
                }
            }
            "wifi" -> {
                val wifiIntent = Intent(Settings.ACTION_WIFI_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                if (wifiIntent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(wifiIntent)
                    OpenOutcome.FALLBACK_USED
                } else {
                    copy(context, action.payload)
                    OpenOutcome.COPIED
                }
            }
            "contact", "event", "tel", "email", "sms" -> {
                copy(context, action.payload)
                OpenOutcome.COPIED
            }
            else -> OpenOutcome.FAILED
        }
    }

    private fun normalizeUrl(value: String): String {
        return if (value.startsWith("http://") || value.startsWith("https://")) {
            value
        } else {
            "https://$value"
        }
    }

    private fun buildMapsUrl(value: String): String {
        val encoded = URLEncoder.encode(value, StandardCharsets.UTF_8)
        return "https://www.google.com/maps/search/?api=1&query=$encoded"
    }

    private fun buildContactInsertIntent(payload: String): Intent {
        val intent = Intent(ContactsContract.Intents.Insert.ACTION).apply {
            type = ContactsContract.RawContacts.CONTENT_TYPE
        }
        extractField(payload, "FN")?.let { intent.putExtra(ContactsContract.Intents.Insert.NAME, it) }
        extractField(payload, "TEL")?.let { intent.putExtra(ContactsContract.Intents.Insert.PHONE, it) }
        extractField(payload, "EMAIL")?.let { intent.putExtra(ContactsContract.Intents.Insert.EMAIL, it) }
        return intent
    }

    private fun buildCalendarInsertIntent(payload: String): Intent {
        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
        }
        extractField(payload, "SUMMARY")?.let { intent.putExtra(CalendarContract.Events.TITLE, it) }
        extractField(payload, "LOCATION")?.let { intent.putExtra(CalendarContract.Events.EVENT_LOCATION, it) }
        extractField(payload, "DESCRIPTION")?.let { intent.putExtra(CalendarContract.Events.DESCRIPTION, it) }
        extractField(payload, "DTSTART")?.toLongOrNull()?.let { intent.putExtra(CalendarContract.Events.DTSTART, it) }
        extractField(payload, "DTEND")?.toLongOrNull()?.let { intent.putExtra(CalendarContract.Events.DTEND, it) }
        return intent
    }

    private fun extractField(payload: String, key: String): String? {
        return payload.lineSequence()
            .firstOrNull { it.startsWith("$key:", ignoreCase = true) }
            ?.substringAfter(':')
            ?.trim()
    }
}

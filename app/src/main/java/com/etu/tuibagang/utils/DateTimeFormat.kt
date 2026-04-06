package com.etu.tuibagang.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

internal fun formatCreatedAt(raw: String): String {
    val parsedDate = parseSupabaseTimestamp(raw) ?: return raw
    val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
    outputFormat.timeZone = TimeZone.getDefault()
    return outputFormat.format(parsedDate)
}

private fun parseSupabaseTimestamp(raw: String): Date? {
    val regex = Regex("""^(\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2})(?:\.(\d{1,6}))?([+-]\d{2})(?::?(\d{2}))?$""")
    val match = regex.matchEntire(raw.trim()) ?: return null

    val base = match.groupValues[1]
    val fraction = match.groupValues[2].ifBlank { "000" }.padEnd(6, '0').take(3)
    val tzHour = match.groupValues[3]
    val tzMinute = match.groupValues[4].ifBlank { "00" }
    val normalized = "$base.$fraction$tzHour$tzMinute"

    val parser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ", Locale.US)
    parser.timeZone = TimeZone.getTimeZone("UTC")
    return runCatching { parser.parse(normalized) }.getOrNull()
}

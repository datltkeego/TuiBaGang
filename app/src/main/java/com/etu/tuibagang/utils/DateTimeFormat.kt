package com.etu.tuibagang.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

internal fun formatCreatedAt(raw: String): String {
    val parsedDate = parseSupabaseTimestamp(raw) ?: return raw
    val outputFormat = SimpleDateFormat("dd/MM/yyyy  •  HH:mm", Locale.getDefault())
    outputFormat.timeZone = TimeZone.getDefault()
    return outputFormat.format(parsedDate)
}

/**
 * Parses Supabase timestamps like:
 *   2026-04-01 15:39:43.259365+00
 *   2026-04-01T15:39:43.259365+00
 *   2026-04-01 15:39:43+00
 *   2023-10-27T10:00:00Z
 */
private fun parseSupabaseTimestamp(raw: String): Date? {
    val trimmed = raw.trim()

    // Normalize: replace 'T' separator with space
    val normalized = trimmed.replace('T', ' ')

    // Extract parts via regex:
    // base datetime, optional fractional seconds, optional timezone
    val regex = Regex(
        """^(\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2})""" +   // base
        """(?:\.(\d+))?""" +                                // optional fraction
        """(Z|[+-]\d{2}(?::?\d{2})?)?\s*$"""                // optional timezone
    )
    val match = regex.matchEntire(normalized) ?: return null

    val base = match.groupValues[1]
    val fraction = match.groupValues[2]
    val tz = match.groupValues[3]

    // Truncate fraction to 3 digits (milliseconds)
    val millis = if (fraction.isNotEmpty()) {
        ".${fraction.padEnd(3, '0').take(3)}"
    } else {
        ".000"
    }

    // Normalize timezone to +HHmm / -HHmm format
    val timezone = when {
        tz.isEmpty() || tz == "Z" -> "+0000"
        else -> {
            val clean = tz.replace(":", "")
            if (clean.length == 3) "${clean}00" else clean // +00 -> +0000
        }
    }

    val parseStr = "$base${millis}$timezone"
    val parser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ", Locale.US)
    parser.isLenient = false
    return runCatching { parser.parse(parseStr) }.getOrNull()
}

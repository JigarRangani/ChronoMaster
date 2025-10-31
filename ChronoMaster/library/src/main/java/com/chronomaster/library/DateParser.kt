package com.chronomaster.library

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

/**
 * An internal object responsible for parsing date strings by attempting various common formats.
 */
internal object DateParser {

    // A prioritized list of common date formats using the thread-safe DateTimeFormatter.
    private val SUPPORTED_FORMATTERS = mutableListOf(
        // ISO 8601 variations are handled automatically by ZonedDateTime.parse
        DateTimeFormatter.ISO_ZONED_DATE_TIME,
        DateTimeFormatter.ISO_OFFSET_DATE_TIME,
        DateTimeFormatter.ISO_INSTANT,

        // Common server formats
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.US),
        DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.US),

        // Formats with different separators
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss", Locale.US),
        DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.US),
        DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss", Locale.US),
        DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.US),

        // Textual formats
        DateTimeFormatter.RFC_1123_DATE_TIME,
        DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.US),
    )

    /**
     * Adds a list of custom date format patterns to the parser's repertoire.
     * @param patterns A list of date format strings.
     */
    fun addCustomParsers(patterns: List<String>) {
        patterns.forEach {
            try {
                SUPPORTED_FORMATTERS.add(DateTimeFormatter.ofPattern(it, Locale.US))
            } catch (e: IllegalArgumentException) {
                // Log or handle invalid patterns provided by the user.
                println("ChronoMaster: Invalid custom parser pattern provided: '$it'. It will be ignored.")
            }
        }
    }

    /**
     * Parses a date string by trying a list of supported formats. Handles Epoch timestamps as well.
     *
     * @param dateString The raw date string or timestamp.
     * @param inputZoneId The timezone to assume for date strings that don't specify an offset.
     * @return A [ChronoResult] with the parsed ZonedDateTime or an error.
     */
    fun parse(dateString: String, inputZoneId: ZoneId): ChronoResult<ZonedDateTime> {
        // First, attempt to parse as a numeric (Epoch) timestamp.
        dateString.toLongOrNull()?.let { epoch ->
            return try {
                val instant = when {
                    epoch < 1_000_000_000_000L -> Instant.ofEpochSecond(epoch) // Seconds
                    else -> Instant.ofEpochMilli(epoch) // Milliseconds
                }
                ChronoResult.Success(ZonedDateTime.ofInstant(instant, inputZoneId))
            } catch (e: Exception) {
                return ChronoResult.Error("Failed to parse epoch time: $epoch", e)
            }
        }

        // If not an epoch, try direct parsing with ZonedDateTime, which handles ISO formats.
        try {
            return ChronoResult.Success(ZonedDateTime.parse(dateString))
        } catch (e: DateTimeParseException) {
            // Not an ISO format, proceed to try our custom list.
        }

        // Iterate through the list of supported string formatters.
        for (formatter in SUPPORTED_FORMATTERS) {
            try {
                val zonedDateTime = ZonedDateTime.parse(dateString, formatter.withZone(inputZoneId))
                return ChronoResult.Success(zonedDateTime)
            } catch (e: DateTimeParseException) {
                // Ignore and try the next format.
            }
        }

        return ChronoResult.Error("Failed to parse date string: '$dateString'. None of the supported formats matched.")
    }
}

package com.chronomaster.library

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.zone.ZoneRulesException
import java.util.Locale

/**
 * A sealed class representing the result of an operation, which can either be a success or an error.
 * @param T The type of the successful result data.
 */
sealed class ChronoResult<out T> {
    /**
     * Represents a successful result.
     * @param data The result data.
     */
    data class Success<out T>(val data: T) : ChronoResult<T>()

    /**
     * Represents an error.
     * @param message A descriptive error message.
     * @param throwable An optional throwable for debugging purposes.
     */
    data class Error(val message: String, val throwable: Throwable? = null) : ChronoResult<Nothing>()
}

/**
 * The main entry point for the ChronoMaster library.
 * This object handles the global configuration and provides the primary date formatting functions.
 */
object ChronoMaster {

    private var defaultInputZoneId: ZoneId = ZoneId.of("UTC")
    private var defaultOutputZoneId: ZoneId = ZoneId.systemDefault()

    /**
     * Initializes the ChronoMaster library with default timezones and optional custom date format parsers.
     * This should typically be called once in your Application class.
     *
     * @param inputTimeZoneId The default IANA time zone ID for input date strings (e.g., "UTC").
     * @param outputTimeZoneId The default IANA time zone ID for output formatted strings (e.g., "Asia/Kolkata").
     * @param customParsers A list of custom date format patterns to be added to the automatic detection engine.
     */
    fun initialize(
        inputTimeZoneId: String,
        outputTimeZoneId: String,
        customParsers: List<String> = emptyList()
    ) {
        try {
            defaultInputZoneId = ZoneId.of(inputTimeZoneId)
            defaultOutputZoneId = ZoneId.of(outputTimeZoneId)
        } catch (e: ZoneRulesException) {
            println("ChronoMaster: Invalid timezone ID provided during initialization. Falling back to defaults. Error: ${e.message}")
        }
        DateParser.addCustomParsers(customParsers)
    }

    /**
     * Parses a date string and returns a ZonedDateTime object on success.
     * This is useful when you need to perform manipulations on the date object itself.
     *
     * @param dateString The input date string to be parsed.
     * @return A [ChronoResult] containing the parsed ZonedDateTime on success, or an error message.
     */
    fun parseDate(dateString: String): ChronoResult<ZonedDateTime> {
        return DateParser.parse(dateString, defaultInputZoneId)
    }

    /**
     * Formats a given date string into a specified output format pattern, using the globally configured timezones.
     *
     * @param dateString The input date string to be formatted.
     * @param outputFormat The desired output format pattern (e.g., "dd MMM, yyyy 'at' hh:mm a").
     * @return A [ChronoResult] containing the formatted date string on success, or an error message.
     */
    fun formatDate(dateString: String, outputFormat: String): ChronoResult<String> {
        return when (val parseResult = parseDate(dateString)) {
            is ChronoResult.Success -> {
                try {
                    val formatter = DateTimeFormatter.ofPattern(outputFormat, Locale.getDefault())
                    val formattedDate = parseResult.data.withZoneSameInstant(defaultOutputZoneId).format(formatter)
                    ChronoResult.Success(formattedDate)
                } catch (e: Exception) {
                    ChronoResult.Error("Failed to format date with pattern '$outputFormat'.", e)
                }
            }
            is ChronoResult.Error -> parseResult
        }
    }

    /**
     * Formats a given date string using a standard, locale-aware format style.
     *
     * @param dateString The input date string to be formatted.
     * @param formatStyle The desired [FormatStyle] (e.g., FormatStyle.MEDIUM, FormatStyle.LONG).
     * @param locale The locale to be used for formatting. Defaults to the system's default locale.
     * @return A [ChronoResult] containing the localized formatted date string on success, or an error message.
     */
    fun formatDate(
        dateString: String,
        formatStyle: FormatStyle,
        locale: Locale = Locale.getDefault()
    ): ChronoResult<String> {
        return when (val parseResult = parseDate(dateString)) {
            is ChronoResult.Success -> {
                try {
                    val formatter = DateTimeFormatter.ofLocalizedDateTime(formatStyle).withLocale(locale)
                    val formattedDate = parseResult.data.withZoneSameInstant(defaultOutputZoneId).format(formatter)
                    ChronoResult.Success(formattedDate)
                } catch (e: Exception) {
                    ChronoResult.Error("Failed to format date with style '$formatStyle' for locale '$locale'.", e)
                }
            }
            is ChronoResult.Error -> parseResult
        }
    }

    /**
     * Asynchronously fetches the current "true" time from a reliable network source.
     *
     * @return A [ChronoResult] containing a trustworthy `java.time.Instant` on success, or an error.
     */
    suspend fun getTrueTime(): ChronoResult<Instant> {
        return NtpClient.getTrueTime()
    }

    /**
     * Converts a given timestamp into a human-readable relative time string.
     *
     * @param epochMillis The timestamp to format, in milliseconds.
     * @return A string like "5 minutes ago", "Yesterday", or "In 2 weeks".
     */
    fun toRelativeTime(epochMillis: Long): String {
        return DateUtils.toRelativeTime(epochMillis)
    }

    /**
     * Adds a specified number of days to a ZonedDateTime object.
     *
     * @param zonedDateTime The original date-time.
     * @param days The number of days to add.
     * @return A new ZonedDateTime object representing the result.
     */
    fun plusDays(zonedDateTime: ZonedDateTime, days: Long): ZonedDateTime {
        return DateUtils.plusDays(zonedDateTime, days)
    }

    /**
     * Subtracts a specified number of days from a ZonedDateTime object.
     *
     * @param zonedDateTime The original date-time.
     * @param days The number of days to subtract.
     * @return A new ZonedDateTime object representing the result.
     */
    fun minusDays(zonedDateTime: ZonedDateTime, days: Long): ZonedDateTime {
        return DateUtils.minusDays(zonedDateTime, days)
    }
}

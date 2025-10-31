package com.chronomaster.library

import java.util.Date
import java.util.TimeZone

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

    private var defaultInputTimeZone: TimeZone = TimeZone.getTimeZone("UTC")
    private var defaultOutputTimeZone: TimeZone = TimeZone.getDefault()

    /**
     * Initializes the ChronoMaster library with default timezones.
     * This should typically be called once in your Application class.
     *
     * @param inputTimeZoneId The default IANA time zone ID for input date strings (e.g., "UTC").
     * @param outputTimeZoneId The default IANA time zone ID for output formatted strings (e.g., "Asia/Kolkata").
     */
    fun initialize(inputTimeZoneId: String, outputTimeZoneId: String) {
        try {
            defaultInputTimeZone = TimeZone.getTimeZone(inputTimeZoneId)
            defaultOutputTimeZone = TimeZone.getTimeZone(outputTimeZoneId)
        } catch (e: Exception) {
            // Log this error, but don't crash. Fallback to defaults.
            println("ChronoMaster: Invalid timezone ID provided during initialization. Falling back to defaults. Error: ${e.message}")
        }
    }

    /**
     * Formats a given date string into a specified output format, using the globally configured timezones.
     *
     * @param dateString The input date string to be formatted (e.g., "2025-10-31T10:30:00Z", "31/10/2025", 1730389200).
     * @param outputFormat The desired output format (e.g., "dd MMM, yyyy 'at' hh:mm a").
     * @return A [ChronoResult] containing the formatted date string on success, or an error message.
     */
    fun formatDate(dateString: String, outputFormat: String): ChronoResult<String> {
        return DateParser.parseAndFormat(
            dateString = dateString,
            outputFormat = outputFormat,
            inputTimeZone = defaultInputTimeZone,
            outputTimeZone = defaultOutputTimeZone
        )
    }

    /**
     * Formats a given date string into a specified output format, using custom timezones that override the global configuration.
     *
     * @param dateString The input date string to be formatted.
     * @param outputFormat The desired output format.
     * @param inputTimeZoneId The IANA time zone ID of the input string.
     * @param outputTimeZoneId The IANA time zone ID for the output string.
     * @return A [ChronoResult] containing the formatted date string on success, or an error message.
     */
    fun formatDate(
        dateString: String,
        outputFormat: String,
        inputTimeZoneId: String,
        outputTimeZoneId: String
    ): ChronoResult<String> {
        return try {
            val inputTimeZone = TimeZone.getTimeZone(inputTimeZoneId)
            val outputTimeZone = TimeZone.getTimeZone(outputTimeZoneId)
            DateParser.parseAndFormat(
                dateString = dateString,
                outputFormat = outputFormat,
                inputTimeZone = inputTimeZone,
                outputTimeZone = outputTimeZone
            )
        } catch (e: Exception) {
            ChronoResult.Error("Invalid custom timezone ID provided. Error: ${e.message}", e)
        }
    }

    /**
     * Asynchronously fetches the current "true" time from a reliable network source.
     * This is crucial for handling cases where a user has manually changed their device clock.
     *
     * @return A [ChronoResult] containing a trustworthy timestamp (Long) on success, or an error.
     */
    suspend fun getTrueTime(): ChronoResult<Long> {
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
     * Adds a specified number of days to a Date object.
     *
     * @param date The original date.
     * @param days The number of days to add.
     * @return A new Date object representing the result.
     */
    fun plusDays(date: Date, days: Int): Date {
        return DateUtils.plusDays(date, days)
    }

    /**
     * Subtracts a specified number of days from a Date object.
     *
     * @param date The original date.
     * @param days The number of days to subtract.
     * @return A new Date object representing the result.
     */
    fun minusDays(date: Date, days: Int): Date {
        return DateUtils.minusDays(date, days)
    }
}

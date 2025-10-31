package com.chronomaster.library

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * An internal object responsible for parsing date strings by attempting various common formats.
 */
internal object DateParser {

    // A prioritized list of common date formats.
    private val SUPPORTED_FORMATS = listOf(
        // ISO 8601 variations
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US),
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US),
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.US),
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.US),

        // Common server formats
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US),
        SimpleDateFormat("yyyy-MM-dd", Locale.US),

        // Formats with different separators
        SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US),
        SimpleDateFormat("dd/MM/yyyy", Locale.US),
        SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.US),
        SimpleDateFormat("MM/dd/yyyy", Locale.US),

        // Textual formats
        SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US),
        SimpleDateFormat("MMM dd, yyyy", Locale.US),
    )

    /**
     * Parses a date string by trying a list of supported formats and then formats it to the desired output.
     * Also handles Epoch timestamp parsing.
     *
     * @param dateString The raw date string or timestamp.
     * @param outputFormat The desired pattern for the output string.
     * @param inputTimeZone The timezone of the input date.
     * @param outputTimeZone The timezone for the output date.
     * @return A [ChronoResult] with the formatted string or an error.
     */
    fun parseAndFormat(
        dateString: String,
        outputFormat: String,
        inputTimeZone: TimeZone,
        outputTimeZone: TimeZone
    ): ChronoResult<String> {
        // First, attempt to parse as a numeric (Epoch) timestamp.
        dateString.toLongOrNull()?.let { epoch ->
            return try {
                val date = when {
                    // Check if it's seconds (a reasonable range for modern dates)
                    epoch < 1_000_000_000_000 -> Date(TimeUnit.SECONDS.toMillis(epoch))
                    // Assume milliseconds otherwise
                    else -> Date(epoch)
                }
                val outputSdf = SimpleDateFormat(outputFormat, Locale.US)
                outputSdf.timeZone = outputTimeZone
                ChronoResult.Success(outputSdf.format(date))
            } catch (e: Exception) {
                return ChronoResult.Error("Failed to format epoch time: $epoch", e)
            }
        }

        // If not an epoch, iterate through the list of supported string formats.
        for (sdf in SUPPORTED_FORMATS) {
            try {
                sdf.timeZone = inputTimeZone
                val parsedDate = sdf.parse(dateString)
                if (parsedDate != null) {
                    val outputSdf = SimpleDateFormat(outputFormat, Locale.US)
                    outputSdf.timeZone = outputTimeZone
                    return ChronoResult.Success(outputSdf.format(parsedDate))
                }
            } catch (e: Exception) {
                // Ignore and try the next format.
            }
        }

        return ChronoResult.Error("Failed to parse date string: '$dateString'. None of the supported formats matched.")
    }
}

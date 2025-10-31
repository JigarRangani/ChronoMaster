package com.chronomaster.library

import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit

/**
 * An internal object containing utility functions for date manipulation and relative time formatting.
 */
internal object DateUtils {

    private const val NOW_THRESHOLD_SECONDS = 10

    /**
     * Formats a given epoch timestamp into a human-readable relative time string.
     * (e.g., "5 minutes ago", "Yesterday", "In 2 weeks").
     *
     * @param epochMillis The timestamp to format, in milliseconds since the Unix epoch.
     * @param currentTimeMillis The current time to compare against, defaults to System.currentTimeMillis().
     * @return A string representing the relative time.
     */
    fun toRelativeTime(epochMillis: Long, currentTimeMillis: Long = System.currentTimeMillis()): String {
        val durationMillis = currentTimeMillis - epochMillis
        val seconds = TimeUnit.MILLISECONDS.toSeconds(durationMillis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis)
        val hours = TimeUnit.MILLISECONDS.toHours(durationMillis)
        val days = TimeUnit.MILLISECONDS.toDays(durationMillis)

        return when {
            // Future dates
            seconds < 0 -> {
                val futureSeconds = -seconds
                val futureMinutes = TimeUnit.SECONDS.toMinutes(futureSeconds)
                val futureHours = TimeUnit.SECONDS.toHours(futureSeconds)
                val futureDays = TimeUnit.SECONDS.toDays(futureSeconds)
                when {
                    futureSeconds < NOW_THRESHOLD_SECONDS -> "In a moment"
                    futureMinutes < 1 -> "In $futureSeconds seconds"
                    futureHours < 1 -> "In $futureMinutes minutes"
                    futureDays < 1 -> "In $futureHours hours"
                    futureDays < 2 -> "Tomorrow"
                    futureDays < 7 -> "In $futureDays days"
                    futureDays < 14 -> "Next week"
                    else -> "In ${futureDays / 7} weeks"
                }
            }
            // Past dates
            seconds < NOW_THRESHOLD_SECONDS -> "Just now"
            minutes < 1 -> "$seconds seconds ago"
            hours < 1 -> "$minutes minutes ago"
            days < 1 -> "$hours hours ago"
            days < 2 -> "Yesterday"
            days < 7 -> "$days days ago"
            days < 14 -> "Last week"
            else -> "${days / 7} weeks ago"
        }
    }

    /**
     * Adds a specified number of days to a given Date object.
     *
     * @param date The original date.
     * @param days The number of days to add.
     * @return A new [Date] object with the days added.
     */
    fun plusDays(date: Date, days: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.DAY_OF_YEAR, days)
        return calendar.time
    }

    /**
     * Subtracts a specified number of days from a given Date object.
     *
     * @param date The original date.
     * @param days The number of days to subtract.
     * @return A new [Date] object with the days subtracted.
     */
    fun minusDays(date: Date, days: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.DAY_OF_YEAR, -days)
        return calendar.time
    }
}

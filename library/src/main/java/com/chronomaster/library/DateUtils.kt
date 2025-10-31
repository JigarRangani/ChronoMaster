package com.chronomaster.library

import java.time.Duration
import java.time.Instant
import java.time.ZonedDateTime
import kotlin.math.abs

internal object DateUtils {

    fun toRelativeTime(epochMillis: Long): String {
        val now = Instant.now()
        val target = Instant.ofEpochMilli(epochMillis)
        val duration = Duration.between(now, target)
        val isFuture = !duration.isNegative

        val seconds = abs(duration.seconds)

        val timePeriod = when {
            seconds < 60 -> "$seconds seconds"
            seconds < 3600 -> "${seconds / 60} minutes"
            seconds < 86400 -> "${seconds / 3600} hours"
            seconds < 604800 -> "${seconds / 86400} days"
            seconds < 2592000 -> "${seconds / 604800} weeks"
            seconds < 31536000 -> "${seconds / 2592000} months"
            else -> "${seconds / 31536000} years"
        }

        return if (isFuture) "in $timePeriod" else "$timePeriod ago"
    }

    fun plusDays(zonedDateTime: ZonedDateTime, days: Long): ZonedDateTime {
        return zonedDateTime.plusDays(days)
    }

    fun minusDays(zonedDateTime: ZonedDateTime, days: Long): ZonedDateTime {
        return zonedDateTime.minusDays(days)
    }
}

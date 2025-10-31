package com.chronomaster.library

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.ZoneId
import java.time.ZonedDateTime

class DateParserTest {

    @Test
    fun `test parse with timezone`() {
        val dateString = "2024-01-01T12:00:00Z"
        val result = DateParser.parse(dateString, ZoneId.of("UTC"))
        assertTrue(result is ChronoResult.Success)
        val zonedDateTime = (result as ChronoResult.Success).data
        assertEquals(2024, zonedDateTime.year)
        assertEquals(1, zonedDateTime.monthValue)
        assertEquals(1, zonedDateTime.dayOfMonth)
    }

    @Test
    fun `test parse without timezone`() {
        val dateString = "2024-01-01"
        val result = DateParser.parse(dateString, ZoneId.of("UTC"))
        assertTrue(result is ChronoResult.Success)
        val zonedDateTime = (result as ChronoResult.Success).data
        assertEquals(2024, zonedDateTime.year)
        assertEquals(1, zonedDateTime.monthValue)
        assertEquals(1, zonedDateTime.dayOfMonth)
        assertEquals("UTC", zonedDateTime.zone.id)
    }
}

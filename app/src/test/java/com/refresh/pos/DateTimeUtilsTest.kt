package com.refresh.pos

import com.refresh.pos.domain.DateTimeUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class DateTimeUtilsTest {

    @Test
    fun getCurrentTime_returnsNonEmptyString() {
        val result = DateTimeUtils.getCurrentTime()
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun getCurrentTime_matchesExpectedFormat() {
        val result = DateTimeUtils.getCurrentTime()
        assertTrue(result.matches(Regex("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")))
    }

    @Test
    fun getSQLDateFormat_returnsDateOnly() {
        val calendar = Calendar.getInstance().apply {
            set(2026, 4, 30, 10, 30, 0)
        }
        val result = DateTimeUtils.getSQLDateFormat(calendar)
        assertEquals(10, result.length)
        assertEquals("2026-05-30", result)
    }
}

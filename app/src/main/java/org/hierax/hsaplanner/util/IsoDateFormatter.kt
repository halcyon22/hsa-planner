package org.hierax.hsaplanner.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAccessor
import javax.inject.Inject

const val ISO_DATE_FORMAT: String = "yyyy-MM-dd"

class IsoDateFormatter @Inject constructor() {
    private val formatter = DateTimeFormatter.ofPattern(ISO_DATE_FORMAT)

    fun format(date: LocalDate): String {
        return formatter.format(date)
    }

    fun parse(potentialDate: String): TemporalAccessor {
        return formatter.parse(potentialDate)
    }

}
package org.hierax.hsaplanner.repository

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CustomConverters {
    companion object {
        private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        @TypeConverter
        @JvmStatic
        fun toLocalDate(dateString: String): LocalDate {
            return LocalDate.parse(dateString)
        }

        @TypeConverter
        @JvmStatic
        fun fromLocalDate(localDate: LocalDate): String {
            return formatter.format(localDate)
        }
    }
}
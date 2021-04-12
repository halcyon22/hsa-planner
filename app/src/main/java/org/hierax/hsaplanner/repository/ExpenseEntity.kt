package org.hierax.hsaplanner.repository

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "description", defaultValue = "") val description: String,

    @TypeConverters(LocalDateConverter::class)
    @ColumnInfo(name = "expense_date", defaultValue = "date(current_timestamp)") val expenseDate: String,

    @ColumnInfo(name = "original_amount", defaultValue = "0.0") val originalAmount: Double,
    @ColumnInfo(name = "remaining_amount", defaultValue = "0.0") val remainingAmount: Double
)
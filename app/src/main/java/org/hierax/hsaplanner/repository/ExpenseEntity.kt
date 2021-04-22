package org.hierax.hsaplanner.repository

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.time.LocalDate

@TypeConverters(CustomConverters::class)
@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "description", defaultValue = "") val description: String,
    @ColumnInfo(name = "expense_date", defaultValue = "date(current_timestamp)") val expenseDate: LocalDate,
    @ColumnInfo(name = "original_amount", defaultValue = "0.0") val originalAmount: Double,
    @ColumnInfo(name = "remaining_amount", defaultValue = "0.0") val remainingAmount: Double
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ExpenseEntity

        if (description != other.description) return false
        if (expenseDate != other.expenseDate) return false
        if (originalAmount != other.originalAmount) return false
        if (remainingAmount != other.remainingAmount) return false

        return true
    }

    override fun hashCode(): Int {
        var result = description.hashCode()
        result = 31 * result + expenseDate.hashCode()
        result = 31 * result + originalAmount.hashCode()
        result = 31 * result + remainingAmount.hashCode()
        return result
    }
}
package org.hierax.hsaplanner.repository

import androidx.room.Dao
import androidx.room.Query
import androidx.room.TypeConverters
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
@TypeConverters(CustomConverters::class)
interface ExpenseDao {
    @Query("select * from `expenses` where remaining_amount > 0 order by `expense_date` asc")
    fun getOutstandingExpenses(): Flow<List<ExpenseEntity>>

    @Query("select * from `expenses` order by `expense_date` asc")
    fun getAllExpenses(): Flow<List<ExpenseEntity>>

    @Query("select * from `expenses` where id = :id")
    suspend fun getExpense(id: Int): ExpenseEntity?

    @Update
    suspend fun update(expenseEntity: ExpenseEntity)

    @Query("insert into `expenses` (`description`, `expense_date`, `original_amount`, `remaining_amount`) values (:description, :expenseDate, :originalAmount, :remainingAmount)")
    suspend fun insert(description: String, expenseDate: LocalDate, originalAmount: Double, remainingAmount: Double)
}

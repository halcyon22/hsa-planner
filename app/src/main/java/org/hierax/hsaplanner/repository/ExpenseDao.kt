package org.hierax.hsaplanner.repository

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Query("select * from `expenses` where remaining_amount > 0 order by `expense_date` asc")
    fun getOutstandingExpenses(): Flow<List<ExpenseEntity>>

    @Query("select * from `expenses` order by `expense_date` asc")
    fun getAllExpenses(): Flow<List<ExpenseEntity>>

    @Query("select * from `expenses` where id = :id")
    suspend fun getExpense(id: Int): ExpenseEntity

    @Update
    suspend fun update(expenseEntity: ExpenseEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(expenseEntity: ExpenseEntity)
}
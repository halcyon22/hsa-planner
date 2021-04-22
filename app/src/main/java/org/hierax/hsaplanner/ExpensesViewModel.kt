package org.hierax.hsaplanner

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import org.hierax.hsaplanner.repository.ExpenseDao
import org.hierax.hsaplanner.repository.ExpenseEntity

class ExpensesViewModel(
    private val expenseDao: ExpenseDao
) : ViewModel() {
    val allExpenses: LiveData<List<ExpenseEntity>> = expenseDao.getAllExpenses().asLiveData()
}

class ExpensesViewModelFactory(
    private val expenseDao: ExpenseDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExpensesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ExpensesViewModel(expenseDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
    }
}
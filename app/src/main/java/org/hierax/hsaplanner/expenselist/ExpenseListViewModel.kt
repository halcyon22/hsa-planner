package org.hierax.hsaplanner.expenselist

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import org.hierax.hsaplanner.repository.ExpenseDao
import org.hierax.hsaplanner.repository.ExpenseEntity

class ExpensesViewModel(
    private val expenseDao: ExpenseDao
) : ViewModel() {
    val allExpenses: LiveData<List<ExpenseEntity>> = expenseDao.getAllExpenses().asLiveData()

    fun deleteExpense(expenseId: Int) {
        viewModelScope.launch {
            expenseDao.delete(expenseId)
        }
    }
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
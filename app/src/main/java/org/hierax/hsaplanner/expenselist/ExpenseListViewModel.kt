package org.hierax.hsaplanner.expenselist

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.hierax.hsaplanner.repository.ExpenseDao
import org.hierax.hsaplanner.repository.ExpenseEntity
import javax.inject.Inject

@HiltViewModel
class ExpenseListViewModel @Inject constructor(
    private val expenseDao: ExpenseDao
) : ViewModel() {
    val allExpenses: LiveData<List<ExpenseEntity>> = expenseDao.getAllExpenses().asLiveData()
    val outstandingExpenses: LiveData<List<ExpenseEntity>> = expenseDao.getOutstandingExpenses().asLiveData()

    fun deleteExpense(expenseId: Int) {
        viewModelScope.launch {
            expenseDao.delete(expenseId)
        }
    }
}

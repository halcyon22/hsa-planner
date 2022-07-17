package org.hierax.hsaplanner.editexpense

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.hierax.hsaplanner.repository.ExpenseDao
import org.hierax.hsaplanner.repository.ExpenseEntity
import org.hierax.hsaplanner.util.IsoDateFormatter
import java.time.LocalDate
import java.time.format.DateTimeParseException
import javax.inject.Inject

@HiltViewModel
class EditExpenseViewModel @Inject constructor(
    private val expenseDao: ExpenseDao,
    private val dateFormatter: IsoDateFormatter
) : ViewModel() {

    var expenseId = 0

    private val _description = MutableLiveData<String>()
    val description: LiveData<String> = _description

    private val _expenseDate = MutableLiveData(LocalDate.now())
    val expenseDate: LiveData<LocalDate> = _expenseDate

    private val _originalAmount = MutableLiveData<Double>()
    val originalAmount: LiveData<Double> = _originalAmount

    private val _remainingAmount = MutableLiveData<Double>()
    val remainingAmount: LiveData<Double> = _remainingAmount

    fun loadExpense(id: Int) {
        expenseId = id
        viewModelScope.launch {
            val expense = expenseDao.getExpense(expenseId)
            expense?.apply {
                _description.value = description
                _expenseDate.value = expenseDate
                _originalAmount.value = originalAmount
                _remainingAmount.value = remainingAmount
            }
        }
    }

    fun setExpenseDate(newExpenseDate: LocalDate) {
        _expenseDate.value = newExpenseDate
    }

    fun saveExpense(newDescription: String, newOriginalAmount: Double, newRemainingAmount: Double) {
        if (expenseId > 0) {
            viewModelScope.launch {
                expenseDao.update(ExpenseEntity(expenseId, newDescription, _expenseDate.value!!, newOriginalAmount, newRemainingAmount))
            }
        } else {
            viewModelScope.launch {
                expenseDao.insert(newDescription, _expenseDate.value!!, newOriginalAmount, newRemainingAmount)
            }
        }
    }

    fun formatDate(date: LocalDate): String {
        return dateFormatter.format(date)
    }

    fun formatAsMoney(decimal: Double): String {
        return String.format("%1.2f", decimal)
    }

    fun isValidDescription(description: String?): Boolean {
        return !description.isNullOrBlank()
    }

    fun isValidDate(potentialDate: String): Boolean {
        try {
            dateFormatter.parse(potentialDate)
        } catch (e: DateTimeParseException) {
            return false
        }
        return true
    }

    fun isValidDouble(potentialDouble: String?): Boolean {
        return potentialDouble?.toDoubleOrNull() != null
    }
}

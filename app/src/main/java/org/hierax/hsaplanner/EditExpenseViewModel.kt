package org.hierax.hsaplanner

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class EditExpenseViewModel : ViewModel() {

    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    val expenseDate = MutableLiveData(LocalDate.now())

    fun formatDate(date: LocalDate): String {
        return formatter.format(date)
    }
}
package com.example.hsaplanner

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.math.BigDecimal
import java.text.NumberFormat

class SettingsViewModel : ViewModel() {

    private val _currentBalance: MutableLiveData<BigDecimal> = MutableLiveData()
    val currentBalance: LiveData<String> = Transformations.map(_currentBalance) {
        NumberFormat.getCurrencyInstance().format(it)
    }

    fun setCurrentBalance(newBalance: BigDecimal) {
        _currentBalance.value = newBalance
    }

}
package org.hierax.hsaplanner

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.hierax.hsaplanner.repository.SettingsDao
import org.hierax.hsaplanner.repository.SettingsEntity

class SettingsViewModel(private val settingsDao: SettingsDao, private val coroutineScope: CoroutineScope) : ViewModel() {
    val currentBalance: LiveData<String> = Transformations.map(settingsDao.getSettings()) {
        String.format("%1.2f", it.currentBalance)
    }

    fun setBalance(newBalance: Double) {
        coroutineScope.launch {
            settingsDao.updateSettings(SettingsEntity(1, newBalance))
        }
    }
}

class SettingsViewModelFactory(private val settingsDao: SettingsDao, private val coroutineScope: CoroutineScope) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(settingsDao, coroutineScope) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
    }
}
package org.hierax.hsaplanner.settings

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.hierax.hsaplanner.TAG
import org.hierax.hsaplanner.repository.SettingsDao
import org.hierax.hsaplanner.repository.SettingsEntity

class SettingsViewModel(
    private val settingsDao: SettingsDao
) : ViewModel() {

    val settings: LiveData<SettingsEntity> = settingsDao.getSettings()

    fun formatAsMoney(decimal: Double): String {
        return String.format("%1.2f", decimal)
    }

    fun update(
        newBalance: Double,
        newPersonalContribution: Double,
        newEmployerContribution: Double,
        newReimbursementThreshold: Double,
        newReimbursementMax: Double,
    ) {
        viewModelScope.launch {
            Log.i(TAG, "update: $newBalance, $newPersonalContribution, $newEmployerContribution, $newReimbursementThreshold, $newReimbursementMax")
            settingsDao.updateSettings(
                SettingsEntity(
                    1,
                    newBalance,
                    newPersonalContribution,
                    newEmployerContribution,
                    newReimbursementThreshold,
                    newReimbursementMax,
                )
            )
        }
    }
}

class SettingsViewModelFactory(
    private val settingsDao: SettingsDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(settingsDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
    }
}
package org.hierax.hsaplanner.settings

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.hierax.hsaplanner.TAG
import org.hierax.hsaplanner.repository.SettingsDao
import org.hierax.hsaplanner.repository.SettingsEntity
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
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

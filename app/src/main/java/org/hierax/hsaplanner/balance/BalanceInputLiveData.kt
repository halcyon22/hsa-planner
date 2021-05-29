package org.hierax.hsaplanner.balance

import android.util.Log
import androidx.lifecycle.MediatorLiveData
import org.hierax.hsaplanner.TAG
import org.hierax.hsaplanner.expenselist.ExpenseListViewModel
import org.hierax.hsaplanner.repository.ExpenseEntity
import org.hierax.hsaplanner.repository.SettingsEntity
import org.hierax.hsaplanner.settings.SettingsViewModel

class BalanceInputLiveData(
    expenseListViewModel: ExpenseListViewModel,
    settingsModel: SettingsViewModel
) : MediatorLiveData<Pair<List<ExpenseEntity>, SettingsEntity>>() {
    var expenses: List<ExpenseEntity>? = null
    var settings: SettingsEntity? = null

    init {
        addSource(expenseListViewModel.outstandingExpenses) { expenses ->
            Log.i(TAG, "expenses updated")
            this.expenses = expenses
            settings?.let { value = expenses to it }
        }
        addSource(settingsModel.settings) { settings ->
            Log.i(TAG, "settings updated")
            this.settings = settings
            expenses?.let { value = it to settings }
        }
    }
}
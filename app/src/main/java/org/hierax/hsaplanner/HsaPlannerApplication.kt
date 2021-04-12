package org.hierax.hsaplanner

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.hierax.hsaplanner.repository.HsaPlannerDatabase

class HsaPlannerApplication : Application()  {
    private val applicationScope = CoroutineScope(SupervisorJob())
    private val database by lazy { HsaPlannerDatabase.getDatabase(this, applicationScope) }
    val settingsDao by lazy { database.settingsDao() }
    val expenseDao by lazy { database.expenseDao() }
}
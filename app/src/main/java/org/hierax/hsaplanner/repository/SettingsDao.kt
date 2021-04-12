package org.hierax.hsaplanner.repository

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface SettingsDao {
    @Query("select * from `settings` where `id` = 1")
    fun getSettings(): LiveData<SettingsEntity>

    @Update
    suspend fun updateSettings(settings: SettingsEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSettings(settings: SettingsEntity)
}
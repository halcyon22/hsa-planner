package org.hierax.hsaplanner.repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [SettingsEntity::class], version = 1)
abstract class HsaPlannerDatabase : RoomDatabase() {
    abstract fun settingsDao(): SettingsDao

    companion object {
        @Volatile
        private var INSTANCE: HsaPlannerDatabase? = null

        fun getDatabase(context: Context, coroutineScope: CoroutineScope): HsaPlannerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HsaPlannerDatabase::class.java,
                    "hsa_planner_database"
                )
                    .addCallback(WordDatabaseCallback(coroutineScope))
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }

    class WordDatabaseCallback(private val coroutineScope: CoroutineScope) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                coroutineScope.launch {
                    populateDatabase(database.settingsDao())
                }
            }
        }

        private suspend fun populateDatabase(settingsDao: SettingsDao) {
            settingsDao.insertSettings(SettingsEntity(1, 0.0))
        }
    }
}
package org.hierax.hsaplanner.repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [SettingsEntity::class, ExpenseEntity::class], version = 3)
abstract class HsaPlannerDatabase : RoomDatabase() {
    abstract fun settingsDao(): SettingsDao
    abstract fun expenseDao(): ExpenseDao

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
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .addCallback(WordDatabaseCallback(coroutineScope))
                    .build()
                INSTANCE = instance
                return instance
            }
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    |create table `new_settings` (
                    |    `id` integer not null, 
                    |    `current_balance` real not null default 0.0, 
                    |    `personal_contribution` real not null default 0.0, 
                    |    `employer_contribution` real not null default 0.0, 
                    |    `reimbursement_threshold` real not null default 0.0, 
                    |    `reimbursement_max` real not null default 0.0, 
                    |    primary key(`id`)
                    |)
                    """.trimMargin())
                database.execSQL("""
                    |insert into `new_settings` (`id`, `current_balance`)
                    |select `id`, `current_balance` from `settings`
                    """.trimMargin())
                database.execSQL("drop table `settings`")
                database.execSQL("alter table `new_settings` rename to `settings`")
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    |create table `expenses` (
                    |   `id` integer not null, 
                    |   `description` text not null default '', 
                    |   `expense_date` text not null default 'date(current_timestamp)', 
                    |   `original_amount` real not null default 0.0, 
                    |   `remaining_amount` real not null default 0.0, 
                    |   primary key(`id`)
                    |)
                    """.trimMargin())
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
            settingsDao.insertSettings(
                SettingsEntity(
                    1,
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                )
            )
        }
    }
}
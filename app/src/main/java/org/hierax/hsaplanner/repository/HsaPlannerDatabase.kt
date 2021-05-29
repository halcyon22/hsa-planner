package org.hierax.hsaplanner.repository

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.hierax.hsaplanner.TAG
import javax.inject.Singleton

@Database(entities = [SettingsEntity::class, ExpenseEntity::class], version = 3)
abstract class HsaPlannerDatabase : RoomDatabase() {
    abstract fun settingsDao(): SettingsDao
    abstract fun expenseDao(): ExpenseDao

    companion object {
        suspend fun initializeSettings(settingsDao: SettingsDao) {
            Log.i(TAG, "initializing settings")
            settingsDao.insertSettings(
                SettingsEntity(
                    1,
                    1000.0,
                    100.0,
                    500.0,
                    2000.0,
                    1000.0,
                )
            )
        }

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
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

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    |create table `expenses` (
                    |   `id` integer not null, 
                    |   `description` text not null default '', 
                    |   `expense_date` text not null default 'date(current_timestamp)', 
                    |   `original_amount` real not null default 0.0, 
                    |   `remaining_amount` real not null default 0.0, 
                    |   primary key(`id`)
                    |)
                    """.trimMargin()
                )
            }
        }
    }
}

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {
    lateinit var database: HsaPlannerDatabase

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): HsaPlannerDatabase {
        val coroutineScope = CoroutineScope(SupervisorJob())

        database = Room.databaseBuilder(
            appContext,
            HsaPlannerDatabase::class.java,
            "hsa_planner_database"
        )
            .addMigrations(HsaPlannerDatabase.MIGRATION_1_2, HsaPlannerDatabase.MIGRATION_2_3)
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    coroutineScope.launch {
                        HsaPlannerDatabase.initializeSettings(database.settingsDao())
                    }
                }
            })
            .build()
        return database
    }

    @Provides
    fun provideSettingsDao(database: HsaPlannerDatabase): SettingsDao {
        return database.settingsDao()
    }

    @Provides
    fun provideExpenseDao(database: HsaPlannerDatabase): ExpenseDao {
        return database.expenseDao()
    }
}
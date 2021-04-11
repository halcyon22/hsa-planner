package org.hierax.hsaplanner.repository

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class SettingsEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "current_balance", defaultValue = "0.0") val currentBalance: Double,
    @ColumnInfo(name = "personal_contribution", defaultValue = "0.0") val personalContribution: Double,
    @ColumnInfo(name = "employer_contribution", defaultValue = "0.0") val employerContribution: Double,
    @ColumnInfo(name = "reimbursement_threshold", defaultValue = "0.0") val reimbursementThreshold: Double,
    @ColumnInfo(name = "reimbursement_max", defaultValue = "0.0") val reimbursementMax: Double,
)
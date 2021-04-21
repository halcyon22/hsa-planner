package org.hierax.hsaplanner.data

import java.math.BigDecimal
import java.time.LocalDate

data class Expense(
    val id: Int,
    val description: String,
    val date: LocalDate,
    val originalAmount: BigDecimal,
    var remainingAmount: BigDecimal
)
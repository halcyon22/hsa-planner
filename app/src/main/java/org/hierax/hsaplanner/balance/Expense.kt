package org.hierax.hsaplanner.balance

import java.math.BigDecimal

data class Expense(
    val description: String,
    val originalAmount: BigDecimal,
    var remainingAmount: BigDecimal
)
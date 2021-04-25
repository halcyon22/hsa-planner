package org.hierax.hsaplanner.balance

import java.math.BigDecimal
import java.time.LocalDate

data class BalanceLine(
    val date: LocalDate,
    val description: String,
    val transactionAmount: BigDecimal,
    val balance: BigDecimal
)
package org.hierax.hsaplanner.balance

import java.math.BigDecimal
import java.time.LocalDate

abstract class BalanceLine(
    val date: LocalDate,
    val balance: BigDecimal
) {
    companion object {
        const val STARTING_BALANCE = 0
        const val CONTRIBUTION = 1
        const val REIMBURSEMENT = 2
    }
    abstract fun getViewType(): Int
}

class StartingBalanceLine(
    date: LocalDate, balance: BigDecimal
) : BalanceLine(date, balance) {
    override fun getViewType(): Int {
        return STARTING_BALANCE
    }
}

class ContributionBalanceLine(
    date: LocalDate, balance: BigDecimal,
    val source: Int,
    val amount: BigDecimal
) : BalanceLine(date, balance) {
    companion object {
        const val PERSONAL = 0
        const val EMPLOYER = 1
    }
    override fun getViewType(): Int {
        return CONTRIBUTION
    }
}

class ReimbursementBalanceLine(
    date: LocalDate, balance: BigDecimal,
    val reimbursementAmount: BigDecimal,
    val expenseDescription: String,
    val expenseStartingBalance: BigDecimal,
    val expenseEndingBalance: BigDecimal
) : BalanceLine(date, balance) {
    override fun getViewType(): Int {
        return REIMBURSEMENT
    }
}

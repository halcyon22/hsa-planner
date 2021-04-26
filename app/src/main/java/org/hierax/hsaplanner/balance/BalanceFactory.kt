package org.hierax.hsaplanner.balance

import android.content.Context
import org.hierax.hsaplanner.R
import java.math.BigDecimal
import java.math.BigDecimal.ZERO
import java.math.RoundingMode
import java.time.LocalDate
import java.time.Month

class BalanceFactory(
    currentBalance: Double,
    personalContribution: Double,
    employerContribution: Double,
    reimbursementThreshold: Double,
    reimbursementMax: Double,
    private val expenses: List<Expense>,
    private val context: Context
) {
    private val currentBalance: BigDecimal = BigDecimal.valueOf(currentBalance).setScale(2, RoundingMode.FLOOR)
    private val personalContribution: BigDecimal = BigDecimal.valueOf(personalContribution).setScale(2, RoundingMode.FLOOR)
    private val employerContribution: BigDecimal = BigDecimal.valueOf(employerContribution).setScale(2, RoundingMode.FLOOR)
    private val reimbursementThreshold: BigDecimal = BigDecimal.valueOf(reimbursementThreshold).setScale(2, RoundingMode.FLOOR)
    private val reimbursementMax: BigDecimal = BigDecimal.valueOf(reimbursementMax).setScale(2, RoundingMode.FLOOR)

    fun makeBalanceLines(): List<BalanceLine> {
        val lines = mutableListOf<BalanceLine>()

        val today: LocalDate = LocalDate.now().withDayOfMonth(1)
        var balance = currentBalance
        var expenseIndex = 0

        repeat(12) { monthIndex ->
            val firstOfMonth = today.plusMonths(monthIndex.toLong())
            balance = balance.plus(personalContribution)

            if (monthIndex < 1) {
                lines.add(makeStartingBalanceLine(firstOfMonth, balance))
            } else {
                lines.add(makePersonalContribution(firstOfMonth, balance))
            }

            makeEmployerContribution(firstOfMonth, balance)?.also {
                lines.add(it)
                balance = it.balance
            }

            makeReimbursementLines(expenseIndex, firstOfMonth, balance)?.also {
                lines.add(it.accountLine)
                lines.add(it.expenseLine)
                balance = it.endingAccountBalance
                if (it.expenseFullyReimbursed) {
                    expenseIndex++
                }
            }
        }

        return lines
    }

    private fun makeStartingBalanceLine(firstOfMonth: LocalDate, balance: BigDecimal) =
        BalanceLine(
            firstOfMonth,
            description = context.getString(R.string.starting_balance),
            transactionAmount = ZERO,
            balance
        )

    private fun makePersonalContribution(firstOfMonth: LocalDate, balance: BigDecimal) =
        BalanceLine(
            firstOfMonth,
            description = context.getString(R.string.personal_contribution),
            transactionAmount = personalContribution,
            balance
        )

    private fun makeEmployerContribution(firstOfMonth: LocalDate, balance: BigDecimal): BalanceLine? {
        if (employerContribution > ZERO &&
            firstOfMonth.month == Month.JANUARY || firstOfMonth.month == Month.JULY
        ) {
            return BalanceLine(
                firstOfMonth.plusDays(1),
                description = context.getString(R.string.employer_contribution),
                transactionAmount = employerContribution,
                balance.plus(employerContribution)
            )
        }
        return null
    }

    private fun makeReimbursementLines(expenseIndex: Int, firstOfMonth: LocalDate, accountBalance: BigDecimal): ReimbursementLines? {
        if (accountBalance < reimbursementThreshold || expenses.size <= expenseIndex) {
            return null
        }

        val expense = expenses[expenseIndex]
        var reimbursementAmount = reimbursementMax

        val expenseFullyReimbursed = expense.remainingAmount <= reimbursementMax
        if (expenseFullyReimbursed) {
            reimbursementAmount = expense.remainingAmount
            expense.remainingAmount = ZERO
        } else {
            expense.remainingAmount = expense.remainingAmount.minus(reimbursementMax)
        }

        val endingAccountBalance = accountBalance.minus(reimbursementAmount)

        val accountLine = BalanceLine(
            firstOfMonth.plusDays(2),
            description = context.getString(R.string.reimbursement),
            transactionAmount = reimbursementAmount.negate(),
            endingAccountBalance
        )

        val expenseLine = BalanceLine(
            firstOfMonth.plusDays(3),
            description = expense.description,
            transactionAmount = reimbursementAmount.negate(),
            expense.remainingAmount
        )

        return ReimbursementLines(accountLine, expenseLine, endingAccountBalance, expenseFullyReimbursed)
    }

    private data class ReimbursementLines(
        val accountLine: BalanceLine,
        val expenseLine: BalanceLine,
        val endingAccountBalance: BigDecimal,
        val expenseFullyReimbursed: Boolean
    )

}

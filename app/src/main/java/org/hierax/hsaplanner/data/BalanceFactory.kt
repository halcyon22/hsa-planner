package org.hierax.hsaplanner.data

import android.content.Context
import org.hierax.hsaplanner.R
import java.math.BigDecimal
import java.time.LocalDate
import java.time.Month

class BalanceFactory(
    private val context: Context,
    private val expenses: List<Expense>
) {

    companion object {
        val STARTING_BALANCE = BigDecimal("2650.00")
        val MONTHLY_CONTRIBUTION_AMOUNT = BigDecimal("450.00")
        val EMPLOYER_CONTRIBUTION_AMOUNT = BigDecimal("600.00")
        val THRESHOLD_AMOUNT = BigDecimal("2000.00")
        val REIMBURSEMENT_MAX = BigDecimal("1000.00")
    }

    fun makeBalanceLines(): List<BalanceLine> {
        val lines = mutableListOf<BalanceLine>()

        val today: LocalDate = LocalDate.now().withDayOfMonth(1)
        var balance = STARTING_BALANCE.minus(MONTHLY_CONTRIBUTION_AMOUNT)
        var expenseIndex = 0

        repeat(12) { monthIndex ->
            val firstOfMonth = today.plusMonths(monthIndex.toLong())
            balance = balance.plus(MONTHLY_CONTRIBUTION_AMOUNT)

            lines.add(
                makePersonalContribution(firstOfMonth, balance)
            )

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

    private fun makePersonalContribution(firstOfMonth: LocalDate, balance: BigDecimal) =
        BalanceLine(
            firstOfMonth,
            description = context.getString(R.string.personal_contribution),
            transactionAmount = MONTHLY_CONTRIBUTION_AMOUNT,
            balance
        )

    private fun makeEmployerContribution(firstOfMonth: LocalDate, balance: BigDecimal): BalanceLine? {
        if (EMPLOYER_CONTRIBUTION_AMOUNT > BigDecimal.ZERO &&
            firstOfMonth.month == Month.JANUARY || firstOfMonth.month == Month.JULY
        ) {
            return BalanceLine(
                firstOfMonth.plusDays(1),
                description = context.getString(R.string.employer_contribution),
                transactionAmount = EMPLOYER_CONTRIBUTION_AMOUNT,
                balance.plus(EMPLOYER_CONTRIBUTION_AMOUNT)
            )
        }
        return null
    }


    private fun makeReimbursementLines(expenseIndex: Int, firstOfMonth: LocalDate, accountBalance: BigDecimal): ReimbursementLines? {
        if (accountBalance < THRESHOLD_AMOUNT || expenses.size <= expenseIndex) {
            return null
        }

        val expense = expenses[expenseIndex]
        var reimbursementAmount = REIMBURSEMENT_MAX

        val expenseFullyReimbursed = expense.remainingAmount <= REIMBURSEMENT_MAX
        if (expenseFullyReimbursed) {
            reimbursementAmount = expense.remainingAmount
            expense.remainingAmount = BigDecimal.ZERO
        } else {
            expense.remainingAmount = expense.remainingAmount.minus(REIMBURSEMENT_MAX)
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

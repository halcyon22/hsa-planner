package com.example.hsaplanner.data

import android.content.Context
import com.example.hsaplanner.R
import java.math.BigDecimal
import java.time.LocalDate
import java.time.Month

class BalanceDataSource(
    private val context: Context,
    private val expenses: List<Expense>
) {

    companion object {
        val STARTING_BALANCE = BigDecimal("2650.00")
        val MONTHLY_CONTRIBUTION_AMOUNT = BigDecimal("450.00")
        val EMPLOYER_CONTRIBUTION_AMOUNT = BigDecimal("600.00")
        val THRESHOLD_AMOUNT = BigDecimal("3000.00")
        val REIMBURSEMENT_MAX = BigDecimal("1000.00")
    }

    fun load(): List<BalanceLine> {
        val lines = mutableListOf<BalanceLine>()

        val today: LocalDate = LocalDate.now().withDayOfMonth(1)
        var balance = STARTING_BALANCE.minus(MONTHLY_CONTRIBUTION_AMOUNT)
        var expenseIndex = 0

        repeat(12) {
            val firstOfMonth = today.plusMonths(it.toLong())
            balance = balance.plus(MONTHLY_CONTRIBUTION_AMOUNT)

            lines.add(
                BalanceLine(
                    firstOfMonth,
                    description = context.getString(R.string.personal_contribution),
                    transactionAmount = MONTHLY_CONTRIBUTION_AMOUNT,
                    balance
                )
            )

            if (firstOfMonth.month == Month.JANUARY || firstOfMonth.month == Month.JULY) {
                balance = balance.plus(EMPLOYER_CONTRIBUTION_AMOUNT)
                lines.add(
                    BalanceLine(
                        firstOfMonth.plusDays(1),
                        description = context.getString(R.string.employer_contribution),
                        transactionAmount = EMPLOYER_CONTRIBUTION_AMOUNT,
                        balance
                    )
                )
            }

            if (balance > THRESHOLD_AMOUNT && expenses.size > expenseIndex) {
                val expense = expenses[expenseIndex]
                var reimbursementAmount = REIMBURSEMENT_MAX
                if (expense.remainingAmount > REIMBURSEMENT_MAX) {
                    expense.remainingAmount = expense.remainingAmount.minus(REIMBURSEMENT_MAX)
                } else {
                    reimbursementAmount = expense.remainingAmount
                    expense.remainingAmount = BigDecimal.ZERO
                    expenseIndex++
                }

                balance = balance.minus(reimbursementAmount)
                lines.add(
                    BalanceLine(
                        firstOfMonth.plusDays(2),
                        description = context.getString(R.string.reimbursement),
                        transactionAmount = reimbursementAmount.negate(),
                        balance
                    )
                )

                lines.add(
                    BalanceLine(
                        firstOfMonth.plusDays(3),
                        description = expense.description,
                        transactionAmount = reimbursementAmount.negate(),
                        expense.remainingAmount
                    )
                )
            }
        }

        return lines
    }

}
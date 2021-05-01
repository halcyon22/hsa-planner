package org.hierax.hsaplanner.balance

import android.content.Context
import org.hierax.hsaplanner.R
import org.hierax.hsaplanner.repository.ExpenseEntity
import org.hierax.hsaplanner.repository.SettingsEntity
import java.math.BigDecimal
import java.math.BigDecimal.ZERO
import java.math.RoundingMode
import java.time.LocalDate
import java.time.Month
import java.util.stream.Collectors

class BalanceFactory(
    settings: SettingsEntity,
    expenseEntities: List<ExpenseEntity>,
    private val context: Context
) {
    private val currentBalance: BigDecimal = toBigDecimal(settings.currentBalance)
    private val personalContribution: BigDecimal = toBigDecimal(settings.personalContribution)
    private val employerContribution: BigDecimal = toBigDecimal(settings.employerContribution)
    private val reimbursementThreshold: BigDecimal = toBigDecimal(settings.reimbursementThreshold)
    private val reimbursementMax: BigDecimal = toBigDecimal(settings.reimbursementMax)
    private val expenses = transformExpenseEntities(expenseEntities)

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
            transactionAmount = balance,
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

    private fun toBigDecimal(value: Double): BigDecimal {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.FLOOR)
    }

    private fun transformExpenseEntities(entities: List<ExpenseEntity>): List<Expense> {
        return entities.stream()
            .map {
                Expense(it.description,
                    toBigDecimal(it.originalAmount),
                    toBigDecimal(it.remainingAmount)
                )
            }
            .collect(Collectors.toList())
    }

    data class Expense(
        val description: String,
        val originalAmount: BigDecimal,
        var remainingAmount: BigDecimal
    )

    private data class ReimbursementLines(
        val accountLine: BalanceLine,
        val expenseLine: BalanceLine,
        val endingAccountBalance: BigDecimal,
        val expenseFullyReimbursed: Boolean
    )

}

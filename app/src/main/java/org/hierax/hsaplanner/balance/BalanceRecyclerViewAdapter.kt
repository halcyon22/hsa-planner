package org.hierax.hsaplanner.balance

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.hierax.hsaplanner.R
import org.hierax.hsaplanner.balance.BalanceLine.Companion.CONTRIBUTION
import org.hierax.hsaplanner.balance.BalanceLine.Companion.REIMBURSEMENT
import org.hierax.hsaplanner.balance.BalanceLine.Companion.STARTING_BALANCE
import org.hierax.hsaplanner.balance.ContributionBalanceLine.Companion.EMPLOYER
import org.hierax.hsaplanner.balance.ContributionBalanceLine.Companion.PERSONAL
import java.text.NumberFormat
import java.time.format.DateTimeFormatter

class BalanceRecyclerViewAdapter(
    private val lines: List<BalanceLine>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // TODO MDP add click handler for starting balance, reimbursement lines

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            STARTING_BALANCE -> {
                val itemLayout = LayoutInflater.from(parent.context)
                    .inflate(R.layout.starting_balance_line, parent, false)
                StartingBalanceLineViewHolder(itemLayout)
            }
            CONTRIBUTION -> {
                val itemLayout = LayoutInflater.from(parent.context)
                    .inflate(R.layout.contribution_line, parent, false)
                ContributionLineViewHolder(itemLayout)
            }
            REIMBURSEMENT -> {
                val itemLayout = LayoutInflater.from(parent.context)
                    .inflate(R.layout.reimbursement_balance_line, parent, false)
                ReimbursementLineViewHolder(itemLayout)
            }
            else -> {
                throw IllegalArgumentException("Unknown viewType value: $viewType")
            }
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        when (lines[position].getViewType()) {
            STARTING_BALANCE -> {
                val it = viewHolder as StartingBalanceLineViewHolder
                val balanceLine = lines[position] as StartingBalanceLine
                it.updateContent(balanceLine)
            }
            CONTRIBUTION -> {
                val contributionLine = lines[position] as ContributionBalanceLine
                val it = viewHolder as ContributionLineViewHolder
                it.updateContent(contributionLine)
            }
            REIMBURSEMENT -> {
                val reimbursementLine = lines[position] as ReimbursementBalanceLine
                val it = viewHolder as ReimbursementLineViewHolder
                it.updateContent(reimbursementLine)
            }
            else -> {
                throw IllegalArgumentException("Unknown viewType value: ${lines[position].getViewType()}")
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return lines[position].getViewType()
    }

    override fun getItemCount(): Int {
        return lines.size
    }

    class StartingBalanceLineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dateView: TextView = itemView.findViewById(R.id.date)
        private val descriptionView: TextView = itemView.findViewById(R.id.description)
        private val balanceView: TextView = itemView.findViewById(R.id.balance)

        fun updateContent(startingBalanceLine: StartingBalanceLine) {
            dateView.text = dateFormatter.format(startingBalanceLine.date)
            descriptionView.text = itemView.context.getString(R.string.starting_balance)
            balanceView.text = NumberFormat.getCurrencyInstance().format(startingBalanceLine.balance)
        }
    }

    class ContributionLineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dateView: TextView = itemView.findViewById(R.id.date)
        private val descriptionView: TextView = itemView.findViewById(R.id.description)
        private val balanceView: TextView = itemView.findViewById(R.id.balance)

        fun updateContent(contributionLine: ContributionBalanceLine) {
            val formattedAmount = NumberFormat.getCurrencyInstance().format(contributionLine.amount)

            dateView.text = dateFormatter.format(contributionLine.date)
            descriptionView.text = when (contributionLine.source) {
                PERSONAL -> itemView.context.getString(R.string.personal_contribution, formattedAmount)
                EMPLOYER -> itemView.context.getString(R.string.employer_contribution, formattedAmount)
                else -> {
                    throw IllegalArgumentException("Unknown contribution source: ${contributionLine.source}")
                }
            }
            balanceView.text = NumberFormat.getCurrencyInstance().format(contributionLine.balance)
        }
    }

    class ReimbursementLineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val descriptionView: TextView = itemView.findViewById(R.id.description)
        private val balanceView: TextView = itemView.findViewById(R.id.balance)
        private val expenseDescriptionView: TextView = itemView.findViewById(R.id.expense_description)
        private val expenseAmountView: TextView = itemView.findViewById(R.id.expense_amounts)

        fun updateContent(reimbursementLine: ReimbursementBalanceLine) {
            val formattedReimbursementAmount = NumberFormat.getCurrencyInstance().format(reimbursementLine.reimbursementAmount)
            val formattedExpenseStartingAmount = NumberFormat.getCurrencyInstance().format(reimbursementLine.expenseStartingBalance)
            val formattedExpenseEndingAmount = NumberFormat.getCurrencyInstance().format(reimbursementLine.expenseEndingBalance)

            descriptionView.text = itemView.context.getString(R.string.reimbursement, formattedReimbursementAmount)
            balanceView.text = NumberFormat.getCurrencyInstance().format(reimbursementLine.balance)
            expenseDescriptionView.text = reimbursementLine.expenseDescription
            expenseAmountView.text = itemView.context.getString(R.string.expense_amounts, formattedExpenseStartingAmount, formattedExpenseEndingAmount)
        }
    }

    companion object {
        private val dateFormatter = DateTimeFormatter.ofPattern("yyyy MMM")
    }

}
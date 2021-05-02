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

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy MMM")

    // TODO MDP add click handler for expense lines

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

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (lines[position].getViewType()) {
            STARTING_BALANCE -> {
                val balanceLine = lines[position] as StartingBalanceLine
                val it = holder as StartingBalanceLineViewHolder

                // TODO MDP move into holder classes?
                it.dateView.text = dateFormatter.format(balanceLine.date)
                it.descriptionView.text = holder.itemView.context.getString(R.string.starting_balance)
                it.balanceView.text = NumberFormat.getCurrencyInstance().format(balanceLine.balance)
            }
            CONTRIBUTION -> {
                val contributionLine = lines[position] as ContributionBalanceLine
                val formattedAmount = NumberFormat.getCurrencyInstance().format(contributionLine.amount)
                val it = holder as ContributionLineViewHolder

                it.dateView.text = dateFormatter.format(contributionLine.date)
                it.descriptionView.text = when (contributionLine.source) {
                    PERSONAL -> holder.itemView.context.getString(R.string.personal_contribution, formattedAmount)
                    EMPLOYER -> holder.itemView.context.getString(R.string.employer_contribution, formattedAmount)
                    else -> {
                        throw IllegalArgumentException("Unknown contribution source: ${contributionLine.source}")
                    }
                }
                it.balanceView.text = NumberFormat.getCurrencyInstance().format(contributionLine.balance)
            }
            REIMBURSEMENT -> {
                val reimbursementLine = lines[position] as ReimbursementBalanceLine
                val formattedReimbursementAmount = NumberFormat.getCurrencyInstance().format(reimbursementLine.reimbursementAmount)
                val formattedExpenseStartingAmount = NumberFormat.getCurrencyInstance().format(reimbursementLine.expenseStartingBalance)
                val formattedExpenseEndingAmount = NumberFormat.getCurrencyInstance().format(reimbursementLine.expenseEndingBalance)
                val it = holder as ReimbursementLineViewHolder

                it.descriptionView.text = holder.itemView.context.getString(R.string.reimbursement, formattedReimbursementAmount)
                it.balanceView.text = NumberFormat.getCurrencyInstance().format(reimbursementLine.balance)
                it.expenseDescriptionView.text = reimbursementLine.expenseDescription
                it.expenseAmountView.text = holder.itemView.context.getString(R.string.expense_amounts, formattedExpenseStartingAmount, formattedExpenseEndingAmount)
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

    class StartingBalanceLineViewHolder(lineView: View) : RecyclerView.ViewHolder(lineView) {
        val dateView: TextView = lineView.findViewById(R.id.date)
        val descriptionView: TextView = lineView.findViewById(R.id.description)
        val balanceView: TextView = lineView.findViewById(R.id.balance)
    }

    class ContributionLineViewHolder(lineView: View) : RecyclerView.ViewHolder(lineView) {
        val dateView: TextView = lineView.findViewById(R.id.date)
        val descriptionView: TextView = lineView.findViewById(R.id.description)
        val balanceView: TextView = lineView.findViewById(R.id.balance)
    }

    class ReimbursementLineViewHolder(lineView: View) : RecyclerView.ViewHolder(lineView) {
        val descriptionView: TextView = lineView.findViewById(R.id.description)
        val balanceView: TextView = lineView.findViewById(R.id.balance)
        val expenseDescriptionView: TextView = lineView.findViewById(R.id.expense_description)
        val expenseAmountView: TextView = lineView.findViewById(R.id.expense_amounts)
    }

}
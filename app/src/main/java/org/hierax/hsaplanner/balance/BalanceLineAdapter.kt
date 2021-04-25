package org.hierax.hsaplanner.balance

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.hierax.hsaplanner.R
import org.hierax.hsaplanner.data.BalanceLine
import java.text.NumberFormat
import java.time.format.DateTimeFormatter

class BalanceLineAdapter(
    private val lines: List<BalanceLine>
) : RecyclerView.Adapter<BalanceLineAdapter.BalanceLineViewHolder>() {

    private val formatter = DateTimeFormatter.ofPattern("yyyy MMM")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BalanceLineViewHolder {
        val lineCardLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.balance_line, parent, false)
        return BalanceLineViewHolder(lineCardLayout)
    }

    override fun onBindViewHolder(holder: BalanceLineViewHolder, position: Int) {
        val balanceLine = lines[position]
        holder.dateView.text = formatter.format(balanceLine.date)
        holder.descriptionView.text = balanceLine.description
        holder.transactionAmountView.text = NumberFormat.getCurrencyInstance().format(balanceLine.transactionAmount)
        holder.balanceView.text = NumberFormat.getCurrencyInstance().format(balanceLine.balance)
    }

    override fun getItemCount(): Int {
        return lines.size
    }

    class BalanceLineViewHolder(lineView: View) : RecyclerView.ViewHolder(lineView) {
        val dateView: TextView = lineView.findViewById(R.id.date)
        val descriptionView: TextView = lineView.findViewById(R.id.list_item_description)
        val transactionAmountView: TextView = lineView.findViewById(R.id.transaction_amount)
        val balanceView: TextView = lineView.findViewById(R.id.balance)
    }

}
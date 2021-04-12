package org.hierax.hsaplanner

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.hierax.hsaplanner.data.Expense
import java.text.NumberFormat
import java.time.format.DateTimeFormatter

class ExpenseRecyclerViewAdapter(
    private val values: List<Expense>
) : RecyclerView.Adapter<ExpenseRecyclerViewAdapter.ExpenseViewHolder>() {

    private val formatter = DateTimeFormatter.ofPattern("yyyy MMM")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_expense_list_item, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {

        val expense = values[position]
        holder.dateView.text = formatter.format(expense.date)
        holder.descriptionView.text = expense.description
        holder.originalAmountView.text = holder.itemView.context.getString(R.string.original_amount, NumberFormat.getCurrencyInstance().format(expense.originalAmount))
        holder.remainingAmountView.text = holder.itemView.context.getString(R.string.remaining_amount, NumberFormat.getCurrencyInstance().format(expense.remainingAmount))
    }

    override fun getItemCount(): Int = values.size

    inner class ExpenseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dateView: TextView = view.findViewById(R.id.list_item_expense_date)
        val descriptionView: TextView = view.findViewById(R.id.list_item_description)
        val originalAmountView: TextView = view.findViewById(R.id.list_item_original_amount)
        val remainingAmountView: TextView = view.findViewById(R.id.list_item_remaining_amount)
    }
}
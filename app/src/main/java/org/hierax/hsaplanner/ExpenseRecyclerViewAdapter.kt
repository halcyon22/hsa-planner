package org.hierax.hsaplanner

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
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
        holder.originalAmountView.text = holder.itemView.context.getString(R.string.original_amount_format,
            NumberFormat.getCurrencyInstance().format(expense.originalAmount))
        holder.remainingAmountView.text = holder.itemView.context.getString(
            R.string.remaining_amount_format, NumberFormat.getCurrencyInstance().format(expense.remainingAmount))

        holder.itemView.setOnClickListener { view ->
            val action = ExpensesFragmentDirections.actionExpensesFragmentToEditExpensesFragment(expense.id)
            view.findNavController().navigate(action)
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateView: TextView = itemView.findViewById(R.id.list_item_expense_date)
        val descriptionView: TextView = itemView.findViewById(R.id.list_item_description)
        val originalAmountView: TextView = itemView.findViewById(R.id.list_item_original_amount)
        val remainingAmountView: TextView = itemView.findViewById(R.id.list_item_remaining_amount)
    }
}
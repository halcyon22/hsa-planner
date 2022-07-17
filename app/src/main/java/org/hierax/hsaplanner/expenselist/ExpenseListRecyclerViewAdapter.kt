package org.hierax.hsaplanner.expenselist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.hierax.hsaplanner.R
import org.hierax.hsaplanner.repository.ExpenseEntity
import org.hierax.hsaplanner.util.IsoDateFormatter
import java.text.NumberFormat

class ExpenseListRecyclerViewAdapter(
    private val expenseListViewModel: ExpenseListViewModel,
    private val dateFormatter: IsoDateFormatter
) : ListAdapter<ExpenseEntity, ExpenseListRecyclerViewAdapter.ExpenseViewHolder>(ExpenseComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.expense_list_item, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = getItem(position)
        holder.dateView.text = dateFormatter.format(expense.expenseDate)
        holder.descriptionView.text = expense.description
        holder.originalAmountView.text = holder.itemView.context.getString(
            R.string.original_amount_format,
            NumberFormat.getCurrencyInstance().format(expense.originalAmount)
        )
        holder.remainingAmountView.text = holder.itemView.context.getString(
            R.string.remaining_amount_format, NumberFormat.getCurrencyInstance().format(expense.remainingAmount)
        )

        val editExpenseListener = { view: View ->
            val action = ExpenseListFragmentDirections.actionExpensesFragmentToEditExpensesFragment(expense.id)
            view.findNavController().navigate(action)
        }

        holder.itemView.setOnClickListener(editExpenseListener)

        holder.itemView.setOnCreateContextMenuListener { contextMenu, view, _ ->
            contextMenu.add(R.string.action_edit_expense).setOnMenuItemClickListener {
                editExpenseListener(view)
                true
            }
            contextMenu.add(R.string.action_delete).setOnMenuItemClickListener {
                expenseListViewModel.deleteExpense(expense.id)
                true
            }
        }
    }

    inner class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateView: TextView = itemView.findViewById(R.id.list_item_expense_date)
        val descriptionView: TextView = itemView.findViewById(R.id.list_item_description)
        val originalAmountView: TextView = itemView.findViewById(R.id.list_item_original_amount)
        val remainingAmountView: TextView = itemView.findViewById(R.id.list_item_remaining_amount)
    }

    class ExpenseComparator : DiffUtil.ItemCallback<ExpenseEntity>() {
        override fun areItemsTheSame(oldItem: ExpenseEntity, newItem: ExpenseEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ExpenseEntity, newItem: ExpenseEntity): Boolean {
            return oldItem == newItem
        }
    }
}
package org.hierax.hsaplanner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.hierax.hsaplanner.data.Expense
import org.hierax.hsaplanner.databinding.FragmentExpenseListBinding
import java.math.BigDecimal
import java.time.LocalDate

class ExpensesFragment : Fragment() {
    private var binding: FragmentExpenseListBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val fragmentBinding = FragmentExpenseListBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val expenses = listOf(
            Expense(
                1,
                "Expense 1",
                LocalDate.of(2018, 12, 17),
                BigDecimal("6098.56"),
                BigDecimal("3098.56")
            ),
            Expense(
                2,
                "Expense 2",
                LocalDate.of(2021, 1, 12),
                BigDecimal("2833.72"),
                BigDecimal("2833.72")
            )
        )

        binding?.apply {
            val recyclerView = recyclerViewExpenses
            recyclerView.setHasFixedSize(true)
            recyclerView.adapter = ExpenseRecyclerViewAdapter(expenses)

            buttonAddExpense.setOnClickListener { findNavController().navigate(R.id.editExpensesFragment) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
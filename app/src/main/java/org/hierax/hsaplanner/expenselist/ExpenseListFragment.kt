package org.hierax.hsaplanner.expenselist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import org.hierax.hsaplanner.R
import org.hierax.hsaplanner.databinding.FragmentExpenseListBinding
import org.hierax.hsaplanner.util.IsoDateFormatter
import javax.inject.Inject

@AndroidEntryPoint
class ExpenseListFragment @Inject constructor(
    private val dateFormatter: IsoDateFormatter
) : Fragment() {
    private lateinit var binding: FragmentExpenseListBinding
    private val expenseListViewModel: ExpenseListViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val fragmentBinding = FragmentExpenseListBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            val recyclerView = recyclerViewExpenses
            recyclerView.setHasFixedSize(true)
            val adapter = ExpenseListRecyclerViewAdapter(expenseListViewModel, dateFormatter)
            expenseListViewModel.allExpenses.observe(viewLifecycleOwner, { expenses ->
                expenses?.let { adapter.submitList(it) }
            })
            recyclerView.adapter = adapter

            buttonAddExpense.setOnClickListener { findNavController().navigate(R.id.editExpensesFragment) }
        }
    }
}
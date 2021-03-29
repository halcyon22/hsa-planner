package com.example.hsaplanner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.hsaplanner.data.BalanceFactory
import com.example.hsaplanner.data.Expense
import com.example.hsaplanner.databinding.FragmentBalanceBinding
import java.math.BigDecimal
import java.time.LocalDate

class BalanceFragment : Fragment() {
    private var binding: FragmentBalanceBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val fragmentBinding = FragmentBalanceBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val expenses = listOf(
            Expense(
                "Expense 1",
                LocalDate.of(2018, 12, 17),
                BigDecimal("6098.56"),
                BigDecimal("3098.56")
            ),
            Expense(
                "Expense 2",
                LocalDate.of(2021, 1, 12),
                BigDecimal("2833.72"),
                BigDecimal("2833.72")
            )
        )

        val balanceLines = BalanceFactory(requireContext(), expenses).makeBalanceLines()

        val recyclerView = binding?.balanceRecyclerView
        recyclerView?.adapter = BalanceLineAdapter(balanceLines)
        recyclerView?.setHasFixedSize(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
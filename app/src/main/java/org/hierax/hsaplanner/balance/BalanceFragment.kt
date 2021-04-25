package org.hierax.hsaplanner.balance

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import org.hierax.hsaplanner.HsaPlannerApplication
import org.hierax.hsaplanner.R
import org.hierax.hsaplanner.SettingsViewModel
import org.hierax.hsaplanner.SettingsViewModelFactory
import org.hierax.hsaplanner.data.BalanceFactory
import org.hierax.hsaplanner.data.Expense
import org.hierax.hsaplanner.databinding.FragmentBalanceBinding
import org.hierax.hsaplanner.repository.SettingsEntity
import java.math.BigDecimal
import java.time.LocalDate

class BalanceFragment : Fragment() {
    private var binding: FragmentBalanceBinding? = null
    private val settingsModel: SettingsViewModel by activityViewModels {
        val hsaPlannerApplication = activity?.application as HsaPlannerApplication
        SettingsViewModelFactory(hsaPlannerApplication.settingsDao)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val fragmentBinding = FragmentBalanceBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO replace with ExpenseEntity loaded along with settings using MediatorLiveData
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

        val recyclerView = binding?.recyclerViewBalanceLines
        recyclerView?.setHasFixedSize(true)

        settingsModel.settings.observe(viewLifecycleOwner, object : Observer<SettingsEntity?> {
            override fun onChanged(settings: SettingsEntity?) {
                settings ?: return

                val balanceLines = BalanceFactory(
                    settings.currentBalance,
                    settings.personalContribution,
                    settings.employerContribution,
                    settings.reimbursementThreshold,
                    settings.reimbursementMax,
                    expenses,
                    requireContext()
                ).makeBalanceLines()

                recyclerView?.adapter = BalanceLineAdapter(balanceLines)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                findNavController().navigate(R.id.settingsFragment)
                return true
            }
            R.id.action_expenses -> {
                findNavController().navigate(R.id.expensesFragment)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
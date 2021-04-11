package org.hierax.hsaplanner

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
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
        SettingsViewModelFactory(hsaPlannerApplication.settingsDao, CoroutineScope(SupervisorJob()))
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

        val recyclerView = binding?.balanceRecyclerView
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

                Log.i(TAG, "settings observer onChanged: updating BalanceLineAdapter")
                recyclerView?.adapter = BalanceLineAdapter(balanceLines)
            }
        })

        Log.i(TAG, "BalanceFragment.onViewCreated: end")
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
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
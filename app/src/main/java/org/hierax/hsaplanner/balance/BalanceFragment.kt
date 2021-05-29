package org.hierax.hsaplanner.balance

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import org.hierax.hsaplanner.R
import org.hierax.hsaplanner.databinding.FragmentBalanceBinding
import org.hierax.hsaplanner.expenselist.ExpenseListViewModel
import org.hierax.hsaplanner.settings.SettingsViewModel

@AndroidEntryPoint
class BalanceFragment : Fragment() {
    private lateinit var binding: FragmentBalanceBinding
    private val settingsModel: SettingsViewModel by viewModels()
    private val expenseListViewModel: ExpenseListViewModel by viewModels()

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

        val recyclerView = binding.recyclerViewBalanceLines
        recyclerView.setHasFixedSize(true)

        // fires when both of the inputs are ready, and when either one changes
        BalanceInputLiveData(expenseListViewModel, settingsModel)
            .observe(viewLifecycleOwner, { (expenseEntities, settings) ->
                val balanceLines = BalanceFactory(settings, expenseEntities).makeBalanceLines()
                recyclerView.adapter = BalanceRecyclerViewAdapter(balanceLines)
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
}


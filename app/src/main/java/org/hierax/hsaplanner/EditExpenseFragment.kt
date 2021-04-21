package org.hierax.hsaplanner

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import org.hierax.hsaplanner.databinding.EditExpenseFragmentBinding
import java.time.LocalDate

class EditExpenseFragment : Fragment() {
    private var _binding: EditExpenseFragmentBinding? = null
    private val binding get() = _binding!! // hack to avoid ?. during phases when it will be defined
    private val editExpenseModel: EditExpenseViewModel by viewModels()
    private var expenseId = 0

    companion object {
        const val EXPENSE_ID_ARG = "expenseId"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        arguments?.let {
            expenseId = it.getInt(EXPENSE_ID_ARG)
            Log.i(TAG, "onCreate: got argument: $expenseId")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val fragmentBinding = EditExpenseFragmentBinding.inflate(inflater, container, false)
        _binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = editExpenseModel
            fragment = this@EditExpenseFragment

            textInputOriginalAmount.filters = arrayOf(MoneyInputFilter())
            textInputRemainingAmount.filters = arrayOf(MoneyInputFilter())

            imageButtonOpenDateDialog.setOnClickListener {
                val selectedDate = editExpenseModel.expenseDate.value ?: LocalDate.now()
                DatePickerDialog(
                    requireContext(), { _, year, month, day ->
                        editExpenseModel.expenseDate.value = LocalDate.of(year, month, day)
                    },
                    selectedDate.year,
                    selectedDate.monthValue,
                    selectedDate.dayOfMonth
                ).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_edit_expense, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_save -> {
                handleSave()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun handleSave() {
        Toast.makeText(view?.context, "saving!", LENGTH_SHORT).show()

        Log.i(TAG, "handleSave: ${binding.textInputExpenseDate.text}")
        Log.i(TAG, "handleSave: ${binding.textInputDescription.text}")
        Log.i(TAG, "handleSave: ${binding.textInputOriginalAmount.text}")
        Log.i(TAG, "handleSave: ${binding.textInputRemainingAmount.text}")

        // TODO validate

        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
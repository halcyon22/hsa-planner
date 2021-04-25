package org.hierax.hsaplanner.editexpense

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import org.hierax.hsaplanner.HsaPlannerApplication
import org.hierax.hsaplanner.MoneyInputFilter
import org.hierax.hsaplanner.R
import org.hierax.hsaplanner.databinding.FragmentEditExpenseBinding
import java.time.LocalDate

class EditExpenseFragment : Fragment() {
    private var _binding: FragmentEditExpenseBinding? = null
    private val binding get() = _binding!! // hack to avoid ?. during phases when it will be defined
    private val editExpenseModel: EditExpenseViewModel by viewModels {
        val hsaPlannerApplication = activity?.application as HsaPlannerApplication
        EditExpenseViewModelFactory(hsaPlannerApplication.expenseDao)
    }
    private var expenseId = 0

    companion object {
        const val EXPENSE_ID_ARG = "expenseId"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        arguments?.let {
            expenseId = it.getInt(EXPENSE_ID_ARG)
            editExpenseModel.loadExpense(expenseId)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val fragmentBinding = FragmentEditExpenseBinding.inflate(inflater, container, false)
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
                        editExpenseModel.setExpenseDate(LocalDate.of(year, month, day))
                    },
                    selectedDate.year,
                    selectedDate.monthValue-1,
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
        var validationFailure = false
        if (!editExpenseModel.isValidDate(binding.textInputExpenseDate.text.toString())) {
            binding.textInputLayoutExpenseDate.isErrorEnabled = true
            binding.textInputLayoutExpenseDate.error = getString(R.string.invalid_date)
            validationFailure = true
        }
        if (!editExpenseModel.isValidDescription(binding.textInputDescription.text.toString())) {
            binding.textInputLayoutDescription.isErrorEnabled = true
            binding.textInputLayoutDescription.error = getString(R.string.invalid_description)
            validationFailure = true
        }
        if (!editExpenseModel.isValidDouble(binding.textInputOriginalAmount.text.toString())) {
            binding.textInputLayoutOriginalAmount.isErrorEnabled = true
            binding.textInputLayoutOriginalAmount.error = getString(R.string.invalid_amount)
            validationFailure = true
        }
        if (!editExpenseModel.isValidDouble(binding.textInputRemainingAmount.text.toString())) {
            binding.textInputLayoutRemainingAmount.isErrorEnabled = true
            binding.textInputLayoutRemainingAmount.error = getString(R.string.invalid_amount)
            validationFailure = true
        }

        if (!validationFailure) {
            editExpenseModel.saveExpense(
                binding.textInputDescription.text.toString(),
                binding.textInputOriginalAmount.text.toString().toDouble(),
                binding.textInputRemainingAmount.text.toString().toDouble()
            )
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package org.hierax.hsaplanner.editexpense

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import org.hierax.hsaplanner.R
import org.hierax.hsaplanner.databinding.FragmentEditExpenseBinding
import org.hierax.hsaplanner.settings.MoneyInputFilter
import java.time.LocalDate

class EditExpenseFragment : Fragment() {
    private lateinit var binding: FragmentEditExpenseBinding
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
            editExpenseModel.loadExpense(expenseId)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val fragmentBinding = FragmentEditExpenseBinding.inflate(inflater, container, false)
        binding = fragmentBinding
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
                val initialDate = editExpenseModel.expenseDate.value ?: LocalDate.now()
                DatePickerDialog(
                    requireContext(),
                    { _, year, month, day -> // date set listener
                        editExpenseModel.setExpenseDate(LocalDate.of(year, month + 1, day))
                    },
                    // initial selections
                    initialDate.year,
                    initialDate.monthValue - 1,
                    initialDate.dayOfMonth
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
                true
            }
            android.R.id.home -> {
                hideKeyboard()
                super.onOptionsItemSelected(item)
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun handleSave() {
        hideKeyboard()

        if (isFormValid()) {
            editExpenseModel.saveExpense(
                binding.textInputDescription.text.toString(),
                binding.textInputOriginalAmount.text.toString().toDouble(),
                binding.textInputRemainingAmount.text.toString().toDouble()
            )
            findNavController().popBackStack()
        }
    }

    private fun isFormValid(): Boolean {
        var valid = true
        if (!editExpenseModel.isValidDate(binding.textInputExpenseDate.text.toString())) {
            binding.textInputLayoutExpenseDate.isErrorEnabled = true
            binding.textInputLayoutExpenseDate.error = getString(R.string.invalid_date)
            valid = false
        }
        if (!editExpenseModel.isValidDescription(binding.textInputDescription.text.toString())) {
            binding.textInputLayoutDescription.isErrorEnabled = true
            binding.textInputLayoutDescription.error = getString(R.string.invalid_description)
            valid = false
        }
        if (!editExpenseModel.isValidDouble(binding.textInputOriginalAmount.text.toString())) {
            binding.textInputLayoutOriginalAmount.isErrorEnabled = true
            binding.textInputLayoutOriginalAmount.error = getString(R.string.invalid_amount)
            valid = false
        }
        if (!editExpenseModel.isValidDouble(binding.textInputRemainingAmount.text.toString())) {
            binding.textInputLayoutRemainingAmount.isErrorEnabled = true
            binding.textInputLayoutRemainingAmount.error = getString(R.string.invalid_amount)
            valid = false
        }
        return valid
    }

    private fun hideKeyboard(): Boolean {
        val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, 0)
        return true
    }
}

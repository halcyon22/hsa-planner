package org.hierax.hsaplanner

import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.hierax.hsaplanner.databinding.FragmentSettingsBinding
import java.util.regex.Matcher
import java.util.regex.Pattern

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val settingsModel: SettingsViewModel by activityViewModels {
        val hsaPlannerApplication = activity?.application as HsaPlannerApplication
        SettingsViewModelFactory(hsaPlannerApplication.settingsDao, CoroutineScope(SupervisorJob()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val fragmentBinding = FragmentSettingsBinding.inflate(inflater, container, false)
        _binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = settingsModel
            fragment = this@SettingsFragment

            textInputCurrentBalance.filters = arrayOf(MoneyInputFilter())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_settings, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_save -> {
                val currentBalance = getUiValue(binding.textInputCurrentBalance, binding.textInputLayoutCurrentBalance)
                val personalContribution = getUiValue(binding.textInputPersonalContribution, binding.textInputLayoutPersonalContribution)
                val employerContribution = getUiValue(binding.textInputEmployerContribution, binding.textInputLayoutEmployerContribution)
                val reimbursementThreshold =
                    getUiValue(binding.textInputReimbursementThreshold, binding.textInputLayoutReimbursementThreshold)
                val reimbursementMax = getUiValue(binding.textInputReimbursementMax, binding.textInputLayoutReimbursementMax)

                if (currentBalance == null ||
                    personalContribution == null ||
                    employerContribution == null ||
                    reimbursementThreshold == null ||
                    reimbursementMax == null
                ) {
                    return true
                }

                settingsModel.update(
                    currentBalance,
                    personalContribution,
                    employerContribution,
                    reimbursementThreshold,
                    reimbursementMax,
                )
                findNavController().popBackStack()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getUiValue(input: TextInputEditText, inputLayout: TextInputLayout): Double? {
        val uiValue = input.text.toString().toDoubleOrNull()
        if (uiValue == null) {
            inputLayout.isErrorEnabled = true
            inputLayout.error = getString(R.string.setting_required)
            return null
        }
        inputLayout.isErrorEnabled = false
        return uiValue
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// based on https://stackoverflow.com/a/13716269/421245
class MoneyInputFilter : InputFilter {
    private val inputPattern = Pattern.compile("(0|[1-9]+[0-9]*)?(\\.[0-9]{0,2})?")

    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        val charsBefore = dest?.subSequence(0, dstart) ?: ""
        val charsAfter = dest?.subSequence(dend, dest.length) ?: ""
        val matcher: Matcher = inputPattern.matcher("${charsBefore}${source}${charsAfter}")
        if (!matcher.matches()) {
            return dest?.subSequence(dstart, dend)
        }
        return null
    }
}
package org.hierax.hsaplanner.settings

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.hierax.hsaplanner.HsaPlannerApplication
import org.hierax.hsaplanner.R
import org.hierax.hsaplanner.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    private val settingsModel: SettingsViewModel by viewModels {
        val hsaPlannerApplication = activity?.application as HsaPlannerApplication
        SettingsViewModelFactory(hsaPlannerApplication.settingsDao)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val fragmentBinding = FragmentSettingsBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = settingsModel
            fragment = this@SettingsFragment

            textInputCurrentBalance.filters = arrayOf(MoneyInputFilter())
            textInputPersonalContribution.filters = arrayOf(MoneyInputFilter())
            textInputEmployerContribution.filters = arrayOf(MoneyInputFilter())
            textInputReimbursementThreshold.filters = arrayOf(MoneyInputFilter())
            textInputReimbursementMax.filters = arrayOf(MoneyInputFilter())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_settings, menu)
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
        val currentBalance = getValidSettingFromUi(binding.textInputCurrentBalance, binding.textInputLayoutCurrentBalance)
        val personalContribution = getValidSettingFromUi(binding.textInputPersonalContribution, binding.textInputLayoutPersonalContribution)
        val employerContribution = getValidSettingFromUi(binding.textInputEmployerContribution, binding.textInputLayoutEmployerContribution)
        val reimbursementThreshold =
            getValidSettingFromUi(binding.textInputReimbursementThreshold, binding.textInputLayoutReimbursementThreshold)
        val reimbursementMax = getValidSettingFromUi(binding.textInputReimbursementMax, binding.textInputLayoutReimbursementMax)

        if (currentBalance == null ||
            personalContribution == null ||
            employerContribution == null ||
            reimbursementThreshold == null ||
            reimbursementMax == null
        ) {
            return
        }

        settingsModel.update(currentBalance, personalContribution, employerContribution, reimbursementThreshold, reimbursementMax)
        findNavController().popBackStack()
    }

    private fun getValidSettingFromUi(input: TextInputEditText, inputLayout: TextInputLayout): Double? {
        val uiValue = input.text.toString().toDoubleOrNull()
        if (uiValue == null) {
            inputLayout.isErrorEnabled = true
            inputLayout.error = getString(R.string.setting_required)
            return null
        }
        inputLayout.isErrorEnabled = false
        return uiValue
    }
}

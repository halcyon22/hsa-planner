package org.hierax.hsaplanner

import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import org.hierax.hsaplanner.databinding.FragmentSettingsBinding
import java.math.BigDecimal
import java.util.regex.Matcher
import java.util.regex.Pattern

class SettingsFragment: Fragment() {
    private var binding: FragmentSettingsBinding? = null
    private val settingsModel: SettingsViewModel by activityViewModels()

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

        settingsModel.setCurrentBalance(BigDecimal("2650.00"))

        binding?.apply {
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
                Toast.makeText(activity, R.string.settings_saved, Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
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
package com.hover.stax.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.gson.JsonObject
import com.hover.sdk.actions.HoverAction
import com.hover.sdk.api.Hover
import com.hover.sdk.permissions.PermissionHelper
import com.hover.stax.BuildConfig
import com.hover.stax.R
import com.hover.stax.accounts.Account
import com.hover.stax.bounties.BountyActivity
import com.hover.stax.databinding.FragmentSettingsBinding
import com.hover.stax.home.MainActivity
import com.hover.stax.languages.LanguageViewModel
import com.hover.stax.library.LibraryActivity
import com.hover.stax.navigation.NavigationInterface
import com.hover.stax.permissions.PermissionsFragment
import com.hover.stax.utils.Constants
import com.hover.stax.utils.UIHelper
import com.hover.stax.utils.Utils
import com.hover.stax.views.AbstractStatefulInput
import com.hover.stax.views.StaxTextInputLayout
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber


class SettingsFragment : Fragment(), NavigationInterface {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private var accountAdapter: ArrayAdapter<Account>? = null
    private var clickCounter = 0

    private val viewModel: SettingsViewModel by sharedViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Utils.logAnalyticsEvent(getString(R.string.visit_screen, getString(R.string.visit_security)), requireActivity())

        setUpShare()
        setUpMeta(viewModel)
        setUpChooseLang()
        setUpSupport()
        setUpUssdLibrary()
        setUpEnableTestMode()
        setupAppVersionInfo()
    }

    private fun setUpShare() {
        binding.shareCard.shareText.setOnClickListener { Utils.shareStax(requireActivity()) }
        viewModel.email.observe(viewLifecycleOwner) { Timber.e("got email: %s", it); updateReferralInfo(it) }
        viewModel.fetchUsername()
    }

    private fun updateReferralInfo(email: String?) {
        binding.shareCard.refereeLayout.visibility = if (!email.isNullOrEmpty()) VISIBLE else GONE
        binding.shareCard.openRefereeBtn.setOnClickListener { openRefereeDialog() }
    }

    private fun openRefereeDialog() {
        Utils.logAnalyticsEvent(getString(R.string.referrals_tap), requireContext())
        ReferralDialog().show(childFragmentManager, ReferralDialog.TAG)
    }

    private fun setUpMeta(viewModel: SettingsViewModel) {
        binding.settingsCard.connectAccounts.setOnClickListener { (activity as MainActivity).checkPermissionsAndNavigate(Constants.NAV_LINK_ACCOUNT) }
        viewModel.accounts.observe(viewLifecycleOwner) {
            if (it.isNullOrEmpty()) {
                binding.settingsCard.defaultAccountEntry.visibility = GONE
                binding.settingsCard.connectAccounts.visibility = VISIBLE
            } else
                createDefaultSelector(it)
        }
    }

    private fun createDefaultSelector(accounts: List<Account>) {
        val spinner = binding.settingsCard.defaultAccountSpinner
        binding.settingsCard.defaultAccountEntry.visibility = VISIBLE
        spinner.setAdapter(accountAdapter)
        spinner.setText(accounts.first { it.isDefault }.alias, false);
        spinner.onItemClickListener = OnItemClickListener { _, _, pos: Int, _ -> if (pos != 0) viewModel.setDefaultAccount(accounts[pos]) }
    }

    private fun setUpChooseLang() {
        val selectLangBtn = binding.languageCard.selectLanguageBtn
        val languageVM = getViewModel<LanguageViewModel>()
        languageVM.loadLanguages().observe(viewLifecycleOwner) { langs ->
            langs.forEach {
                if (it.isSelected) selectLangBtn.text = it.name
            }
        }

        selectLangBtn.setOnClickListener { navigateToLanguageSelectionFragment(requireActivity()) }
    }

    private fun setUpSupport() {
        with(binding.staxSupport) {
            twitterContact.setOnClickListener { Utils.openUrl(getString(R.string.stax_twitter_url), requireActivity()) }
            receiveStaxUpdate.setOnClickListener { Utils.openUrl(getString(R.string.receive_stax_updates_url), requireActivity()) }
            requestFeature.setOnClickListener { Utils.openUrl(getString(R.string.stax_nolt_url), requireActivity()) }
            contactSupport.setOnClickListener { Utils.openEmail(getString(R.string.stax_emailing_subject, Hover.getDeviceId(requireContext())), requireContext()) }
            faq.setOnClickListener { navigateFAQ(this@SettingsFragment) }
        }
    }

    private fun setUpUssdLibrary() = binding.libraryCard.visitLibrary.setOnClickListener {
        requireActivity().startActivity(Intent(requireActivity(), LibraryActivity::class.java))
    }

    private fun setUpEnableTestMode() {
        binding.settingsCard.testMode.setOnCheckedChangeListener { _, isChecked ->
            Utils.saveBoolean(Constants.TEST_MODE, isChecked, requireContext())
            UIHelper.flashMessage(requireContext(), if (isChecked) R.string.test_mode_toast else R.string.test_mode_disabled)
        }
        binding.settingsCard.testMode.visibility = if (Utils.getBoolean(Constants.TEST_MODE, requireContext())) VISIBLE else GONE
        binding.disclaimer.setOnClickListener {
            clickCounter++
            if (clickCounter == 5) UIHelper.flashMessage(requireContext(), R.string.test_mode_almost_toast) else if (clickCounter == 7) enableTestMode()
        }
    }

    private fun enableTestMode() {
        Utils.saveBoolean(Constants.TEST_MODE, true, requireActivity())
        binding.settingsCard.testMode.visibility = VISIBLE
        UIHelper.flashMessage(requireContext(), R.string.test_mode_toast)
    }

    private fun setupAppVersionInfo() {
        val deviceId = Hover.getDeviceId(requireContext())
        val appVersion: String = BuildConfig.VERSION_NAME
        val versionCode: String = java.lang.String.valueOf(BuildConfig.VERSION_CODE)
        binding.staxAndDeviceInfo.text = getString(R.string.app_version_and_device_id, appVersion, versionCode, deviceId)
    }

    companion object {
        @JvmField
        val LANG_CHANGE = "Settings"
    }
}
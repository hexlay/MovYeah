package hexlay.movyeah.fragments

import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import hexlay.movyeah.R
import hexlay.movyeah.activities.AboutActivity
import hexlay.movyeah.activities.SettingsActivity
import hexlay.movyeah.helpers.PreferenceHelper
import hexlay.movyeah.helpers.translateLanguage
import hexlay.movyeah.helpers.translateQuality
import org.jetbrains.anko.support.v4.startActivity

class SettingsFragment : PreferenceFragmentCompat() {

    private lateinit var brightness: SwitchPreference
    private lateinit var downloadNotification: SwitchPreference
    private lateinit var lang: ListPreference
    private lateinit var quality: ListPreference
    private lateinit var darkMode: ListPreference
    private lateinit var about: Preference

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        initPreferences()
    }

    private fun initPreferences() {
        addPreferencesFromResource(R.xml.settings)
        brightness = preferenceManager.findPreference("brightness")!!
        darkMode = preferenceManager.findPreference("darknet")!!
        downloadNotification = preferenceManager.findPreference("download_notification")!!
        lang = preferenceManager.findPreference("lang_value")!!
        quality = preferenceManager.findPreference("qual_value")!!
        about = preferenceManager.findPreference("about_pref")!!

        lang.summary = PreferenceHelper.lang.translateLanguage(requireContext())
        quality.summary = PreferenceHelper.quality.translateQuality(requireContext())

        about.setOnPreferenceClickListener {
            startActivity<AboutActivity>()
            false
        }

        lang.setOnPreferenceChangeListener { _, newValue ->
            val newLang = newValue.toString()
            PreferenceHelper.lang = newLang
            lang.summary = newLang.translateLanguage(requireContext())
            true
        }

        quality.setOnPreferenceChangeListener { _, newValue ->
            val newQuality = newValue.toString()
            PreferenceHelper.quality = newQuality
            quality.summary = newQuality.translateQuality(requireContext())
            true
        }

        downloadNotification.setOnPreferenceChangeListener { _, newValue ->
            val currentCase = newValue.toString().toBoolean()
            downloadNotification.isChecked = currentCase
            PreferenceHelper.downloadNotification = currentCase
            currentCase
        }

        brightness.setOnPreferenceChangeListener { _, newValue ->
            val currentCase = newValue.toString().toBoolean()
            brightness.isChecked = currentCase
            PreferenceHelper.maxBrightness = currentCase
            currentCase
        }

        darkMode.setOnPreferenceChangeListener { _, newValue ->
            val newMode = newValue.toString().toInt()
            PreferenceHelper.darkMode = newMode
            (requireActivity() as SettingsActivity).transitionRecreate()
            true
        }
    }

}

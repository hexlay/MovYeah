package hexlay.movyeah.fragments

import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import androidx.recyclerview.widget.RecyclerView
import hexlay.movyeah.R
import hexlay.movyeah.activities.AboutActivity
import hexlay.movyeah.activities.MainActivity
import hexlay.movyeah.helpers.*
import kotlinx.android.synthetic.main.fragment_settings.*
import org.jetbrains.anko.support.v4.startActivity
import java.lang.ref.WeakReference

class SettingsFragment : PreferenceFragmentCompat() {

    private lateinit var preferenceHelper: PreferenceHelper
    private lateinit var brightness: SwitchPreference
    private lateinit var autoPlay: SwitchPreference
    private lateinit var notifications: SwitchPreference
    private lateinit var pipMode: SwitchPreference
    private lateinit var serviceCategory: ListPreference
    private lateinit var seek: ListPreference
    private lateinit var lang: ListPreference
    private lateinit var quality: ListPreference
    private lateinit var darkMode: ListPreference
    private lateinit var about: Preference
    private lateinit var reference: WeakReference<MainActivity>

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        reference = WeakReference(activity as MainActivity)
        preferenceHelper = PreferenceHelper(reference.get()!!)
        val height = getStatusBarHeight() + getActionBarSize()
        toolbar.setNavigationOnClickListener { reference.get()!!.supportFragmentManager.popBackStack() }
        toolbar.setPadding(0, getStatusBarHeight(), 0, 0)
        toolbar.setSize(height = height)
        listView.setPadding(0, height, 0, 0)
        listView.clipToPadding = false
        listView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            val initial = listView.top

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                toolbar.elevation = if (listView.getChildAt(0).top < initial)
                    dpOf(4).toFloat()
                else
                    dpOf(0.1F)
            }

        })
        initPreferences()
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {}

    private fun initPreferences() {
        addPreferencesFromResource(R.xml.settings)
        pipMode = preferenceManager.findPreference("pip")!!
        brightness = preferenceManager.findPreference("brightness")!!
        darkMode = preferenceManager.findPreference("darknet")!!
        notifications = preferenceManager.findPreference("notifications")!!
        autoPlay = preferenceManager.findPreference("autostart")!!
        serviceCategory = preferenceManager.findPreference("service_value")!!
        seek = preferenceManager.findPreference("seek_value")!!
        lang = preferenceManager.findPreference("lang_value")!!
        quality = preferenceManager.findPreference("qual_value")!!
        about = preferenceManager.findPreference("about_pref")!!

        if (Constants.isAndroidO)
            pipMode.isEnabled = true

        serviceCategory.summary = getString(if (preferenceHelper.notificationType == 0) R.string.menu_movies else R.string.menu_series)
        seek.summary = getString(R.string.settings_main_seek_seconds).format((preferenceHelper.seek / 1000))
        lang.summary = preferenceHelper.lang.translateLanguage(requireContext())
        quality.summary = preferenceHelper.quality.translateQuality(requireContext())

        about.setOnPreferenceClickListener {
            startActivity<AboutActivity>()
            false
        }

        serviceCategory.setOnPreferenceChangeListener { _, newValue ->
            val newNotificationType = newValue.toString().toInt()
            preferenceHelper.notificationType = newNotificationType
            serviceCategory.summary = getString(if (newNotificationType == 0) R.string.menu_movies else R.string.menu_series)
            true
        }

        lang.setOnPreferenceChangeListener { _, newValue ->
            val newLang = newValue.toString()
            preferenceHelper.lang = newLang
            lang.summary = newLang.translateLanguage(requireContext())
            true
        }

        quality.setOnPreferenceChangeListener { _, newValue ->
            val newQuality = newValue.toString()
            preferenceHelper.quality = newQuality
            quality.summary = newQuality.translateQuality(requireContext())
            true
        }

        seek.setOnPreferenceChangeListener { _, newValue ->
            val newSeek = newValue.toString().toInt()
            preferenceHelper.seek = newSeek
            seek.summary = getString(R.string.settings_main_seek_seconds).format((newSeek / 1000).toString())
            true
        }

        notifications.setOnPreferenceChangeListener { _, newValue ->
            val currentCase = newValue.toString().toBoolean()
            notifications.isChecked = currentCase
            preferenceHelper.getNotifications = currentCase
            if (currentCase) {
                reference.get()!!.initSync()
            } else {
                reference.get()!!.stopSync()
            }
            currentCase
        }

        autoPlay.setOnPreferenceChangeListener { _, newValue ->
            val currentCase = newValue.toString().toBoolean()
            autoPlay.isChecked = currentCase
            preferenceHelper.autoStart = currentCase
            currentCase
        }

        brightness.setOnPreferenceChangeListener { _, newValue ->
            val currentCase = newValue.toString().toBoolean()
            brightness.isChecked = currentCase
            preferenceHelper.maxBrightness = currentCase
            currentCase
        }

        darkMode.setOnPreferenceChangeListener { _, newValue ->
            val newMode = newValue.toString().toInt()
            preferenceHelper.darkMode = newMode
            reference.get()!!.initDarkMode()
            true
        }

        pipMode.setOnPreferenceChangeListener { _, newValue ->
            val currentCase = newValue.toString().toBoolean()
            pipMode.isChecked = currentCase
            preferenceHelper.pictureInPicture = currentCase
            currentCase
        }
    }

}

package hexlay.movyeah.activities

import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.afollestad.materialdialogs.MaterialDialog
import hexlay.movyeah.R
import hexlay.movyeah.activities.base.AbsCoreActivity
import hexlay.movyeah.adapters.MainPageAdapter
import hexlay.movyeah.api.database.view_models.DbCategoryViewModel
import hexlay.movyeah.api.database.view_models.DbCountryViewModel
import hexlay.movyeah.api.github.view_models.AlertViewModel
import hexlay.movyeah.api.github.view_models.GithubViewModel
import hexlay.movyeah.api.network.view_models.FilterAttrsViewModel
import hexlay.movyeah.fragments.*
import hexlay.movyeah.helpers.*
import hexlay.movyeah.models.events.NetworkChangeEvent
import hexlay.movyeah.models.events.StartWatchingEvent
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.jetbrains.anko.startActivity

class MainActivity : AbsCoreActivity() {

    private var searchFragment: SearchFragment? = null
    private var searchMode = false

    private lateinit var mainFragment: MainFragment
    private lateinit var moviesFragment: MoviesFragment
    private lateinit var tvShowFragment: TvShowFragment
    private lateinit var favoriteFragment: FavoriteFragment
    private lateinit var downloadFragment: DownloadFragment

    private val apiFilters by viewModels<FilterAttrsViewModel>()
    private val dbCategories by viewModels<DbCategoryViewModel>()
    private val dbCountries by viewModels<DbCountryViewModel>()
    private val apiAlerts by viewModels<AlertViewModel>()
    private val apiGithub by viewModels<GithubViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        applyExitMaterialTransform()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initActivity()
    }


    override fun initActivity() {
        super.initActivity()
        initFiltersApi()
        initFragments()
        initToolbar()
        initNavigationView()
        initStarterData()
        initAlerts()
        initUpdates()
    }

    private fun initUpdates() {
        apiGithub.fetchReleases().observeOnce(this, {
            val latest = it.first()
            showUpdateDialog(latest)
        })
    }

    private fun initAlerts() {
        apiAlerts.fetchAlerts().observeOnce(this, {
            var message = ""
            for (alert in it) {
                if (!PreferenceHelper.savedAlerts.contains(alert.id)) {
                    message += "<p>&#9679; ${alert.message}</p>"
                    PreferenceHelper.addAlertHistory(alert.id)
                }
            }
            if (message.isNotEmpty()) {
                MaterialDialog(this).show {
                    message(text = message.toHtml())
                    negativeButton(R.string.done) {
                        dismiss()
                    }
                }
            }
        })
    }

    private fun initFragments() {
        mainFragment = MainFragment()
        moviesFragment = MoviesFragment()
        tvShowFragment = TvShowFragment()
        favoriteFragment = FavoriteFragment()
        downloadFragment = DownloadFragment()
    }

    private fun initFiltersApi() {
        apiFilters.fetchCategories().observeOnce(this, {
            if (it != null) {
                for (category in it) {
                    dbCategories.insertCategory(category)
                }
            }
        })
        apiFilters.fetchCountries().observeOnce(this, {
            if (it != null) {
                for (country in it) {
                    dbCountries.insertCountry(country)
                }
            }
        })
    }

    private fun initToolbar() {
        setupViewPagerAdapter()
        floating_search.setSize(height = getActionBarSize() - dpOf(7))
        floating_search.setMargins(top = getStatusBarHeight() + dpOf(7))
        search_overlay.setSize(height = getStatusBarHeight() + getActionBarSize())
        button_settings.setOnClickListener {
            startActivity<SettingsActivity>()
        }
        toolbar_search.setOnClickListener {
            startSearchMode()
        }
        button_search.setOnClickListener {
            startSearchMode()
        }
    }

    private fun startSearchMode() {
        if (!isNetworkAvailable) {
            showAlert(text = getString(R.string.unable_search), color = android.R.color.holo_red_dark)
            return
        }
        if (!searchMode) {
            searchMode = true
            searchFragment = SearchFragment()
            supportFragmentManager.commit {
                setCustomAnimations(R.anim.anim_enter, R.anim.anim_exit)
                add(R.id.searcher, searchFragment!!, "search_mode")
                addToBackStack("")
            }
            navigation.isVisible = false
            button_settings.isGone = true
            toolbar_search.isFocusable = true
            toolbar_search.isCursorVisible = true
            toolbar_search.isFocusableInTouchMode = true
            toolbar_search.setOnEditorActionListener { _, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        event != null &&
                        event.action == KeyEvent.ACTION_DOWN &&
                        event.keyCode == KeyEvent.KEYCODE_ENTER) {
                    if (event == null || !event.isShiftPressed) {
                        val searchText = toolbar_search.text.toString()
                        searchFragment?.search(searchText)
                        toolbar_search.hideKeyboard()
                        return@setOnEditorActionListener true
                    }
                }
                false
            }
            button_search.setOnClickListener {
                stopSearchMode()
            }
            button_search.morph()
            toolbar_search.requestFocus()
            toolbar_search.showKeyboard()
        }
    }

    private fun stopSearchMode() {
        if (searchMode) {
            searchMode = false
            val fragment = supportFragmentManager.findFragmentByTag("search_mode")
            if (fragment != null) {
                supportFragmentManager.commit {
                    setCustomAnimations(R.anim.anim_enter, R.anim.anim_exit)
                    remove(fragment)
                }
            }
            searchFragment = null
            navigation.isVisible = true
            button_settings.isGone = false
            toolbar_search.isFocusable = false
            toolbar_search.isCursorVisible = false
            toolbar_search.isFocusableInTouchMode = false
            toolbar_search.setOnEditorActionListener(null)
            toolbar_search.text?.clear()
            button_search.morph()
            button_search.setOnClickListener {
                startSearchMode()
            }
            toolbar_search.hideKeyboard()
        }

    }

    private fun initNavigationView() {
        navigation.onItemSelected = {
            fragment_pager.currentItem = it
        }
    }

    private fun initStarterData() {
        if (intent.extras != null && !intent.extras!!.isEmpty) {
            if (intent.hasExtra("movie")) {
                lifecycleScope.launch {
                    EventBus.getDefault().post(StartWatchingEvent(intent.getParcelableExtra("movie")!!))
                }
            }
        }
    }

    private fun setupViewPagerAdapter() {
        val adapter = MainPageAdapter(this)
        if (isNetworkAvailable) {
            adapter.addFragment(mainFragment)
            adapter.addFragment(moviesFragment)
            adapter.addFragment(tvShowFragment)
            adapter.addFragment(favoriteFragment)
        } else {
            navigation.isGone = true
        }
        adapter.addFragment(downloadFragment)
        fragment_pager.adapter = adapter
        fragment_pager.offscreenPageLimit = adapter.itemCount
        fragment_pager.currentItem = 0

        fragment_pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                navigation.itemActiveIndex = position
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    override fun onBackPressed() {
        when {
            searchMode -> {
                stopSearchMode()
            }
            supportFragmentManager.findFragmentByTag("settings") != null -> {
                supportFragmentManager.popBackStack()
            }
            fragment_pager.currentItem != 0 -> {
                fragment_pager.currentItem = 0
            }
            else -> {
                super.onBackPressed()
            }
        }
    }

    @Subscribe
    fun listenNetworkChange(event: NetworkChangeEvent) {
        if (event.isConnected && !isNetworkAvailable) {
            isNetworkAvailable = true
            fragment_pager.adapter = null
            setupViewPagerAdapter()
            navigation.isGone = false
        }
        if (!event.isConnected && isNetworkAvailable) {
            isNetworkAvailable = false
            fragment_pager.adapter = null
            setupViewPagerAdapter()
            navigation.isGone = true
        }
    }

}

package hexlay.movyeah.activities

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.ViewPager
import hexlay.movyeah.R
import hexlay.movyeah.activities.base.AbsWatchModeActivity
import hexlay.movyeah.adapters.MainPageAdapter
import hexlay.movyeah.api.database.view_models.DbCategoryViewModel
import hexlay.movyeah.api.database.view_models.DbCountryViewModel
import hexlay.movyeah.api.helpers.isNetworkAvailable
import hexlay.movyeah.api.network.view_models.FilterAttrsViewModel
import hexlay.movyeah.fragments.*
import hexlay.movyeah.helpers.*
import hexlay.movyeah.models.events.NetworkChangeEvent
import hexlay.movyeah.services.NotificationServiceJob
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.navigation
import kotlinx.android.synthetic.main.fragment_watch.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.Subscribe

class MainActivity : AbsWatchModeActivity() {

    private var searchFragment: SearchFragment? = null
    private var searchMode = false
    private var searchAdapter: ArrayAdapter<String>? = null
    private var isNetworkAvailable = true

    private lateinit var mainFragment: MainFragment
    private lateinit var moviesFragment: MoviesFragment
    private lateinit var tvShowFragment: TvShowFragment
    private lateinit var favoriteFragment: FavoriteFragment
    private lateinit var downloadFragment: DownloadFragment

    override var networkView: Int = R.id.navigation

    private val apiFilters by viewModels<FilterAttrsViewModel>()
    private val dbCategories by viewModels<DbCategoryViewModel>()
    private val dbCountries by viewModels<DbCountryViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        isNetworkAvailable = isNetworkAvailable()
        initActivity()
    }


    override fun initActivity() {
        super.initActivity()
        initFiltersApi()
        makeFullscreen()
        initFragments()
        initToolbar()
        initNavigationView()
        initSync()
        initDarkMode()
        initHistory()
        initStarterData()
    }

    private fun initFragments() {
        mainFragment = MainFragment()
        moviesFragment = MoviesFragment()
        tvShowFragment = TvShowFragment()
        favoriteFragment = FavoriteFragment()
        downloadFragment = DownloadFragment()
    }

    private fun initFiltersApi() {
        apiFilters.fetchCategories().observeOnce(this, Observer {
            for (category in it) {
                dbCategories.insertCategory(category)
            }
        })
        apiFilters.fetchCountries().observeOnce(this, Observer {
            for (country in it) {
                dbCountries.insertCountry(country)
            }
        })
    }

    fun initDarkMode() {
        when (PreferenceHelper.darkMode) {
            0 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            2 -> if (Constants.isAndroidQ) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
            }
        }
        delegate.applyDayNight()
    }

    private fun initHistory() {
        searchAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, PreferenceHelper.searchHistory.toTypedArray())
        toolbar_search.setAdapter(searchAdapter)
    }

    private fun initToolbar() {
        setupViewPager()
        floating_search.setSize(height = getActionBarSize() - dpOf(7))
        floating_search.setMargins(top = getStatusBarHeight() + dpOf(7))
        search_overlay.setSize(height = getStatusBarHeight() + getActionBarSize())
        button_settings.setOnClickListener {
            enterPreference()
        }
        toolbar_search.setOnClickListener {
            startSearchMode()
        }
        button_search.setOnClickListener {
            startSearchMode()
        }
        setSupportActionBar(toolbar)
    }

    private fun startSearchMode() {
        if (!searchMode && isNetworkAvailable) {
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
                        PreferenceHelper.addSearchHistory(searchText)
                        searchAdapter?.add(searchText)
                        toolbar_search.hideKeyboard()
                        return@setOnEditorActionListener true
                    }
                }
                false
            }
            button_search.setOnClickListener {
                stopSearchMode()
            }
            button_search.setImageDrawable(getDrawable(R.drawable.ic_arrow_back_w))
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
            toolbar_search.text.clear()
            button_search.setOnClickListener {
                startSearchMode()
            }
            button_search.setImageDrawable(getDrawable(R.drawable.ic_search))
            toolbar_search.hideKeyboard()
        }

    }

    private fun initNavigationView() {
        navigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_main -> fragment_pager.currentItem = 0
                R.id.nav_movies -> fragment_pager.currentItem = 1
                R.id.nav_series -> fragment_pager.currentItem = 2
                R.id.nav_favorites -> fragment_pager.currentItem = 3
                R.id.nav_downloads -> fragment_pager.currentItem = 4
            }
            true
        }
    }

    private fun initStarterData() {
        if (intent.extras != null && !intent.extras!!.isEmpty) {
            if (intent.hasExtra("movie")) {
                lifecycleScope.launch {
                    delay(500)
                    startWatchMode(intent.getParcelableExtra("movie")!!)
                }
            }
        }
    }

    fun initSync() {
        if (PreferenceHelper.getNotifications) {
            if (!isSyncing()) {
                val jobService = ComponentName(this, NotificationServiceJob::class.java)
                val syncInfo = JobInfo.Builder(0x1, jobService)
                        .setPeriodic(3600000) //1h; 4h - 14400000
                        .setPersisted(true)
                        .build()
                val scheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
                scheduler.schedule(syncInfo)
            }
        }
    }

    private fun isSyncing(): Boolean {
        val scheduler = getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
        if (Constants.isAndroidN) {
            return scheduler.getPendingJob(0x1) != null
        } else {
            for (jobInfo in scheduler.allPendingJobs) {
                if (jobInfo.id == 0x1) {
                    return true
                }
            }
            return false
        }
    }

    fun stopSync() {
        if (isSyncing()) {
            val scheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
            scheduler.cancel(0x1)
        }
    }

    private fun setupViewPagerAdapter() {
        val adapter = MainPageAdapter(supportFragmentManager)
        if (isNetworkAvailable) {
            adapter.addFragment(mainFragment)
            adapter.addFragment(moviesFragment)
            adapter.addFragment(tvShowFragment)
            adapter.addFragment(favoriteFragment)
        } else {
            navigation.menu.hideItem(R.id.nav_main)
            navigation.menu.hideItem(R.id.nav_movies)
            navigation.menu.hideItem(R.id.nav_series)
            navigation.menu.hideItem(R.id.nav_favorites)
        }
        adapter.addFragment(downloadFragment)
        fragment_pager.adapter = adapter
        fragment_pager.offscreenPageLimit = adapter.count
        fragment_pager.currentItem = 0
    }

    private fun setupViewPager() {
        setupViewPagerAdapter()
        fragment_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> {
                        navigation.selectedItemId = R.id.nav_main
                    }
                    1 -> {
                        navigation.selectedItemId = R.id.nav_movies
                    }
                    2 -> {
                        navigation.selectedItemId = R.id.nav_series
                    }
                    3 -> {
                        navigation.selectedItemId = R.id.nav_favorites
                    }
                    4 -> {
                        navigation.selectedItemId = R.id.nav_downloads
                    }
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    override fun onBackPressed() {
        if (isInWatchMode()) {
            if (watchFragment!!.isFullscreen) {
                requestPortrait()
            } else {
                endWatchMode()
            }
        } else if (searchMode) {
            stopSearchMode()
        } else if (supportFragmentManager.findFragmentByTag("settings") != null) {
            supportFragmentManager.popBackStack()
        } else if (fragment_pager.currentItem != 0) {
            fragment_pager.currentItem = 0
        } else {
            super.onBackPressed()
        }
    }

    private fun enterPreference() {
        if (supportFragmentManager.findFragmentByTag("settings") == null) {
            supportFragmentManager.commit {
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                add(android.R.id.content, SettingsFragment(), "settings")
                addToBackStack("")
            }
        }
    }

    @Subscribe
    fun listenNetworkChange(event: NetworkChangeEvent) {
        if (event.isConnected && !isNetworkAvailable) {
            isNetworkAvailable = true
            fragment_pager.adapter = null
            setupViewPagerAdapter()
            navigation.menu.showItem(R.id.nav_main)
            navigation.menu.showItem(R.id.nav_movies)
            navigation.menu.showItem(R.id.nav_series)
            navigation.menu.showItem(R.id.nav_favorites)
        }
    }

}

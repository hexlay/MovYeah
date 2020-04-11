package hexlay.movyeah.activities

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import hexlay.movyeah.R
import hexlay.movyeah.activities.base.AbsWatchModeActivity
import hexlay.movyeah.adapters.MainPageAdapter
import hexlay.movyeah.database.view_models.DbCategoryViewModel
import hexlay.movyeah.fragments.*
import hexlay.movyeah.helpers.*
import hexlay.movyeah.models.movie.Movie
import hexlay.movyeah.models.movie.attributes.Category
import hexlay.movyeah.services.NotificationService
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.navigation
import kotlinx.android.synthetic.main.fragment_watch.*

class MainActivity : AbsWatchModeActivity() {

    private var searchFragment: SearchFragment? = null
    private var searchMode = false
    private var searchAdapter: ArrayAdapter<String>? = null

    private val dbCategories by viewModels<DbCategoryViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initActivity()
    }

    override fun initActivity() {
        super.initActivity()
        initCategories()
        makeFullscreen()
        initToolbar()
        initNavigationView()
        initSync()
        initDarkMode()
        initStarterData()
        initHistory()
    }

    private fun initCategories() {
        val categoryIds = listOf(265, 253, 259, 252, 249, 269, 267, 264, 258, 260, 268, 256, 273, 262, 248, 266, 257, 251, 263, 255, 254, 275, 250, 317, 316, 312, 261)
        dbCategories.getCategories()?.observeOnce(this, Observer {
            if (it.size != categoryIds.size) {
                dbCategories.clearCategories()
                for (categoryId in categoryIds) {
                    val identifier = resources.getIdentifier("cat_${categoryId}", "string", packageName)
                    dbCategories.insertCategory(Category(categoryId, getString(identifier)))
                }
            }
        })
    }

    fun initDarkMode() {
        when (preferenceHelper?.darkMode) {
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
        searchAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, preferenceHelper?.searchHistory!!.toTypedArray())
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
        if (!searchMode && isNetworkAvailable()) {
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
                        preferenceHelper?.addSearchHistory(searchText)
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
            button_search.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_arrow_back_w))
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
            button_search.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_search))
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
                Handler().postDelayed({
                    startWatchMode(intent.getParcelableExtra("movie") as Movie)
                }, 500)
            }
        }
    }

    fun initSync() {
        if (preferenceHelper?.getNotifications!!) {
            if (!isSyncing()) {
                val jobService = ComponentName(this, NotificationService::class.java)
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

    private fun setupViewPager() {
        val adapter = MainPageAdapter(supportFragmentManager)
        if (isNetworkAvailable()) {
            adapter.addFragment(MainFragment())
            adapter.addFragment(MoviesFragment())
            adapter.addFragment(TvShowFragment())
            adapter.addFragment(FavoriteFragment())
        } else {
            navigation.menu.removeItem(R.id.nav_main)
            navigation.menu.removeItem(R.id.nav_movies)
            navigation.menu.removeItem(R.id.nav_series)
            navigation.menu.removeItem(R.id.nav_favorites)
        }
        adapter.addFragment(DownloadFragment())
        fragment_pager.adapter = adapter
        fragment_pager.offscreenPageLimit = adapter.count
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

}

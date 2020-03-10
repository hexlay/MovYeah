package hexlay.movyeah.activities

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.recyclical.datasource.emptyDataSource
import com.afollestad.recyclical.setup
import com.afollestad.recyclical.withItem
import hexlay.movyeah.R
import hexlay.movyeah.activities.base.AbsWatchModeActivity
import hexlay.movyeah.adapters.view_holders.MovieViewHolder
import hexlay.movyeah.api.view_models.ActorMoviesViewModel
import hexlay.movyeah.helpers.*
import hexlay.movyeah.models.movie.Movie
import hexlay.movyeah.models.movie.attributes.Actor
import kotlinx.android.synthetic.main.activity_actor_movie.*

@SuppressLint("CheckResult")
class ActorActivity : AbsWatchModeActivity() {

    private val actorMoviesViewModel by viewModels<ActorMoviesViewModel>()
    private var actor: Actor? = null

    private var page = 1
    private var loading = true
    private val source = emptyDataSource()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_actor_movie)
        initActivity()
    }

    override fun initActivity() {
        super.initActivity()
        initActorData()
        initToolbar()
        makeFullscreen()
        initActorInfo()
        initRecyclerView()
        loadMovies()
    }

    private fun initToolbar() {
        button_back.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initActorData() {
        if (intent.extras == null || intent.extras!!.isEmpty) {
            onBackPressed()
        }
        actor = intent.getParcelableExtra("actor") as Actor
    }

    private fun initActorInfo() {
        actor_name.setMargins(top = getStatusBarHeight())
        actor_image.setMargins(top = getStatusBarHeight())
        actor_name.text = actor?.getTitle()
        actor?.poster?.let { actor_image.setUrl(it) }
    }

    private fun loadMovies() {
        actor?.id?.let { id ->
            actorMoviesViewModel.fetchMovies(id, page).observeOnce(this, Observer {
                handleMovies(it)
            })
        }
    }

    private fun initRecyclerView() {
        val gridLayoutManager = GridLayoutManager(this, 2)
        movie_list.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            var pastVisibleItems = 0
            var visibleItemCount = 0
            var totalItemCount = 0

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    visibleItemCount = gridLayoutManager.childCount
                    totalItemCount = gridLayoutManager.itemCount
                    pastVisibleItems = gridLayoutManager.findFirstVisibleItemPosition()
                    if (loading) {
                        if (visibleItemCount + pastVisibleItems >= totalItemCount - 10) {
                            loading = false
                            loadMovies()
                        }
                    }
                }
            }
        })
        movie_list.setup {
            withDataSource(source)
            withLayoutManager(gridLayoutManager)
            withItem<Movie, MovieViewHolder>(R.layout.list_items_extended) {
                onBind(::MovieViewHolder) { _, item ->
                    this.bind(item, this@ActorActivity)
                }
            }
        }
    }

    private fun handleMovies(dataList: List<Movie>) {
        loading_movies.isVisible = false
        if (dataList.isNotEmpty()) {
            warning_holder.isGone = true
            if (page > 1) {
                source.addAll(dataList)
                loading = true
            } else {
                source.clear()
                source.addAll(dataList)
            }
            page++
        } else {
            if (page == 1) {
                warning_holder.text = getString(R.string.loading_news_fail)
                warning_holder.isVisible = true
            }
        }
    }

    override fun onBackPressed() {
        if (isInWatchMode()) {
            if (watchFragment!!.isFullscreen) {
                requestPortrait()
            } else {
                endWatchMode()
            }
        } else {
            super.onBackPressed()
        }
    }

}

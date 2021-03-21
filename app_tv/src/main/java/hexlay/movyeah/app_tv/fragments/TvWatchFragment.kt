package hexlay.movyeah.app_tv.fragments

import android.os.Bundle
import android.util.SparseArray
import androidx.core.util.isNotEmpty
import androidx.fragment.app.viewModels
import androidx.leanback.app.BackgroundManager
import androidx.leanback.app.DetailsSupportFragment
import androidx.leanback.widget.*
import androidx.leanback.widget.ListRow
import androidx.lifecycle.Observer
import com.afollestad.inlineactivityresult.startActivityForResult
import hexlay.movyeah.api.database.view_models.DbEpisodeViewModel
import hexlay.movyeah.api.database.view_models.DbMovieViewModel
import hexlay.movyeah.api.models.Movie
import hexlay.movyeah.api.models.attributes.Subtitle
import hexlay.movyeah.api.models.attributes.show.Episode
import hexlay.movyeah.api.models.attributes.show.EpisodeCache
import hexlay.movyeah.api.models.attributes.show.EpisodeFileData
import hexlay.movyeah.api.network.view_models.WatchViewModel
import hexlay.movyeah.app_tv.R
import hexlay.movyeah.app_tv.activities.TvPlaybackActivity
import hexlay.movyeah.app_tv.fragments.watch.EpisodeWatchFragment
import hexlay.movyeah.app_tv.helpers.*
import hexlay.movyeah.app_tv.models.PlaybackModel
import hexlay.movyeah.app_tv.models.events.StartFragmentEvent
import hexlay.movyeah.app_tv.models.events.watch.MovieErrorEvent
import hexlay.movyeah.app_tv.models.events.watch.WatchEpisodeChangeEvent
import hexlay.movyeah.app_tv.presenters.CastPresenter
import hexlay.movyeah.app_tv.presenters.DetailsDescriptionPresenter
import hexlay.movyeah.app_tv.presenters.WatchPresenter
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.*
import kotlin.collections.HashMap


class TvWatchFragment : DetailsSupportFragment() {

    private var backgroundManager: BackgroundManager? = null

    private val watchViewModel by viewModels<WatchViewModel>()
    private val dbMovie by viewModels<DbMovieViewModel>()
    private val dbEpisodes by viewModels<DbEpisodeViewModel>()

    private lateinit var movie: Movie
    private var qualityKey = "NONE"
    private var languageKey = "NONE"
    private var subtitleKey = "NONE"
    private var currentSeason = 1
    private var currentEpisode = 1
    private var genres = ""

    private var fileData: Map<String, List<EpisodeFileData>> = HashMap()
    private var subtitleData: Map<String, List<Subtitle>> = HashMap()
    private var tvShowSeasons: SparseArray<List<Episode>> = SparseArray()

    private var mainRows: DetailsOverviewRow? = null
    private var superAdapter: ArrayObjectAdapter? = null
    private var watchPresenter: WatchPresenter? = null
    private var actionAdapter: SparseArrayObjectAdapter? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        EventBus.getDefault().register(this)
        initBackgroundManager()
        initData()
        loadData()
        initCast()
    }

    private fun initBackgroundManager() {
        backgroundManager = BackgroundManager.getInstance(requireActivity())
        if (!backgroundManager!!.isAttached)
            backgroundManager!!.attach(getWindow())
    }

    private fun initData() {
        genres = movie.getGenresString()
        watchPresenter = WatchPresenter(requireContext(), DetailsDescriptionPresenter(genres))
        mainRows = DetailsOverviewRow(movie)
        actionAdapter = SparseArrayObjectAdapter()
        val classPresenterSelector = ClassPresenterSelector()
        classPresenterSelector.addClassPresenter(DetailsOverviewRow::class.java, watchPresenter)
        classPresenterSelector.addClassPresenter(ListRow::class.java, ListRowPresenter())
        superAdapter = ArrayObjectAdapter(classPresenterSelector)
        movie.getCover()?.let { backgroundManager?.setDrawableFromUrl(requireContext(), it) }
        movie.getTruePoster()?.let { mainRows?.setDrawableFromUrl(requireContext(), it) }
        mainRows!!.actionsAdapter = actionAdapter
        superAdapter!!.add(mainRows)

        watchPresenter?.setOnActionClickedListener {
            when (it.id) {
                0L -> {
                    val playback = PlaybackModel(movie, fileData, subtitleData, tvShowSeasons, qualityKey, languageKey, subtitleKey, currentSeason, currentEpisode)
                    val extras = makeParcelableExtra("playback", playback)
                    startActivityForResult<TvPlaybackActivity>(extras = extras, requestCode = 4) { _, _ ->
                        movie.getCover()?.let { image -> backgroundManager?.setDrawableFromUrl(requireContext(), image) }
                    }
                }
                1L -> {
                    val fragment = EpisodeWatchFragment.newInstance(currentSeason, currentEpisode, tvShowSeasons)
                    EventBus.getDefault().post(StartFragmentEvent("choose_episode", fragment))
                }
                3L -> {
                    dbMovie.getMovie(movie.id)?.observeOnce(this, Observer { fav ->
                        val size = actionAdapter!!.size() - 1
                        val action = actionAdapter?.get(size) as Action
                        if (fav == null) {
                            dbMovie.insertMovie(movie)
                            action.icon = getWhiteDrawable(R.drawable.ic_favorite)
                            action.label1 = "ფავორიტებიდან წაშლა"
                        } else {
                            dbMovie.deleteMovie(movie)
                            action.icon = getWhiteDrawable(R.drawable.ic_favorite_empty)
                            action.label1 = "ფავორიტებში დამატება"
                        }
                        actionAdapter?.clear(size)
                        actionAdapter?.set(size, action)
                    })
                }
            }
        }
        adapter = superAdapter
    }

    private fun loadData() {
        if (movie.isTvShow) {
            watchViewModel.fetchSingleMovie(movie.adjaraId).observeOnce(this, Observer { movieExtend ->
                if (movieExtend?.seasons != null) {
                    watchViewModel.fetchTvShowEpisodes(movie.id, movieExtend.seasons!!.data.size)
                            .observeOnce(this, Observer { seasons ->
                        if (seasons != null && seasons.isNotEmpty()) {
                            tvShowSeasons = seasons
                            setupTvShow()
                        } else {
                            EventBus.getDefault().post(MovieErrorEvent(R.string.full_error_show))
                        }
                    })
                } else {
                    EventBus.getDefault().post(MovieErrorEvent(R.string.full_error_show))
                }
            })
        } else {
            watchViewModel.fetchMovieFileData(movie.id).observeOnce(this, Observer { episode ->
                if (episode != null) {
                    fileData = episode.files.map { it.lang!! to it.files }.toMap()
                    subtitleData = episode.files.map { it.lang!! to it.subtitles }.toMap()
                    setupMovie()
                } else {
                    EventBus.getDefault().post(MovieErrorEvent(R.string.full_error_movie))
                }
            })
        }
    }

    private fun setupTvShow() {
        dbEpisodes.getEpisode(movie.id)?.observeOnce(this, Observer {
            if (it != null) {
                currentSeason = it.season
                setupTvShowEpisode(it.episode)
            } else {
                setupTvShowEpisode(0)
            }
        })
    }

    private fun setupTvShowEpisode(episode: Int) {
        currentEpisode = episode
        dbEpisodes.insertEpisode(EpisodeCache(movie.id, episode, currentSeason))
        fileData = tvShowSeasons[currentSeason][episode].files.map { it.lang!! to it.files }.toMap()
        subtitleData = tvShowSeasons[currentSeason][episode].files.map { it.lang!! to it.subtitles }.toMap()
        setupWatchKeys()
        val actions = mutableListOf(
                Action(0, getString(R.string.watch), "", getWhiteDrawable(R.drawable.action_play)),
                Action(1, "S${currentSeason}E${episode + 1}", "", getWhiteDrawable(R.drawable.ic_list))
        )
        initFavorites()
        actions.forEach {
            actionAdapter?.set(it.id.toInt(), it)
        }
    }

    private fun setupMovie() {
        setupWatchKeys()
        val actions = mutableListOf(
                Action(0, getString(R.string.watch), "", getWhiteDrawable(R.drawable.action_play))
        )
        initFavorites()
        actions.forEach {
            actionAdapter?.set(it.id.toInt(), it)
        }
    }

    private fun initFavorites() {
        dbMovie.getMovie(movie.id)?.observeOnce(this, Observer {
            val action = if (it == null) {
                Action(3, "ფავორიტებში დამატება", "", getWhiteDrawable(R.drawable.ic_favorite_empty))
            } else {
                Action(3, "ფავორიტებიდან წაშლა", "", getWhiteDrawable(R.drawable.ic_favorite))
            }
            actionAdapter?.set(actionAdapter!!.size(), action)
        })
    }

    private fun setupWatchKeys() {
        languageKey = when {
            fileData.containsKey("GEO") -> {
                "GEO"
            }
            fileData.containsKey("ENG") -> {
                "ENG"
            }
            else -> {
                fileData.keys.first()
            }
        }
        qualityKey = if (fileData[languageKey]?.map { it.quality }?.contains("HIGH")!!) {
            "HIGH"
        } else {
            fileData[languageKey]?.first()?.quality!!
        }
        if (subtitleData[languageKey]?.isNotEmpty()!!) {
            val list = subtitleData[languageKey]?.map { it.lang?.toUpperCase(Locale.ENGLISH)!! }
            subtitleKey = if (list?.contains(languageKey)!!) {
                list[list.indexOf(languageKey)]
            } else {
                list.first()
            }
        }
    }

    private fun initCast() {
        watchViewModel.fetchMovieActors(movie.adjaraId).observeOnce(this, Observer { cast ->
            if (cast != null) {
                val actorAdapter = ArrayObjectAdapter(CastPresenter(requireContext()))
                actorAdapter.addAll(0, cast)
                val headerItem = HeaderItem(0, getString(R.string.full_cast))
                superAdapter!!.add(1, ListRow(headerItem, actorAdapter))
            }
        })
    }

    @Subscribe
    fun listenEpisodeChange(event: WatchEpisodeChangeEvent) {
        currentSeason = event.season
        actionAdapter?.clear()
        setupTvShowEpisode(event.episode)
    }

    override fun onDestroyView() {
        EventBus.getDefault().unregister(this)
        super.onDestroyView()
    }

    companion object {

        fun newInstance(movie: Movie): TvWatchFragment {
            val fragment = TvWatchFragment()
            fragment.movie = movie
            return fragment
        }

    }

}
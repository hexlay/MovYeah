package hexlay.movyeah.app_tv.fragments.playback

import android.os.Bundle
import android.os.Handler
import android.util.SparseArray
import androidx.core.util.putAll
import androidx.leanback.app.PlaybackSupportFragment
import androidx.leanback.widget.*
import androidx.leanback.widget.PlaybackControlsRow.*
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import hexlay.movyeah.api.models.Movie
import hexlay.movyeah.api.models.attributes.show.Episode
import hexlay.movyeah.app_tv.R
import hexlay.movyeah.app_tv.fragments.TvPlaybackFragment
import hexlay.movyeah.app_tv.helpers.getWhiteDrawable
import hexlay.movyeah.app_tv.helpers.setDrawableFromUrl
import hexlay.movyeah.app_tv.models.PlaybackModel
import hexlay.movyeah.app_tv.presenters.DetailsPlaybackPresenter
import hexlay.movyeah.app_tv.presenters.EpisodePresenter
import java.lang.ref.WeakReference


class PlaybackControlFragment : PlaybackSupportFragment() {

    private lateinit var reference: WeakReference<TvPlaybackFragment>

    private lateinit var movie: Movie
    private var tvShowSeasons: SparseArray<List<Episode>> = SparseArray()
    private var currentSeason = 1

    private var superAdapter: ArrayObjectAdapter? = null

    private var playbackControlsRow: PlaybackControlsRow? = null
    private var playPauseAction: PlayPauseAction? = null
    private var fastForwardAction: FastForwardAction? = null
    private var rewindAction: RewindAction? = null
    private var skipNextAction: SkipNextAction? = null
    private var skipPreviousAction: SkipPreviousAction? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        backgroundType = BG_LIGHT
        isControlsOverlayAutoHideEnabled = false
        setupPlayerControls()
    }

    private fun setupPlayerControls() {
        val classPresenterSelector = ClassPresenterSelector()
        val playbackControlsRowPresenter = PlaybackControlsRowPresenter(DetailsPlaybackPresenter())
        classPresenterSelector.addClassPresenter(PlaybackControlsRow::class.java, playbackControlsRowPresenter)
        classPresenterSelector.addClassPresenter(ListRow::class.java, ListRowPresenter())
        superAdapter = ArrayObjectAdapter(classPresenterSelector)
        addPlaybackControlsRow()
        addEpisodes()
        playbackControlsRowPresenter.onActionClickedListener = OnActionClickedListener { action ->
            when (action.id) {
                playPauseAction!!.id -> {
                    if (reference.get()!!.isPlaying) {
                        reference.get()!!.pausePlayer()
                        playPauseAction!!.index = PlayPauseAction.INDEX_PLAY
                        playPauseAction!!.icon = playPauseAction!!.getDrawable(PlayPauseAction.INDEX_PLAY)
                    } else {
                        reference.get()!!.startPlayer()
                        playPauseAction!!.index = PlayPauseAction.INDEX_PAUSE
                        playPauseAction!!.icon = playPauseAction!!.getDrawable(PlayPauseAction.INDEX_PAUSE)
                    }
                    notifyPlaybackRowChanged()
                }
                fastForwardAction!!.id -> {
                    reference.get()!!.forward()
                }
                rewindAction!!.id -> {
                    reference.get()!!.rewind()
                }
                skipNextAction!!.id -> {
                    reference.get()!!.forwardSuper()
                }
                skipPreviousAction!!.id -> {
                    reference.get()!!.rewindSuper()
                }
                1L -> {
                    reference.get()!!.startQualityFragment()
                }
                2L -> {
                    reference.get()!!.startLanguageFragment()
                }
                3L -> {
                    reference.get()!!.startSubtitleFragment()
                }
            }
        }
        adapter = superAdapter
    }

    fun updateProgressBar(exoPlayer: ExoPlayer) {
        if (playbackControlsRow != null) {
            val position = exoPlayer.currentPosition
            playbackControlsRow!!.duration = exoPlayer.duration
            playbackControlsRow!!.currentPosition = position
            playbackControlsRow!!.bufferedPosition = exoPlayer.bufferedPosition
            val playbackState = exoPlayer.playbackState
            if (playbackState != Player.STATE_IDLE && playbackState != Player.STATE_ENDED) {
                var delayMs = 1000L
                if (exoPlayer.playWhenReady && playbackState == Player.STATE_READY) {
                    delayMs = 1000 - position % 1000
                    if (delayMs < 200)
                        delayMs += 1000
                }
                Handler().postDelayed({
                    updateProgressBar(exoPlayer)
                }, delayMs)
            }
        }
    }

    private fun addPlaybackControlsRow() {
        playbackControlsRow = PlaybackControlsRow(movie)
        superAdapter?.add(playbackControlsRow)
        val presenterSelector = ControlButtonPresenterSelector()
        val primaryActions = ArrayObjectAdapter(ControlButtonPresenterSelector())
        val secondaryActions = ArrayObjectAdapter(presenterSelector)
        playbackControlsRow!!.primaryActionsAdapter = primaryActions
        playbackControlsRow!!.secondaryActionsAdapter = secondaryActions
        movie.getTruePoster()?.let { playbackControlsRow!!.setDrawableFromUrl(requireContext(), it) }

        // Primary
        playPauseAction = PlayPauseAction(requireContext())
        fastForwardAction = FastForwardAction(requireContext())
        rewindAction = RewindAction(requireContext())
        skipNextAction = SkipNextAction(requireContext())
        skipPreviousAction = SkipPreviousAction(requireContext())
        primaryActions.add(skipPreviousAction)
        primaryActions.add(rewindAction)
        primaryActions.add(playPauseAction)
        primaryActions.add(fastForwardAction)
        primaryActions.add(skipNextAction)

        // Secondary
        val actions = listOf(
                Action(1, "", "", getWhiteDrawable(R.drawable.ic_pref_quality)),
                Action(2, "", "", getWhiteDrawable(R.drawable.ic_pref_language)),
                Action(3, "", "", getWhiteDrawable(R.drawable.ic_subtitles))
        )
        secondaryActions.addAll(0, actions)
    }

    private fun addEpisodes() {
        if (tvShowSeasons[currentSeason] != null && tvShowSeasons[currentSeason].isNotEmpty()) {
            val episodeRow = ArrayObjectAdapter(EpisodePresenter(requireContext()))
            tvShowSeasons[currentSeason].forEach {
                episodeRow.add(it)
            }
            superAdapter?.add(ListRow(HeaderItem(0, "სხვა სერიები სეზონში"), episodeRow))
        }
    }

    companion object {
        fun newInstance(parent: TvPlaybackFragment, playback: PlaybackModel): PlaybackControlFragment {
            val fragment = PlaybackControlFragment()
            fragment.movie = playback.movie
            fragment.tvShowSeasons.putAll(playback.tvShowSeasons)
            fragment.currentSeason = playback.currentSeason
            fragment.reference = WeakReference(parent)
            return fragment
        }
    }

}
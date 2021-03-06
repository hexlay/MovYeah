package hexlay.movyeah.app_tv.fragments

import android.net.Uri
import android.os.Bundle
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.util.putAll
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MergingMediaSource
import com.google.android.exoplayer2.source.SingleSampleMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.exoplayer2.util.Util
import hexlay.movyeah.api.database.view_models.DbEpisodeViewModel
import hexlay.movyeah.api.models.Movie
import hexlay.movyeah.api.models.attributes.Subtitle
import hexlay.movyeah.api.models.attributes.show.Episode
import hexlay.movyeah.api.models.attributes.show.EpisodeCache
import hexlay.movyeah.api.models.attributes.show.EpisodeFileData
import hexlay.movyeah.app_tv.R
import hexlay.movyeah.app_tv.fragments.playback.PlaybackControlFragment
import hexlay.movyeah.app_tv.fragments.watch.LanguageWatchFragment
import hexlay.movyeah.app_tv.fragments.watch.QualityWatchFragment
import hexlay.movyeah.app_tv.fragments.watch.SubtitleWatchFragment
import hexlay.movyeah.app_tv.models.PlaybackModel
import hexlay.movyeah.app_tv.models.events.StartFragmentEvent
import hexlay.movyeah.app_tv.models.events.watch.WatchEpisodeChangeEvent
import hexlay.movyeah.app_tv.models.events.watch.WatchLanguageChangeEvent
import hexlay.movyeah.app_tv.models.events.watch.WatchQualityChangeEvent
import hexlay.movyeah.app_tv.models.events.watch.WatchSubtitleChangeEvent
import kotlinx.android.synthetic.main.tv_fragment_playback.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.jetbrains.anko.support.v4.toast
import java.util.*

class TvPlaybackFragment : Fragment() {

    private val dbEpisodes by viewModels<DbEpisodeViewModel>()

    private var playbackControlFragment: PlaybackControlFragment? = null

    private var fileData: Map<String, List<EpisodeFileData>> = HashMap()
    private var subtitleData: Map<String, List<Subtitle>> = HashMap()
    private var tvShowSeasons: SparseArray<List<Episode>> = SparseArray()

    private lateinit var movie: Movie
    private var qualityKey = "NONE"
    private var languageKey = "NONE"
    private var subtitleKey = "NONE"
    private var currentSeason = 1
    private var currentEpisode = 1

    private var playerInitialLaunch = false
    private var stillInApp = false
    private var superExit = false

    var isPlaying = false
    private var canResume = false
    private var exoPlayer: ExoPlayer? = null
    private var isPreparing = true
    private var isSeeking = false
    private var onPause = false
    private var seekSaved: Long = 0
    private var seekSecForward: Long = 0
    private var seekSecBackward: Long = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        EventBus.getDefault().register(this)
        return inflater.inflate(R.layout.tv_fragment_playback, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (playbackControlFragment != null) {
            childFragmentManager.commit {
                replace(R.id.playback_controls_fragment, playbackControlFragment!!)
            }
        }
        initPlayer()
        decideSubtitles()
    }

    private fun initPlayer() {
        exoPlayer = ExoPlayerFactory.newSimpleInstance(context, DefaultTrackSelector())
        tv_player_src.useController = false
        tv_player_src.requestFocus()
        tv_player_src.player = exoPlayer
        exoPlayer!!.playWhenReady = true
        exoPlayer!!.addListener(object : Player.EventListener {

            override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {}

            override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {}

            override fun onLoadingChanged(isLoading: Boolean) {}

            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if (playbackState == Player.STATE_BUFFERING) {
                    tv_exo_loading.isVisible = true
                } else {
                    tv_exo_loading.isVisible = false
                    seekSecForward = 0
                    seekSecBackward = 0
                }
                if (isPreparing && playbackState == Player.STATE_READY) {
                    isPreparing = false
                }
                if (playWhenReady && playbackState == Player.STATE_READY) {
                    isPlaying = true
                    playerInitialLaunch = true
                    exoPlayer!!.playWhenReady = true
                    playbackControlFragment?.updateProgressBar(exoPlayer!!)
                    if (isSeeking) {
                        exoPlayer!!.seekTo(seekSaved)
                        isSeeking = false
                        seekSaved = 0
                    }
                    if (onPause)
                        pausePlayer()
                } else {
                    isPlaying = false
                }
            }

            override fun onRepeatModeChanged(repeatMode: Int) {}

            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {}

            override fun onPlayerError(error: ExoPlaybackException) {
                tv_exo_loading.isVisible = false
            }

            override fun onPositionDiscontinuity(reason: Int) {}

            override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {}

            override fun onSeekProcessed() {}
        })
    }

    private fun generatePlayerUrl(): String {
        return fileData[languageKey]?.first { it.quality == qualityKey }?.src.toString()
    }

    private fun generateSubtitleUrl(): String {
        return subtitleData[languageKey]?.first { it.lang == subtitleKey.toLowerCase(Locale.ENGLISH) }?.url.toString()
    }

    private fun setupSource() {
        if (exoPlayer != null) {
            val httpDataSourceFactory = DefaultHttpDataSourceFactory(
                    Util.getUserAgent(context, "Movyeah"), null,
                    DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
                    DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,
                    true
            )
            val dataSourceFactory = DefaultDataSourceFactory(context, null, httpDataSourceFactory)
            val mediaSource = ExtractorMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(Uri.parse(generatePlayerUrl()))
            exoPlayer!!.prepare(mediaSource)
        }
    }

    private fun setupSourceSubtitles() {
        if (exoPlayer != null) {
            val httpDataSourceFactory = DefaultHttpDataSourceFactory(
                    Util.getUserAgent(context, "Movyeah"), null,
                    DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
                    DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,
                    true
            )
            val dataSourceFactory = DefaultDataSourceFactory(context, null, httpDataSourceFactory)
            val textFormat = Format.createTextSampleFormat(null, MimeTypes.TEXT_VTT, null, Format.NO_VALUE, C.SELECTION_FLAG_DEFAULT, null, null, 0)
            val subtitleSource = SingleSampleMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(generateSubtitleUrl()), textFormat, C.TIME_UNSET)
            val mediaSource = ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(generatePlayerUrl()))
            exoPlayer!!.prepare(MergingMediaSource(mediaSource, subtitleSource))
        }
    }

    fun pausePlayer() {
        exoPlayer?.playWhenReady = false
        exoPlayer?.playbackState
    }

    fun startPlayer() {
        exoPlayer?.playWhenReady = true
        exoPlayer?.playbackState
    }

    private fun decideSubtitles() {
        if (subtitleData.isNotEmpty() && subtitleKey != "NONE")
            setupSourceSubtitles()
        else
            setupSource()
    }


    fun forward() {
        exoPlayer?.seekTo(exoPlayer!!.currentPosition + 10000)
    }

    fun forwardSuper() {
        exoPlayer?.seekTo(exoPlayer!!.currentPosition + 60000)
    }

    fun rewind() {
        exoPlayer?.seekTo(exoPlayer!!.currentPosition - 10000)
    }

    fun rewindSuper() {
        exoPlayer?.seekTo(exoPlayer!!.currentPosition - 60000)
    }

    fun startLanguageFragment() {
        EventBus.getDefault().post(StartFragmentEvent("full_language", LanguageWatchFragment.newInstance(languageKey, fileData.keys.toList())))
    }

    fun startQualityFragment() {
        val list = fileData[languageKey]?.map { quality -> quality.quality!! }!!
        EventBus.getDefault().post(StartFragmentEvent("full_quality", QualityWatchFragment.newInstance(qualityKey, list)))
    }

    fun startSubtitleFragment() {
        val list = subtitleData[languageKey] ?: listOf()
        if (list.isNotEmpty()) {
            EventBus.getDefault().post(StartFragmentEvent("full_quality", SubtitleWatchFragment.newInstance(subtitleKey, list)))
        } else {
            toast("Subtitles not found")
        }
    }

    private fun restartPlayer() {
        if (exoPlayer != null) {
            pausePlayer()
            exoPlayer!!.stop()
            exoPlayer!!.seekTo(0)
            decideSubtitles()
            startPlayer()
            isPreparing = true
        }
    }

    private fun releaseVideo() {
        if (exoPlayer != null) {
            pausePlayer()
            exoPlayer!!.stop()
            exoPlayer!!.release()
            exoPlayer = null
        }
    }

    override fun onResume() {
        super.onResume()
        stillInApp = false
        onPause = false
        if (!superExit && canResume && playerInitialLaunch) {
            if (!isPlaying)
                startPlayer()
            canResume = false
        }
    }

    override fun onPause() {
        if (!superExit) {
            onPause = true
            canResume = true
            if (isPlaying) {
                pausePlayer()
            } else if (isPlaying) {
                pausePlayer()
            }
        }
        super.onPause()
    }

    @Subscribe
    fun listenLanguageChange(event: WatchLanguageChangeEvent) {
        languageKey = event.language
        restartPlayer()
    }

    @Subscribe
    fun listenQualityChange(event: WatchQualityChangeEvent) {
        qualityKey = event.quality
        restartPlayer()
    }

    @Subscribe
    fun listenSubtitleChange(event: WatchSubtitleChangeEvent) {
        subtitleKey = event.subtitle
        restartPlayer()
    }

    @Subscribe
    fun listenEpisodeChange(event: WatchEpisodeChangeEvent) {
        currentSeason = event.season
        currentEpisode = event.episode
        dbEpisodes.insertEpisode(EpisodeCache(movie.id, currentEpisode, currentSeason))
        fileData = tvShowSeasons[currentSeason][currentEpisode].files.map { it.lang!! to it.files }.toMap()
        subtitleData = tvShowSeasons[currentSeason][currentEpisode].files.map { it.lang!! to it.subtitles }.toMap()
        decideSubtitles()
    }

    override fun onDestroyView() {
        releaseVideo()
        EventBus.getDefault().unregister(this)
        super.onDestroyView()
    }

    companion object {
        fun newInstance(playback: PlaybackModel): TvPlaybackFragment {
            val fragment = TvPlaybackFragment()
            fragment.movie = playback.movie
            fragment.fileData = playback.fileData
            fragment.subtitleData = playback.subtitleData
            fragment.tvShowSeasons.putAll(playback.tvShowSeasons)
            fragment.qualityKey = playback.qualityKey
            fragment.languageKey = playback.languageKey
            fragment.subtitleKey = playback.subtitleKey
            fragment.currentSeason = playback.currentSeason
            fragment.currentEpisode = playback.currentEpisode
            fragment.playbackControlFragment = PlaybackControlFragment.newInstance(fragment, playback)
            return fragment
        }
    }

}
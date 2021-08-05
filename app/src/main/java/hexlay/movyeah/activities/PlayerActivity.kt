package hexlay.movyeah.activities

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isGone
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.util.MimeTypes
import hexlay.movyeah.R
import hexlay.movyeah.helpers.*
import hexlay.movyeah.models.PlayerData
import kotlinx.android.synthetic.main.activity_player.*
import java.util.*
import kotlin.collections.ArrayList


class PlayerActivity : AppCompatActivity() {

    private lateinit var playerData: PlayerData
    private var exoPlayer: ExoPlayer? = null

    private var qualityKey = "NONE"
    private var languageKey = "NONE"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        hideSystemUI()
        initStarterData()
        initMovieInformation()
        initActivityProperties()
        initLayoutActions()
        initExoPlayer()
        setupSource()
    }

    private fun initStarterData() {
        playerData = intent!!.extras!!.getParcelable("player_data")!!
    }

    @SuppressLint("CheckResult")
    private fun initLayoutActions() {
        val qualities = playerData.fileData.getValue(languageKey).map { it.quality!! }
        val languages = playerData.fileData.keys.toList()
        button_back.setOnClickListener {
            finish()
        }
        button_quality.setOnClickListener {
            MaterialDialog(this).show {
                title(R.string.full_chquality)
                listItemsSingleChoice(items = qualities, initialSelection = qualities.indexOf(qualityKey)) { _, _, text ->
                    qualityKey = text.toString()
                    setupSource()
                }
            }
        }
        button_quality.setOnLongClickListener {
            val text = getString(R.string.full_show_info).format(qualityKey.translateQuality(this))
            showAlert(text = text)
            true
        }
        button_lang.setOnClickListener {
            MaterialDialog(this).show {
                title(R.string.full_chlang)
                listItemsSingleChoice(items = languages, initialSelection = languages.indexOf(languageKey)) { _, _, text ->
                    languageKey = text.toString()
                    setupSource()
                }
            }
        }
        button_lang.setOnLongClickListener {
            val text = getString(R.string.full_show_info).format(languageKey.translateLanguage(this))
            showAlert(text = text)
            true
        }
        button_share.setOnClickListener {
            playeExternally(generatePlayerUrl())
        }
    }

    private fun initMovieInformation() {
        if (playerData.fileData.isNotEmpty()) {
            setupLanguage()
            setupQuality()
        } else {
            button_lang.isGone = true
            button_quality.isGone = true
        }
    }

    private fun setupLanguage() {
        languageKey = when {
            playerData.fileData.containsKey(PreferenceHelper.lang) -> {
                PreferenceHelper.lang
            }
            playerData.fileData.containsKey("GEO") -> {
                "GEO"
            }
            playerData.fileData.containsKey("ENG") -> {
                "ENG"
            }
            else -> {
                playerData.fileData.keys.first()
            }
        }
        button_lang.isEnabled = true
    }

    private fun setupQuality() {
        val qualities = playerData.fileData.getValue(languageKey).map { it.quality }
        qualityKey = when {
            qualities.contains(PreferenceHelper.quality) -> {
                PreferenceHelper.quality
            }
            qualities.contains("HIGH") -> {
                "HIGH"
            }
            else -> {
                playerData.fileData.getValue(languageKey).first().toString()
            }
        }
        button_quality.isEnabled = true
    }

    private fun initActivityProperties() {
        if (PreferenceHelper.maxBrightness) {
            val layoutParams = window.attributes
            layoutParams?.screenBrightness = 100 / 100.0f
            window.attributes = layoutParams
        }
    }

    private fun initExoPlayer() {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.CONTENT_TYPE_MOVIE)
            .build()
        exoPlayer = SimpleExoPlayer.Builder(this)
            .setAudioAttributes(audioAttributes, true)
            .build()
        player_src.player = exoPlayer
        player_src.controllerShowTimeoutMs = 2000
        player_src.useController = true
        player_src.requestFocus()
        player_src.setShowNextButton(false)
        player_src.setShowPreviousButton(false)
        player_src.setShowBuffering(StyledPlayerView.SHOW_BUFFERING_WHEN_PLAYING)
        player_src.setControllerVisibilityListener { visibility ->
            toolbar.fade(if (visibility == View.VISIBLE) 1 else 0, 100)
        }
    }

    private fun setupSource() {
        if (exoPlayer != null) {
            val mediaItemBuilder = MediaItem.Builder().setUri(Uri.parse(generatePlayerUrl()))

            if (playerData.subtitleData.isNotEmpty()) {
                val subtitleList = ArrayList<MediaItem.Subtitle>()
                val list = playerData.subtitleData[languageKey]?.map { it.lang?.uppercase(Locale.ENGLISH)!! }
                if (list != null) {
                    for (item in list) {
                        val url = generateSubtitleUrl(item)
                        subtitleList.add(MediaItem.Subtitle(Uri.parse(url), MimeTypes.TEXT_VTT, item, Format.NO_VALUE))
                    }
                }
                mediaItemBuilder.setSubtitles(subtitleList)
            }

            exoPlayer!!.setMediaItem(mediaItemBuilder.build())
            exoPlayer!!.prepare()
            exoPlayer!!.seekTo(PreferenceHelper.getWatchProgress(getSaveKey()))
            exoPlayer!!.play()
        }
    }

    private fun generatePlayerUrl(): String {
        if (playerData.offlineIdentifier != null) {
            return getOfflineMovie(playerData.offlineIdentifier!!).absolutePath
        }
        return playerData.fileData.getValue(languageKey).first { it.quality == qualityKey }.src.toString()
    }

    private fun generateSubtitleUrl(subtitleKey: String): String {
        return playerData.subtitleData.getValue(languageKey).first { it.lang == subtitleKey.lowercase(Locale.ENGLISH) }.url.toString()
    }

    private fun releaseVideo() {
        if (exoPlayer != null) {
            exoPlayer!!.pause()
            exoPlayer!!.stop()
            exoPlayer!!.release()
            exoPlayer = null
        }
    }

    private fun releaseActivityProperties() {
        if (PreferenceHelper.maxBrightness) {
            val layoutParams = window.attributes
            layoutParams?.screenBrightness = -1f
            window.attributes = layoutParams
        }
    }

    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, player_frame).let { controller ->
            controller.hide(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun getSaveKey(): String {
        return playerData.movieId + playerData.episode
    }

    override fun onResume() {
        super.onResume()
        exoPlayer?.play()
    }

    override fun onPause() {
        exoPlayer?.pause()
        exoPlayer?.currentPosition?.let { PreferenceHelper.saveWatchProgress(getSaveKey(), it) }
        super.onPause()
    }

    override fun onDestroy() {
        exoPlayer?.currentPosition?.let { PreferenceHelper.saveWatchProgress(getSaveKey(), it) }
        releaseActivityProperties()
        releaseVideo()
        super.onDestroy()
    }

}
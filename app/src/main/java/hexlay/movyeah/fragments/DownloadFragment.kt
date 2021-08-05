package hexlay.movyeah.fragments

import android.app.DownloadManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.recyclical.datasource.dataSourceOf
import com.afollestad.recyclical.datasource.emptyDataSource
import com.afollestad.recyclical.setup
import com.afollestad.recyclical.withItem
import hexlay.movyeah.R
import hexlay.movyeah.activities.DetailActivity
import hexlay.movyeah.activities.PlayerActivity
import hexlay.movyeah.adapters.view_holders.DownloadGroupViewHolder
import hexlay.movyeah.adapters.view_holders.DownloadMovieViewHolder
import hexlay.movyeah.api.database.view_models.DbDownloadMovieViewModel
import hexlay.movyeah.api.models.DownloadMovie
import hexlay.movyeah.helpers.*
import hexlay.movyeah.models.PlayerData
import kotlinx.android.synthetic.main.fragment_movies.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.anko.support.v4.intentFor
import org.jetbrains.anko.support.v4.runOnUiThread
import org.jetbrains.anko.support.v4.startActivity
import java.lang.ref.WeakReference


class DownloadFragment : Fragment() {

    private val dbDownloadMovies by viewModels<DbDownloadMovieViewModel>()
    private var downloadManager: DownloadManager? = null
    private val source = emptyDataSource()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        downloadManager = requireContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        return inflater.inflate(R.layout.fragment_movies, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun initView() {
        val recyclerPaddingTop = getStatusBarHeight() + getActionBarSize() + dpOf(10)
        movies_holder.setPadding(0, recyclerPaddingTop, 0, getActionBarSize())
    }

    private fun init() {
        initView()
        initReloader()
        loadMovies()
    }

    private fun initReloader() {
        movies_reloader.isEnabled = false
    }

    private fun loadMovies() {
        dbDownloadMovies.getMovies()?.observe(viewLifecycleOwner, {
            source.clear()
            if (it.isNotEmpty()) {
                it.groupBy { g -> g.movie!!.id }.forEach { (_, value) ->
                    source.add(value)
                }
                warning_holder.isVisible = false
            } else {
                warning_holder.text = getString(R.string.loading_dows_fail)
                warning_holder.isVisible = true
            }
        })
        movies_holder.setup {
            withDataSource(source)
            withItem<ArrayList<DownloadMovie>, DownloadGroupViewHolder>(R.layout.list_items_download_group) {
                onBind(::DownloadGroupViewHolder) { _, groupedItems ->
                    val firstMovie = groupedItems.first().movie
                    groupTitle.text = firstMovie?.getTitle()
                    firstMovie?.getTruePoster()?.let { groupImage.setUrl(it) }
                    setupChildRecyclerView(childrenHolder, groupedItems)
                    var title = ""
                    groupedItems.forEach {
                        val movie = it.movie
                        if (movie != null) {
                            val translatedLanguage = it.language?.translateLanguage(requireContext())
                            val translatedQuality = it.quality?.translateQuality(requireContext())
                            title += if (movie.isTvShow) {
                                "S${it.currentSeason}E${it.currentEpisode + 1} (${translatedLanguage}, ${translatedQuality})"
                            } else {
                                "${translatedLanguage}, $translatedQuality"
                            }
                            title += "<br>"
                        }
                    }
                    groupContent.text = title.toHtml()
                    itemView.setOnClickListener {
                        children.toggle()
                    }
                }
            }
        }
    }

    private fun setupChildRecyclerView(view: RecyclerView, groupedItems: ArrayList<DownloadMovie>) {
        view.setup {
            withDataSource(dataSourceOf(groupedItems))
            withItem<DownloadMovie, DownloadMovieViewHolder>(R.layout.list_items_download) {
                onBind(::DownloadMovieViewHolder) { _, item ->
                    val movie = item.movie
                    val downloadProgress = DownloadProgress(item.downloadId, progress)
                    if (movie != null) {
                        title.isSelected = true
                        val translatedLanguage = item.language?.translateLanguage(requireContext())
                        val translatedQuality = item.quality?.translateQuality(requireContext())
                        val titleTrue = if (movie.isTvShow) {
                            "S${item.currentSeason}E${item.currentEpisode + 1} (${translatedLanguage}, ${translatedQuality})"
                        } else {
                            "${translatedLanguage}, $translatedQuality"
                        }
                        title.text = titleTrue
                        lifecycleScope.launch {
                            delay(1000)
                            if (downloadExists(item.identifier)) {
                                download.isVisible = false
                                downloadProgress.start()
                            } else {
                                download.isVisible = true
                                progress.progress = 0
                            }
                        }
                        remove.setOnClickListener {
                            MaterialDialog(requireContext()).show {
                                message(R.string.offline_remove_confirm)
                                positiveButton(R.string.yes) {
                                    downloadProgress.interrupt()
                                    downloadManager?.remove(item.downloadId)
                                    getOfflineMovie(item.identifier).delete()
                                    dbDownloadMovies.deleteMovie(item)
                                }
                                negativeButton(R.string.no) {
                                    dismiss()
                                }
                            }
                        }
                        download.setOnClickListener {
                            downloadManager?.remove(item.downloadId)
                            if (downloadExists(item.identifier)) {
                                getOfflineMovie(item.identifier).delete()
                            }
                            item.downloadId = downloadMovie(item.url!!, item.identifier)
                            downloadProgress.setDownloadId(item.downloadId)
                            downloadProgress.start()
                            download.isVisible = false
                        }
                        itemView.setOnClickListener {
                            startActivity<PlayerActivity>(
                                "player_data" to PlayerData(
                                    movieId = item.movie?.getRealId().toString(),
                                    movieName = item.movie?.getTitle()!!,
                                    offlineIdentifier = item.identifier
                                )
                            )
                        }
                        itemView.setOnLongClickListener {
                            startActivity(intentFor<DetailActivity>("movie" to item.movie))
                            true
                        }
                    }
                }
            }
        }
    }

    inner class DownloadProgress(downloadId: Long, progressBar: ProgressBar) : Thread() {

        private val weakProgressBar = WeakReference(progressBar)
        private val query = DownloadManager.Query()

        init {
            setDownloadId(downloadId)
        }

        fun setDownloadId(downloadId: Long) {
            query.setFilterById(downloadId)
        }

        override fun run() {
            while (true) {
                try {
                    sleep(300)
                    val cursor = downloadManager?.query(query)
                    if (cursor != null) {
                        if (cursor.moveToFirst()) {
                            val bytesDownloaded = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                            val bytesTotal = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                            when (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                                DownloadManager.STATUS_SUCCESSFUL, DownloadManager.STATUS_FAILED -> {
                                    interrupt()
                                }
                            }
                            if (bytesTotal > 0) {
                                val progress = (bytesDownloaded * 100L / bytesTotal).toInt()
                                runOnUiThread {
                                    weakProgressBar.get()?.progress = progress
                                }
                            }
                        }
                        cursor.close()
                    }
                } catch (e: Exception) {
                    interrupt()
                    return
                }
            }
        }

    }
}

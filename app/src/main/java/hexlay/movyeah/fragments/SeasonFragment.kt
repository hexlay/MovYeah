package hexlay.movyeah.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.afollestad.assent.Permission
import com.afollestad.assent.runWithPermissions
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItems
import com.afollestad.recyclical.datasource.emptyDataSource
import com.afollestad.recyclical.setup
import com.afollestad.recyclical.withItem
import hexlay.movyeah.R
import hexlay.movyeah.adapters.view_holders.EpisodeViewHolder
import hexlay.movyeah.api.database.view_models.DbDownloadMovieViewModel
import hexlay.movyeah.api.database.view_models.DbEpisodeViewModel
import hexlay.movyeah.api.models.DownloadMovie
import hexlay.movyeah.api.models.Movie
import hexlay.movyeah.api.models.attributes.show.Episode
import hexlay.movyeah.helpers.*
import hexlay.movyeah.models.events.ChooseEpisodeEvent
import kotlinx.android.synthetic.main.fragment_season.*
import org.greenrobot.eventbus.EventBus


class SeasonFragment : Fragment() {

    private var movie: Movie? = null
    private var season = 0
    private var episodes = emptyDataSource()

    private val dbEpisodes by viewModels<DbEpisodeViewModel>()
    private val dbDownloadMovie by viewModels<DbDownloadMovieViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_season, container, false)
    }

    @SuppressLint("SetTextI18n", "CheckResult")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dbEpisodes.getEpisode(movie!!.id)?.observeOnce(viewLifecycleOwner, {
            if (it != null) {
                episode_holder.scrollToPosition(it.episode)
            } else {
                episode_holder.scrollToPosition(0)
            }
        })
        episode_holder.setup {
            withDataSource(episodes)
            withItem<Episode, EpisodeViewHolder>(R.layout.list_episodes) {
                onBind(::EpisodeViewHolder) { index, item ->
                    title.text = "${item.episode}. ${item.getEpisodeTitle()}"
                    languages.text = item.files.map { it.lang?.translateLanguage(requireContext()) }.toCommaList()
                    dbEpisodes.getEpisode(movie!!.id)?.observe(viewLifecycleOwner, {
                        if (it != null && it.episode == index && it.season == season) {
                            paintSelected()
                        } else {
                            paintDeSelected()
                        }
                        itemView.setOnClickListener {
                            EventBus.getDefault().post(ChooseEpisodeEvent(index, season))
                        }
                    })
                    download.setOnClickListener {
                        val langs = item.files.map { it.lang!! to it.files }.toMap()
                        val dialogItems = ArrayList<String>()
                        val dataDownload = ArrayList<DownloadMovie>()
                        for ((langKey, langValue) in langs) {
                            val qualities = langValue.map { it.quality to it }.toMap()
                            for ((qualKey, qualValue) in qualities) {
                                val downloadTitle = "${movie!!.id}_${qualKey}_${langKey}_${season}_${index}"
                                if (!downloadExists(downloadTitle)) {
                                    val download = DownloadMovie(
                                            currentEpisode = index,
                                            currentSeason = season,
                                            language = langKey,
                                            quality = qualKey,
                                            movie = movie,
                                            url = qualValue.src,
                                            identifier = downloadTitle
                                    )
                                    dataDownload.add(download)
                                    val rQuality = qualKey?.translateQuality(requireContext())
                                    val rLang = langKey.translateLanguage(requireContext())
                                    dialogItems.add("$rQuality (${rLang})")
                                }
                            }
                        }
                        if (dialogItems.isNotEmpty()) {
                            MaterialDialog(requireContext()).show {
                                title(text = title.text.toString())
                                listItems(items = dialogItems) { _, index, _ ->
                                    runWithPermissions(Permission.WRITE_EXTERNAL_STORAGE) {
                                        val download = dataDownload[index]
                                        val downloadId = downloadFile(download.url!!, download.identifier)
                                        download.downloadId = downloadId
                                        dbDownloadMovie.insertMovie(download)
                                        showAlert(text = getString(R.string.download_start))
                                    }
                                }
                            }
                        } else {
                            showAlert(text = getString(R.string.download_done_all))
                        }
                    }
                }
            }
        }
    }

    companion object {

        fun newInstance(season: Int, movie: Movie, episodes: List<Episode>): SeasonFragment {
            val seasonFragment = SeasonFragment()
            seasonFragment.episodes.addAll(episodes)
            seasonFragment.movie = movie
            seasonFragment.season = season
            return seasonFragment
        }

    }

}

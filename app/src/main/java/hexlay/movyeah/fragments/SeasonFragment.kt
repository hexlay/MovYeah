package hexlay.movyeah.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.afollestad.assent.Permission
import com.afollestad.assent.runWithPermissions
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItems
import com.afollestad.recyclical.datasource.emptyDataSource
import com.afollestad.recyclical.setup
import com.afollestad.recyclical.withItem
import hexlay.movyeah.R
import hexlay.movyeah.adapters.view_holders.EpisodeViewHolder
import hexlay.movyeah.database.view_models.DbDownloadMovieViewModel
import hexlay.movyeah.database.view_models.DbEpisodeViewModel
import hexlay.movyeah.helpers.*
import hexlay.movyeah.models.events.ChooseEpisodeEvent
import hexlay.movyeah.models.movie.DownloadMovie
import hexlay.movyeah.models.movie.Movie
import hexlay.movyeah.models.movie.attributes.show.Episode
import kotlinx.android.synthetic.main.fragment_season.*
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.support.v4.toast

class SeasonFragment : Fragment() {

    private var movie: Movie? = null
    private var season = 0
    private var episodes = emptyDataSource()

    private val dbEpisodes by viewModels<DbEpisodeViewModel>()
    private val dbDownloadMovie by viewModels<DbDownloadMovieViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_season, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dbEpisodes.getEpisode(movie!!.id)?.observeOnce(viewLifecycleOwner, Observer {
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
                    dbEpisodes.getEpisode(movie!!.id)?.observe(viewLifecycleOwner, Observer {
                        if (it != null && it.episode == index && it.season == season) {
                            paintSelected()
                            itemView.setOnClickListener(null)
                        } else {
                            paintDeSelected()
                            itemView.setOnClickListener {
                                EventBus.getDefault().post(ChooseEpisodeEvent(index))
                            }
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
                                        val downloadId = downloadMovie(download.url!!, download.identifier)
                                        download.downloadId = downloadId
                                        dbDownloadMovie.insertMovie(download)
                                        toast(R.string.download_start)
                                    }
                                }
                            }
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

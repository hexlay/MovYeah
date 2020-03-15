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
import hexlay.movyeah.database.view_models.DbEpisodeViewModel
import hexlay.movyeah.helpers.*
import hexlay.movyeah.models.events.ChooseEpisodeEvent
import hexlay.movyeah.models.movie.attributes.show.Episode
import hexlay.movyeah.models.movie.attributes.show.EpisodeFileData
import kotlinx.android.synthetic.main.fragment_season.*
import org.greenrobot.eventbus.EventBus

class SeasonFragment : Fragment() {

    private var movieId : Int = 0
    private var season : Int = 0
    private var episodes = emptyDataSource()

    private val dbEpisodes by viewModels<DbEpisodeViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_season, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dbEpisodes.getEpisode(movieId)?.observeOnce(viewLifecycleOwner, Observer {
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
                    dbEpisodes.getEpisode(movieId)?.observe(viewLifecycleOwner, Observer {
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
                    itemView.setOnClickListener {
                        EventBus.getDefault().post(ChooseEpisodeEvent(index))
                    }
                    download.setOnClickListener {
                        val langs = item.files.map { it.lang!! to it.files }.toMap()
                        val dialogItems = ArrayList<String>()
                        val dataDownload = ArrayList<EpisodeFileData>()
                        for ((langKey, langValue) in langs) {
                            val qualities = langValue.map { it.quality to it }.toMap()
                            for ((qualKey, qualValue) in qualities) {
                                dataDownload.add(qualValue)
                                dialogItems.add("${qualKey?.translateQuality(requireContext())} (${langKey.translateLanguage(requireContext())})")
                            }
                        }
                        MaterialDialog(requireContext()).show {
                            title(text = title.text.toString())
                            listItems(items = dialogItems) { _, index, _ ->
                                runWithPermissions(Permission.WRITE_EXTERNAL_STORAGE) {
                                    val languages = dialogItems[index].replace(" (", ", ").replace(")", "")
                                    val title = "${title.text} (${languages})"
                                    dataDownload[index].src?.let { url -> downloadMovie(url, title) }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    companion object {

        fun newInstance(season: Int, movieId: Int, episodes: List<Episode>): SeasonFragment {
            val seasonFragment = SeasonFragment()
            seasonFragment.episodes.addAll(episodes)
            seasonFragment.movieId = movieId
            seasonFragment.season = season
            return seasonFragment
        }

    }

}

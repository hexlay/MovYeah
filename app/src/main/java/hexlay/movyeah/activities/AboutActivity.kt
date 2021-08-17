package hexlay.movyeah.activities

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.recyclical.datasource.dataSourceOf
import com.afollestad.recyclical.setup
import com.afollestad.recyclical.withItem
import hexlay.movyeah.R
import hexlay.movyeah.adapters.view_holders.LibraryViewHolder
import hexlay.movyeah.api.github.view_models.GithubViewModel
import hexlay.movyeah.helpers.initDarkMode
import hexlay.movyeah.helpers.observeOnce
import hexlay.movyeah.helpers.showUpdateDialog
import hexlay.movyeah.models.Library
import kotlinx.android.synthetic.main.activity_about.*
import org.jetbrains.anko.browse

class AboutActivity : AppCompatActivity() {

    private val apiGithub by viewModels<GithubViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        initDarkMode()
        initToolbar()
        initResources()
        initVersion()
    }

    private fun initToolbar() {
        toolbar.setNavigationOnClickListener {
            finish()
        }
        version_holder.setOnClickListener {
            browse("https://github.com/hexlay/MovYeah", true)
        }
        author_holder.setOnClickListener {
            browse("https://github.com/hexlay", true)
        }
        update_holder.setOnClickListener {
            apiGithub.fetchReleases().observeOnce(this, {
                val latest = it.first()
                showUpdateDialog(latest)
            })
        }
    }

    private fun initVersion() {
        val gradleVersionName = packageManager.getPackageInfo(packageName, 0).versionName
        version.text = gradleVersionName
    }

    private fun initResources() {
        val libraries = dataSourceOf(
                Library("Adjaranet",
                        "Adjaranet's team",
                        "http://net.adjara.com/"),
                Library("Retrofit",
                        "Square",
                        "https://github.com/square/retrofit"),
                Library("OkHttp",
                        "Square",
                        "https://github.com/square/okhttp"),
                Library("Glide",
                        "Bumptech",
                        "https://github.com/bumptech/glide"),
                Library("Material Dialogs",
                        "Aidan Follestad",
                        "https://github.com/afollestad/material-dialogs"),
                Library("Recyclical",
                        "Aidan Follestad",
                        "https://github.com/afollestad/recyclical"),
                Library("Assent",
                        "Aidan Follestad",
                        "https://github.com/afollestad/assent"),
                Library("ExoPlayer 2",
                        "Google",
                        "https://github.com/google/ExoPlayer"),
                Library("Android Room",
                        "Google",
                        "https://developer.android.com/topic/libraries/architecture/room"),
                Library("Leak Canary",
                        "Square",
                        "https://github.com/square/leakcanary"),
                Library("Expandable Layout",
                        "Daniel Cachapa",
                        "https://github.com/cachapa/ExpandableLayout"),
                Library("Alerter",
                        "Tapadoo",
                        "https://github.com/Tapadoo/Alerter"),
                Library("MorphView",
                        "Mikel (akaita)",
                        "https://github.com/akaita/MorphView"),
                Library("EventBus",
                        "Markus Junginger",
                        "https://github.com/greenrobot/EventBus")
        )
        resource_holder.setup {
            withDataSource(libraries)
            withItem<Library, LibraryViewHolder>(R.layout.list_about) {
                onBind(::LibraryViewHolder) { _, item ->
                    resource.text = item.libraryName
                    summary.text = item.libraryAuthor
                }
                onClick {
                    browse(item.libraryLink, true)
                }
            }
        }
    }

}

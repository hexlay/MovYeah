package hexlay.movyeah.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import hexlay.movyeah.api.network.view_models.WatchViewModel
import hexlay.movyeah.helpers.Constants
import hexlay.movyeah.helpers.observeOnce
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.noAnimation

class StarterActivity : AppCompatActivity() {

    private val watchViewModel by viewModels<WatchViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startMainActivity()
    }

    private fun startMainActivity() {
        if (intent.action == Constants.SHORTCUT_ACTION) {
            if (intent.extras != null && !intent.extras!!.isEmpty) {
                val id = intent.getIntExtra("movie_id", 0)
                watchViewModel.fetchSingleMovie(id).observeOnce(this, {
                    val intent = intentFor<MainActivity>("movie" to it).noAnimation()
                    startActivity(intent)
                    finish()
                })
            }
        } else {
            startActivity(setupNormalIntent())
            finish()
        }
    }

    private fun setupNormalIntent(): Intent {
        val intent = intentFor<MainActivity>().noAnimation()
        if (intent.extras != null && !intent.extras!!.isEmpty) {
            intent.putExtras(intent.extras!!)
        }
        return intent
    }

}

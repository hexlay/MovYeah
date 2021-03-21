package hexlay.movyeah.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jude.swipbackhelper.SwipeBackHelper
import hexlay.movyeah.R
import hexlay.movyeah.fragments.SettingsFragment
import hexlay.movyeah.helpers.initDarkMode
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState ?: intent.getBundleExtra("saved_state"))
        setContentView(R.layout.activity_settings)
        SwipeBackHelper.onCreate(this)
        initDarkMode()
        if (savedInstanceState == null) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.settings, SettingsFragment())
                    .commit()
        }
        toolbar.setNavigationOnClickListener { finish() }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        SwipeBackHelper.onPostCreate(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        SwipeBackHelper.onDestroy(this)
    }

    fun transitionRecreate() {
        val bundle = Bundle()
        onSaveInstanceState(bundle)
        val intent = Intent(this, javaClass)
        intent.putExtra("saved_state", bundle)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}
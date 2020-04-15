package movyeahtv.activities

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commit
import hexlay.movyeah.R
import movyeahtv.fragments.TvMainFragment
import movyeahtv.models.events.StartPreferenceEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


class TvMainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tv_activity_main)
        supportFragmentManager.commit {
            replace(android.R.id.content, TvMainFragment(), "main")
        }
    }

    @Subscribe
    fun listenPreferenceAdd(event: StartPreferenceEvent) {
        removeFragment(event.key)
        addFragment(event.key, event.fragment)
    }

    private fun addFragment(key: String, fragment: Fragment) {
        supportFragmentManager.commit {
            addToBackStack("main")
            add(android.R.id.content, fragment, key)
        }
    }

    private fun removeFragment(key: String) {
        val fragment = supportFragmentManager.findFragmentByTag(key)
        if (fragment != null) {
            supportFragmentManager.commit {
                remove(fragment)
            }
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }

    public override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    public override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

}
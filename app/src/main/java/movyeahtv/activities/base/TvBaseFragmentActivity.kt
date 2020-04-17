package movyeahtv.activities.base

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commit
import hexlay.movyeah.R
import movyeahtv.models.events.StartFragmentEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

abstract class TvBaseFragmentActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tv_activity)
        initMainFragment()
    }

    protected abstract fun initMainFragment()

    @Subscribe
    fun listenFragmentAdd(event: StartFragmentEvent) {
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
package movyeahtv.activities

import androidx.fragment.app.commit
import movyeahtv.activities.base.TvBaseFragmentActivity
import movyeahtv.fragments.TvMainFragment


class TvMainActivity : TvBaseFragmentActivity() {

    override fun initMainFragment() {
        supportFragmentManager.commit {
            replace(android.R.id.content, TvMainFragment(), "main")
        }
    }

}
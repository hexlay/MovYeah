package movyeahtv.activities

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commit
import hexlay.movyeah.R
import movyeahtv.fragments.TvSearchFragment

class TvSearchActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tv_activity)
        initMainFragment()
    }

    private fun initMainFragment() {
        supportFragmentManager.commit {
            replace(android.R.id.content, TvSearchFragment(), "search")
        }
    }

}
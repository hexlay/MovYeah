package hexlay.movyeah.app_tv.activities

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commit
import hexlay.movyeah.app_tv.R
import hexlay.movyeah.app_tv.fragments.TvSearchFragment

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
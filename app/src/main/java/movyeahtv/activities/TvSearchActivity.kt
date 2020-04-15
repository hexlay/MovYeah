package movyeahtv.activities

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commit
import hexlay.movyeah.R
import movyeahtv.fragments.SearchFragment


class TvSearchActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tv_activity_main)
        supportFragmentManager.commit {
            replace(android.R.id.content, SearchFragment(), "search")
        }
    }

}
package hexlay.movyeah.app_tv.activities

import androidx.activity.viewModels
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import hexlay.movyeah.api.database.view_models.DbCategoryViewModel
import hexlay.movyeah.api.network.view_models.FilterAttrsViewModel
import hexlay.movyeah.app_tv.activities.base.TvBaseFragmentActivity
import hexlay.movyeah.app_tv.fragments.TvMainFragment
import hexlay.movyeah.app_tv.helpers.observeOnce


class TvMainActivity : TvBaseFragmentActivity() {

    private val apiCategories by viewModels<FilterAttrsViewModel>()
    private val dbCategories by viewModels<DbCategoryViewModel>()

    override fun initMainFragment() {
        initCategories()
        supportFragmentManager.commit {
            replace(android.R.id.content, TvMainFragment(), "main")
        }
    }

    private fun initCategories() {
        apiCategories.fetchCategories().observeOnce(this, Observer {
            for (category in it) {
                dbCategories.insertCategory(category)
            }
        })
    }

}
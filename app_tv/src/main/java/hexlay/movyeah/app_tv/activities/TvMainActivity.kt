package hexlay.movyeah.app_tv.activities

import androidx.activity.viewModels
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import hexlay.movyeah.api.database.view_models.DbCategoryViewModel
import hexlay.movyeah.api.models.attributes.Category
import hexlay.movyeah.app_tv.activities.base.TvBaseFragmentActivity
import hexlay.movyeah.app_tv.fragments.TvMainFragment
import hexlay.movyeah.app_tv.helpers.observeOnce


class TvMainActivity : TvBaseFragmentActivity() {

    private val dbCategories by viewModels<DbCategoryViewModel>()

    override fun initMainFragment() {
        initCategories()
        supportFragmentManager.commit {
            replace(android.R.id.content, TvMainFragment(), "main")
        }
    }

    private fun initCategories() {
        val categoryIds = listOf(265, 253, 259, 252, 249, 269, 267, 264, 258, 260, 268, 256, 273, 262, 248, 266, 257, 251, 263, 255, 254, 275, 250, 317, 316, 312, 261)
        dbCategories.getCategories()?.observeOnce(Observer {
            if (it.size != categoryIds.size) {
                dbCategories.clearCategories()
                for (categoryId in categoryIds) {
                    val identifier = resources.getIdentifier("cat_${categoryId}", "string", packageName)
                    dbCategories.insertCategory(Category(categoryId, getString(identifier)))
                }
            }
        })
    }

}
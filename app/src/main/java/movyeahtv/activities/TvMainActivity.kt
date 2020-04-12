package movyeahtv.activities

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commit
import hexlay.movyeah.R
import movyeahtv.fragments.TvMainFragment


class TvMainActivity : FragmentActivity() {

    private var isInPreference = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tv_activity_main)
        supportFragmentManager.commit {
            replace(android.R.id.content, TvMainFragment(), "main")
        }
    }

    fun addFragmentPreference(type: Int, title: String, description: String) {
        removeAllFragments()
        isInPreference = true
        /*when (type) {
            1 -> {
                val languagePrefFragment = TvLanguagePrefFragment.newInstance(title, description)
                supportFragmentManager.commit {
                    add(android.R.id.content, languagePrefFragment, "preference_language")
                }
            }
            2 -> {
                val categoryPrefFragment = TvCategoryPrefFragment.newInstance(title, description)
                supportFragmentManager.commit {
                    add(android.R.id.content, categoryPrefFragment, "preference_category")
                }
            }
            3 -> {
                val yearPrefFragment = TvYearPrefFragment.newInstance(title, description)
                supportFragmentManager.commit {
                    add(android.R.id.content, yearPrefFragment, "preference_year")
                }
            }
            4 -> {
                val sortPrefFragment = TvYearPrefFragment.newInstance(title, description)
                supportFragmentManager.commit {
                    add(android.R.id.content, sortPrefFragment, "preference_sort")
                }
            }
        }*/
    }

    fun removeFragment(id: String?) {
        isInPreference = false
        val fragment = supportFragmentManager.findFragmentByTag(id)
        if (fragment != null)
            supportFragmentManager.commit {
                remove(fragment)
            }
    }

    private fun removeAllFragments() {
        isInPreference = false
        val fragmentLanguage = supportFragmentManager.findFragmentByTag("preference_language")
        val fragmentCategory = supportFragmentManager.findFragmentByTag("preference_category")
        val fragmentYear = supportFragmentManager.findFragmentByTag("preference_year")
        val fragmentSort = supportFragmentManager.findFragmentByTag("preference_sort")
        if (fragmentLanguage != null)
            supportFragmentManager.commit {
                remove(fragmentLanguage)
            }
        if (fragmentCategory != null)
            supportFragmentManager.commit {
                remove(fragmentCategory)
            }
        if (fragmentYear != null)
            supportFragmentManager.commit {
                remove(fragmentYear)
            }
        if (fragmentSort != null)
            supportFragmentManager.commit {
                remove(fragmentSort)
            }
    }

    override fun onBackPressed() {
        if (isInPreference)
            removeAllFragments()
        else
            super.onBackPressed()
    }

    override fun onSearchRequested(): Boolean {
        //startActivity(new Intent(this, SearchActivity.class));
        return true
    }

}
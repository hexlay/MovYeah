package hexlay.movyeah.app_tv.models

import androidx.fragment.app.Fragment

data class PreferenceModel(
        val title: String,
        val key: String,
        var fragment: Fragment
)
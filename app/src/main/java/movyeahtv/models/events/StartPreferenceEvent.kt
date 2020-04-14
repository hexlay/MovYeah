package movyeahtv.models.events

import androidx.fragment.app.Fragment

data class StartPreferenceEvent(val key: String, val fragment: Fragment)
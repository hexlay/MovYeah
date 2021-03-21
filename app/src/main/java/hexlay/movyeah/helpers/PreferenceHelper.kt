package hexlay.movyeah.helpers

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

object PreferenceHelper {

    private var settings: SharedPreferences? = null

    fun makePreferences(context: Context) {
        settings = context.getSharedPreferences("adjPreferences", Context.MODE_PRIVATE)
    }
    
    var darkMode: Int
        get() = settings?.getInt("adj_dark_net", 1)!!
        set(value) {
            settings?.edit {
                putInt("adj_dark_net", value)
            }
        }

    var maxBrightness: Boolean
        get() = settings?.getBoolean("adj_max_brightness", false)!!
        set(value) {
            settings?.edit {
                putBoolean("adj_max_brightness", value)
            }
        }

    var pictureInPicture: Boolean
        get() = settings?.getBoolean("adj_pip_enabled", false)!!
        set(value) {
            settings?.edit {
                putBoolean("adj_pip_enabled", value)
            }
        }

    var downloadNotification: Boolean
        get() = settings?.getBoolean("adj_download_notification", false)!!
        set(value) {
            settings?.edit {
                putBoolean("adj_download_notification", value)
            }
        }

    var seek: Int
        get() = settings?.getInt("adj_seek", 5000)!!
        set(value) {
            settings?.edit {
                putInt("adj_seek", value)
            }
        }

    var lang: String
        get() = settings?.getString("adj_movie_language", "GEO")!!
        set(value) {
            settings?.edit {
                putString("adj_movie_language", value)
            }
        }

    var quality: String
        get() = settings?.getString("adj_movie_quality", "MEDIUM")!!
        set(value) {
            settings?.edit {
                putString("adj_movie_quality", value)
            }
        }

    var savedAlerts: MutableSet<String>
        get() = settings?.getStringSet("adj_alerts_ids", mutableSetOf())!!
        set(value) {
            settings?.edit {
                putStringSet("adj_alerts_ids", value)
            }
        }

    var savedNotificationIds: MutableSet<String>
        get() = settings?.getStringSet("adj_notification_ids", mutableSetOf())!!
        set(value) {
            settings?.edit {
                putStringSet("adj_notification_ids", value)
            }
        }

    fun addNotificationHistory(value: String) {
        val current = savedNotificationIds
        current.add(value)
        savedNotificationIds = current
    }

    fun addAlertHistory(value: String) {
        val current = savedAlerts
        current.add(value)
        savedAlerts = current
    }

}
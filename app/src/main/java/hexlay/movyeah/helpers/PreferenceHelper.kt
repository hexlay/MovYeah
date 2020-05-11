package hexlay.movyeah.helpers

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class PreferenceHelper(private val context: Context) {

    private var settings: SharedPreferences = context.getSharedPreferences("adjPreferences", Context.MODE_PRIVATE)

    var darkMode: Int
        get() = settings.getInt("adj_dark_net", 1)
        set(value) {
            settings.edit {
                putInt("adj_dark_net", value)
            }
        }

    var maxBrightness: Boolean
        get() = settings.getBoolean("adj_max_brightness", false)
        set(value) {
            settings.edit {
                putBoolean("adj_max_brightness", value)
            }
        }

    var autoStart: Boolean
        get() = settings.getBoolean("adj_auto_start", true)
        set(value) {
            settings.edit {
                putBoolean("adj_auto_start", value)
            }
        }

    var getNotifications: Boolean
        get() = settings.getBoolean("adj_notification_enabled", false)
        set(value) {
            settings.edit {
                putBoolean("adj_notification_enabled", value)
            }
        }

    var pictureInPicture: Boolean
        get() = settings.getBoolean("adj_pip_enabled", false)
        set(value) {
            settings.edit {
                putBoolean("adj_pip_enabled", value)
            }
        }

    var downloadNotification: Boolean
        get() = settings.getBoolean("adj_download_notification", false)
        set(value) {
            settings.edit {
                putBoolean("adj_download_notification", value)
            }
        }

    var notificationType: Int
        get() = settings.getInt("adj_notification_type", 0)
        set(value) {
            settings.edit {
                putInt("adj_notification_type", value)
            }
        }

    var seek: Int
        get() = settings.getInt("adj_seek", 5000)
        set(value) {
            settings.edit {
                putInt("adj_seek", value)
            }
        }

    var lang: String
        get() = settings.getString("adj_movie_language", "GEO")!!
        set(value) {
            settings.edit {
                putString("adj_movie_language", value)
            }
        }

    var quality: String
        get() = settings.getString("adj_movie_quality", "MEDIUM")!!
        set(value) {
            settings.edit {
                putString("adj_movie_quality", value)
            }
        }

    var savedNotificationIds: MutableSet<String>
        get() = settings.getStringSet("adj_notification_ids", mutableSetOf())!!
        set(value) {
            settings.edit {
                putStringSet("adj_notification_ids", value)
            }
        }

    var searchHistory: MutableSet<String>
        get() = settings.getStringSet("adj_search_history", mutableSetOf())!!
        set(value) {
            settings.edit {
                putStringSet("adj_search_history", value)
            }
        }

    fun addSearchHistory(value: String) {
        val current = searchHistory
        current.add(value)
        searchHistory = current
    }

    fun addNotificationHistory(value: String) {
        val current = savedNotificationIds
        current.add(value)
        savedNotificationIds = current
    }

}
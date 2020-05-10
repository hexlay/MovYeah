package hexlay.movyeah.helpers

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.provider.Settings
import android.text.Html
import android.text.Spanned
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import hexlay.movyeah.R
import org.apache.commons.collections4.CollectionUtils
import java.io.File
import java.util.*

fun View.setMargins(left: Int? = null, top: Int? = null, right: Int? = null, bottom: Int? = null) {
    val params = layoutParams as ViewGroup.MarginLayoutParams
    params.setMargins(
            left ?: params.leftMargin,
            top ?: params.topMargin,
            right ?: params.rightMargin,
            bottom ?: params.rightMargin
    )
    layoutParams = params
}

fun View.setSize(width: Int? = null, height: Int? = null) {
    layoutParams.width = width ?: layoutParams.width
    layoutParams.height = height ?: layoutParams.height
}

fun Int.toHumanDuration(): String {
    val hour = this / 60
    val minute = this % 60
    if (hour == 0 && minute == 0) {
        return "0"
    } else if (hour == 0) {
        return String.format("%02d წთ.", minute)
    } else if (minute == 0) {
        return String.format("%d სთ.", minute)
    }
    return String.format("%d სთ. %02d წთ.", hour, minute)
}

fun String.toHtml(): Spanned {
    return if (Constants.isAndroidN) {
        Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
    } else {
        @Suppress("DEPRECATION")
        Html.fromHtml(this)
    }
}

fun Menu.hideItem(id: Int) {
    findItem(id).isVisible = false
}

fun Menu.showItem(id: Int) {
    findItem(id).isVisible = true
}

fun <T> List<T>.toCommaList(): String = joinToString(separator = ", ")

fun <T> ArrayList<T>.differsFrom(other: ArrayList<T>): Boolean = size != other.size || !CollectionUtils.subtract(this, other).isEmpty()

fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
    observe(lifecycleOwner, object : Observer<T> {
        override fun onChanged(t: T?) {
            observer.onChanged(t)
            removeObserver(this)
        }
    })
}

fun String.translateLanguage(context: Context): String {
    val name = "full_${this.toLowerCase(Locale.ENGLISH)}"
    val identifier = context.resources.getIdentifier(name, "string", context.packageName)
    if (identifier == 0) {
        return this
    }
    return context.getString(identifier)
}

fun String.translateQuality(context: Context): String {
    return when (this) {
        "HIGH" -> context.getString(R.string.full_qual_hd)
        "MEDIUM" -> context.getString(R.string.full_qual_low)
        else -> ""
    }
}

fun ImageView.setUrl(url: String) {
    Glide.with(context)
            .load(url)
            .thumbnail(Glide.with(context).load(ContextCompat.getDrawable(context, R.drawable.loading)))
            .error(Glide.with(context).load(ContextCompat.getDrawable(context, R.drawable.no_image)))
            .into(this)
}

fun View.fade(alpha: Int, time: Long) {
    if (alpha > 0) {
        animate()
                .setDuration(time)
                .alpha(alpha.toFloat())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator) {
                        visibility = View.VISIBLE
                    }
                })
    } else {
        animate()
                .setDuration(time)
                .alpha(alpha.toFloat())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        visibility = View.INVISIBLE
                    }
                })
    }
}

fun View.showKeyboard() {
    val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

fun View.hideKeyboard() {
    val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
}

fun Activity.makeFullscreen() {
    val decorView = window.decorView
    var flags = decorView.systemUiVisibility
    flags = flags or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    flags = flags or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    decorView.systemUiVisibility = flags
}

fun Activity.setLightStatusBar() {
    val decorView = window.decorView
    var flags = decorView.systemUiVisibility
    flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    decorView.systemUiVisibility = flags
}

fun Activity.removeLightStatusBar() {
    val decorView = window.decorView
    var flags = decorView.systemUiVisibility
    flags = flags xor View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    decorView.systemUiVisibility = flags
}

fun Activity.isInNightMode(): Boolean {
    return (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
}

fun Activity.dpOf(value: Int): Int {
    val scale = resources.displayMetrics.density
    return (value * scale + 0.5f).toInt()
}

fun Activity.dpOf(value: Float): Float {
    val scale = resources.displayMetrics.density
    return (value * scale + 0.5f)
}

fun Activity.isInLandscape(): Boolean {
    val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val display = windowManager.defaultDisplay
    return display.rotation == Surface.ROTATION_90 || display.rotation == Surface.ROTATION_270
}

@SuppressLint("SourceLockedOrientationActivity")
fun Activity.requestPortrait() {
    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    Handler().postDelayed({ requestSensorForever() }, 2000)
}

@SuppressLint("SourceLockedOrientationActivity")
fun Activity.requestLandscape() {
    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    Handler().postDelayed({ requestSensorForever() }, 2000)
}

@SuppressLint("SourceLockedOrientationActivity")
fun Activity.requestPortraitForever() {
    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
}

fun Activity.requestSensorForever() {
    if (Settings.System.getInt(contentResolver, Settings.System.ACCELEROMETER_ROTATION, 0) == 1) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
    }
}

fun Activity.getStatusBarHeight(): Int {
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
    return if (resourceId > 0) resources.getDimensionPixelSize(resourceId) else 0
}

fun Activity.getActionBarSize(): Int {
    val styledAttributes = theme.obtainStyledAttributes(intArrayOf(android.R.attr.actionBarSize))
    val dimension = styledAttributes.getDimension(0, 0F).toInt()
    styledAttributes.recycle()
    return dimension
}

fun Fragment.downloadMovie(url: String, title: String): Long {
    val visibleNotification = if (PreferenceHelper(requireContext()).downloadNotification) {
        DownloadManager.Request.VISIBILITY_VISIBLE
    } else {
        DownloadManager.Request.VISIBILITY_HIDDEN
    }
    val request = DownloadManager.Request(Uri.parse(url))
    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
    request.setAllowedOverRoaming(false)
    request.setTitle(title)
    request.setNotificationVisibility(visibleNotification)
    request.setDestinationInExternalFilesDir(requireContext(), Environment.DIRECTORY_DOWNLOADS, "$title.mp4")
    val downloadManager = requireContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    return downloadManager.enqueue(request)
}

fun Fragment.getOfflineMovie(id: String): File {
    val downloadDirectory = requireContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath
    val path = "${downloadDirectory}/${id}.mp4"
    return File(path)
}

fun Fragment.downloadExists(id: String): Boolean {
    return getOfflineMovie(id).exists()
}

fun Fragment.getStatusBarHeight(): Int = requireActivity().getStatusBarHeight()

fun Fragment.getActionBarSize(): Int = requireActivity().getActionBarSize()

fun Fragment.dpOf(value: Int): Int = requireActivity().dpOf(value)

fun Fragment.dpOf(value: Float): Float = requireActivity().dpOf(value)

fun Fragment.isInLandscape(): Boolean = requireActivity().isInLandscape()

fun Fragment.requestPortrait() = requireActivity().requestPortrait()

fun Fragment.requestLandscape() = requireActivity().requestLandscape()

fun Fragment.requestPortraitForever() = requireActivity().requestPortraitForever()

fun Fragment.requestSensorForever() = requireActivity().requestSensorForever()

fun Fragment.setLightStatusBar() = requireActivity().setLightStatusBar()

fun Fragment.removeLightStatusBar() = requireActivity().removeLightStatusBar()

fun Fragment.isInNightMode(): Boolean = requireActivity().isInNightMode()

fun Fragment.getScreenWidth(): Int = resources.displayMetrics.widthPixels

fun Fragment.getWindow(): Window = requireActivity().window

fun Fragment.getDecorView(): View = getWindow().decorView

fun Fragment.onBackPressed() = requireActivity().onBackPressed()

fun Fragment.getDrawable(resId: Int): Drawable? = ContextCompat.getDrawable(requireContext(), resId)
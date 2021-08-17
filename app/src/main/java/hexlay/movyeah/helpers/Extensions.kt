package hexlay.movyeah.helpers

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.text.Html
import android.text.Spanned
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.faltenreich.skeletonlayout.Skeleton
import com.faltenreich.skeletonlayout.SkeletonConfig
import com.faltenreich.skeletonlayout.applySkeleton
import com.google.android.material.transition.platform.MaterialArcMotion
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import com.squareup.picasso.Picasso
import com.tapadoo.alerter.Alerter
import hexlay.movyeah.BuildConfig
import hexlay.movyeah.R
import hexlay.movyeah.api.models.github.Asset
import hexlay.movyeah.api.models.github.Release
import kotlinx.android.synthetic.main.dialog_updater.view.*
import org.apache.commons.collections4.CollectionUtils
import java.io.File
import java.util.*





internal fun getContentTransform(context: Context): MaterialContainerTransform {
    return MaterialContainerTransform().apply {
        addTarget(android.R.id.content)
        duration = 350
        pathMotion = MaterialArcMotion()
        isElevationShadowEnabled = false
        startElevation = 9f
        endElevation = 9f
        startContainerColor = ContextCompat.getColor(context, R.color.color_primary)
    }
}

fun isOutdated(version: String): Boolean {
    return Integer.valueOf(version) > BuildConfig.VERSION_CODE
}

fun AppCompatActivity.showUpdateDialog(release: Release) {
    if (isOutdated(release.tagName)) {
        MaterialDialog(this).show {
            title(R.string.new_update)
            message(text = release.body)
            cancelable(false)
            positiveButton(R.string.yes) {
                update(release.assets.first())
                dismiss()
            }
            negativeButton(R.string.no) {
                dismiss()
            }
        }
    }
}

fun AppCompatActivity.update(asset: Asset) {
    val downloadDir = Environment.DIRECTORY_DOWNLOADS
    val name = asset.name.split(".").first()
    val downloadId = downloadFile(asset.browserDownloadUrl, name, downloadDir, "apk")
    val dialog = MaterialDialog(this)
        .customView(R.layout.dialog_updater)
        .title(R.string.loading)
        .cancelable(false)
    val view = dialog.getCustomView()
    dialog.show()
    val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    val downloadProgress = DownloadProgress(this, downloadId, view.update_progress, downloadManager) {
        dialog.dismiss()
        if (it) {
            val install = Intent(Intent.ACTION_VIEW)
            install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            install.setDataAndType(
                uriFromFile(this, File(getExternalFilesDir(downloadDir), asset.name)),
                downloadManager.getMimeTypeForDownloadedFile(downloadId)
            )
            if (Constants.isAndroidO) {
                if (packageManager.canRequestPackageInstalls()) {
                    startActivity(install)
                } else {
                    startActivity(Intent(android.provider.Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:hexlay.movyeah")))
                }
            } else {
                startActivity(install)
            }
        }
    }
    downloadProgress.start()
}

fun uriFromFile(context: Context, file: File): Uri? {
    return if (Constants.isAndroidN) {
        FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file)
    } else {
        Uri.fromFile(file)
    }
}

fun FragmentActivity.applyExitMaterialTransform() {
    window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
    setExitSharedElementCallback(MaterialContainerTransformSharedElementCallback())
    window.sharedElementsUseOverlay = false
}

fun FragmentActivity.applyMaterialTransform(transitionName: String?) {
    window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
    ViewCompat.setTransitionName(findViewById(android.R.id.content), transitionName)
    setEnterSharedElementCallback(MaterialContainerTransformSharedElementCallback())
    window.sharedElementEnterTransition = getContentTransform(this)
    window.sharedElementReturnTransition = getContentTransform(this)
}

fun Activity.showAlert(title: String = "", text: String, color: Int = R.color.color_accent) {
    val alert = Alerter.create(this)
    if (title.isNotEmpty()) {
        alert.setTitle(title)
    }
    alert.setBackgroundColorRes(color)
    alert.setText(text)
    alert.show()
}

fun AppCompatActivity.initDarkMode() {
    when (PreferenceHelper.darkMode) {
        0 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }
    delegate.applyDayNight()
}

fun Context.playeExternally(url: String) {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.setDataAndType(Uri.parse(url), "video/*")
    startActivity(Intent.createChooser(intent, "Complete action using"))
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

@Suppress("DEPRECATION")
fun String.toHtml(): Spanned {
    return if (Constants.isAndroidN) {
        Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
    } else {
        Html.fromHtml(this)
    }
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
    val name = "full_${this.lowercase(Locale.ENGLISH)}"
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
        else -> this
    }
}

fun ImageView.setUrl(url: String) {
    Picasso.get()
        .load(Uri.parse(url))
        .placeholder(R.drawable.loading)
        .error(R.drawable.no_image)
        .into(this)
}

fun RecyclerView.createSkeleton(@LayoutRes resId: Int, itemCount: Int = 3): Skeleton {
    val config = SkeletonConfig.default(context)
    config.maskColor = ContextCompat.getColor(context, R.color.skeleton_color)
    config.shimmerColor = ContextCompat.getColor(context, R.color.skeleton_shimmer_color)
    config.shimmerDurationInMillis = 1000L
    config.maskCornerRadius = 5f
    return applySkeleton(resId, itemCount, config)
}

fun View.showKeyboard() {
    val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

fun View.hideKeyboard() {
    val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
}

@Suppress("DEPRECATION")
fun AppCompatActivity.makeFullscreen() {
    val decorView = window.decorView
    var flags = decorView.systemUiVisibility
    flags = flags or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    decorView.systemUiVisibility = flags
}

fun AppCompatActivity.dpOf(value: Int): Int {
    val scale = resources.displayMetrics.density
    return (value * scale + 0.5f).toInt()
}

fun AppCompatActivity.getStatusBarHeight(): Int {
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
    return if (resourceId > 0) resources.getDimensionPixelSize(resourceId) else 0
}

fun AppCompatActivity.getActionBarSize(): Int {
    val styledAttributes = theme.obtainStyledAttributes(intArrayOf(android.R.attr.actionBarSize))
    val dimension = styledAttributes.getDimension(0, 0F).toInt()
    styledAttributes.recycle()
    return dimension
}

fun AppCompatActivity.downloadFile(url: String, title: String, dir: String = Constants.DOWNLOAD_DIRECTORY, extension: String = "mp4"): Long {
    val visibleNotification = if (PreferenceHelper.downloadNotification) {
        DownloadManager.Request.VISIBILITY_VISIBLE
    } else {
        DownloadManager.Request.VISIBILITY_HIDDEN
    }
    val request = DownloadManager.Request(Uri.parse(url))
    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
    request.setAllowedOverRoaming(false)
    request.setTitle(title)
    request.setNotificationVisibility(visibleNotification)
    request.setDestinationInExternalFilesDir(this, dir, "$title.$extension")
    val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    return downloadManager.enqueue(request)
}

fun AppCompatActivity.getOfflineMovie(id: String): File {
    val downloadDirectory = getExternalFilesDir(Constants.DOWNLOAD_DIRECTORY)?.absolutePath
    val path = "${downloadDirectory}/${id}.mp4"
    return File(path)
}

fun AppCompatActivity.downloadExists(id: String): Boolean {
    return getOfflineMovie(id).exists()
}

fun Fragment.reqActivity(): AppCompatActivity {
    return (requireActivity() as AppCompatActivity)
}

fun Fragment.downloadFile(url: String, title: String): Long {
    return reqActivity().downloadFile(url, title)
}

fun Fragment.getOfflineMovie(id: String): File {
    return reqActivity().getOfflineMovie(id)
}

fun Fragment.downloadExists(id: String): Boolean {
    return getOfflineMovie(id).exists()
}

fun Fragment.getStatusBarHeight(): Int = reqActivity().getStatusBarHeight()

fun Fragment.getActionBarSize(): Int = reqActivity().getActionBarSize()

fun Fragment.dpOf(value: Int): Int = reqActivity().dpOf(value)

fun Fragment.showAlert(title: String = "", text: String) = reqActivity().showAlert(title, text)
package hexlay.movyeah.app_tv.helpers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.LightingColorFilter
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Parcelable
import android.text.Html
import android.text.Spanned
import android.view.Window
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.leanback.app.BackgroundManager
import androidx.leanback.widget.DetailsOverviewRow
import androidx.leanback.widget.PlaybackControlsRow
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import hexlay.movyeah.app_tv.R
import org.apache.commons.collections4.CollectionUtils
import java.util.*

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
    if (this == "NONE")
        return ""
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

fun Fragment.getDrawable(resId: Int): Drawable? = ContextCompat.getDrawable(requireContext(), resId)

fun Fragment.getWhiteDrawable(resId: Int): Drawable? {
    val drawable = getDrawable(resId)
    drawable?.colorFilter = LightingColorFilter(Color.WHITE, Color.WHITE)
    return drawable
}

fun BackgroundManager.setDrawableFromUrl(context: Context, url: String) {
    Glide.with(context)
            .asBitmap()
            .load(url)
            .thumbnail(Glide.with(context).asBitmap().load(ContextCompat.getDrawable(context, R.drawable.loading)))
            .error(Glide.with(context).asBitmap().load(ContextCompat.getDrawable(context, R.drawable.no_image)))
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    drawable = BitmapDrawable(context.resources, resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
}

fun DetailsOverviewRow.setDrawableFromUrl(context: Context, url: String) {
    Glide.with(context)
            .asBitmap()
            .load(url)
            .thumbnail(Glide.with(context).asBitmap().load(ContextCompat.getDrawable(context, R.drawable.loading)))
            .error(Glide.with(context).asBitmap().load(ContextCompat.getDrawable(context, R.drawable.no_image)))
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    imageDrawable = BitmapDrawable(context.resources, resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
}

fun Fragment.makeParcelableExtra(key: String, value: Parcelable): Bundle {
    val extras = Bundle()
    extras.putParcelable(key, value)
    return extras
}

fun PlaybackControlsRow.setDrawableFromUrl(context: Context, url: String) {
    Glide.with(context)
            .asBitmap()
            .load(url)
            .thumbnail(Glide.with(context).asBitmap().load(ContextCompat.getDrawable(context, R.drawable.loading)))
            .error(Glide.with(context).asBitmap().load(ContextCompat.getDrawable(context, R.drawable.no_image)))
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    imageDrawable = BitmapDrawable(context.resources, resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
}

fun Fragment.getWindow(): Window = requireActivity().window
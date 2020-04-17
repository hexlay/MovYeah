package movyeahtv.helpers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.LightingColorFilter
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.leanback.app.BackgroundManager
import androidx.leanback.widget.DetailsOverviewRow
import androidx.leanback.widget.PlaybackControlsRow
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import hexlay.movyeah.R
import hexlay.movyeah.helpers.getDrawable

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
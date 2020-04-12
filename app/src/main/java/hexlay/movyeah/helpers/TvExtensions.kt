package hexlay.movyeah.helpers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.leanback.app.BackgroundManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import hexlay.movyeah.R

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
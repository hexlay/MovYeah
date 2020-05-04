package hexlay.movyeah.app_tv.presenters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.Presenter
import hexlay.movyeah.api.models.attributes.Actor
import hexlay.movyeah.app_tv.R
import hexlay.movyeah.app_tv.helpers.setUrl


class CastPresenter(private val context: Context) : Presenter() {

    inner class ViewHolder(view: View) : Presenter.ViewHolder(view) {

        val imageCardView = view as ImageCardView

    }

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val imageCardView = ImageCardView(context)
        imageCardView.isFocusable = true
        imageCardView.isFocusableInTouchMode = true
        imageCardView.setMainImageDimensions(300, 400)
        imageCardView.setBackgroundColor(ContextCompat.getColor(context, R.color.fastlane_background))
        return ViewHolder(imageCardView)
    }

    override fun onBindViewHolder(viewHolder: Presenter.ViewHolder, item: Any) {
        val actor = item as Actor
        val holder = viewHolder as ViewHolder
        holder.imageCardView.titleText = actor.getTitle()
        actor.poster?.let { holder.imageCardView.mainImageView.setUrl(it) }
    }

    override fun onUnbindViewHolder(viewHolder: Presenter.ViewHolder) {}


}
package movyeahtv.presenters

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.leanback.widget.DetailsOverviewRowPresenter
import androidx.leanback.widget.Presenter
import androidx.leanback.widget.RowPresenter
import hexlay.movyeah.R

// Deprecated, but elegant as hell <3
class WatchPresenter(private val context: Context, presenter: Presenter) : DetailsOverviewRowPresenter(presenter) {

    override fun onBindRowViewHolder(holder: RowPresenter.ViewHolder?, item: Any?) {
        backgroundColor = ContextCompat.getColor(context, R.color.default_background)
        super.onBindRowViewHolder(holder, item)
    }

}
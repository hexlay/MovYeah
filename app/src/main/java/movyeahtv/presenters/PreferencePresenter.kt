package movyeahtv.presenters

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.leanback.widget.Presenter
import movyeahtv.models.PreferenceModel


class PreferencePresenter(private val context: Context) : Presenter() {

    inner class ViewHolder(view: View) : Presenter.ViewHolder(view) {

        val preference = view as TextView

    }

    override fun onCreateViewHolder(parent: ViewGroup?): ViewHolder {
        val view = TextView(context)
        view.layoutParams = ViewGroup.LayoutParams(315, 175)
        view.isFocusable = true
        view.isFocusableInTouchMode = true
        view.setBackgroundColor(ContextCompat.getColor(context, hexlay.movyeah.R.color.default_background))
        view.setTextColor(Color.WHITE)
        view.gravity = Gravity.CENTER
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: Presenter.ViewHolder, item: Any?) {
        val holder = viewHolder as ViewHolder
        val (title, type) = item as PreferenceModel
        holder.preference.text = title
        holder.preference.setOnClickListener {

        }
    }

    override fun onUnbindViewHolder(viewHolder: Presenter.ViewHolder) {
        TODO("Not yet implemented")
    }


}
package hexlay.movyeah.adapters.view_holders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.afollestad.recyclical.ViewHolder
import hexlay.movyeah.R

class CastViewHolder(itemView: View) : ViewHolder(itemView) {

    var name: TextView = itemView.findViewById(R.id.title)
    var image: ImageView = itemView.findViewById(R.id.image)

    init {
        name.isSelected = true
        name.bringToFront()
    }

}
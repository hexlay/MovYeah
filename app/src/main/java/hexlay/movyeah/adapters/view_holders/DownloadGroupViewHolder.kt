package hexlay.movyeah.adapters.view_holders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.recyclical.ViewHolder
import hexlay.movyeah.R
import hexlay.movyeah.layouts.CustomExpandableLayout

class DownloadGroupViewHolder(itemView: View) : ViewHolder(itemView) {

    var groupTitle: TextView = itemView.findViewById(R.id.title)
    var groupContent: TextView = itemView.findViewById(R.id.item_content)
    var groupImage: ImageView = itemView.findViewById(R.id.image)
    var children: CustomExpandableLayout = itemView.findViewById(R.id.expandable_layout)
    var childrenHolder: RecyclerView = itemView.findViewById(R.id.download_childs)

}
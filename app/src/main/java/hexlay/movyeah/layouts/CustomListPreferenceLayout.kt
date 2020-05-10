package hexlay.movyeah.layouts

import android.content.Context
import android.util.AttributeSet
import androidx.preference.ListPreference
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import hexlay.movyeah.R

class CustomListPreferenceLayout : ListPreference {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    override fun onClick() {
        val initial = entryValues.indexOf(value)
        negativeButtonText = context.getString(R.string.cancel)
        MaterialDialog(context).show {
            title(text = dialogTitle.toString())
            if (dialogMessage != null) {
                message(text = dialogMessage)
            }
            if (dialogIcon != null) {
                icon(drawable = dialogIcon)
            }
            listItemsSingleChoice(items = entries.toList(), initialSelection = initial, waitForPositiveButton = false) { _, index, _ ->
                dismiss()
                val item = entryValues[index].toString()
                if (callChangeListener(item)) {
                    value = item
                }
            }
            negativeButton(text = negativeButtonText)
        }
    }


}
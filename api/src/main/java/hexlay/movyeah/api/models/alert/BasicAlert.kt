package hexlay.movyeah.api.models.alert

import com.google.gson.annotations.SerializedName

data class BasicAlert(
        @SerializedName("id")
        var id: String,
        @SerializedName("message")
        var message: String
)
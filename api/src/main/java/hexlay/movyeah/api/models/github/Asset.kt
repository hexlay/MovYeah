package hexlay.movyeah.api.models.github

import com.google.gson.annotations.SerializedName

data class Asset(
    @SerializedName("name")
    var name: String,
    @SerializedName("label")
    var label: String,
    @SerializedName("browser_download_url")
    var browserDownloadUrl: String,
)
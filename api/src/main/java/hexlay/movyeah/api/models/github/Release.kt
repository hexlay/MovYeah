package hexlay.movyeah.api.models.github

import com.google.gson.annotations.SerializedName

data class Release(
    @SerializedName("name")
    var name: String,
    @SerializedName("tag_name")
    var tagName: String,
    @SerializedName("body")
    var body: String,
    @SerializedName("assets")
    var assets: List<Asset>
)
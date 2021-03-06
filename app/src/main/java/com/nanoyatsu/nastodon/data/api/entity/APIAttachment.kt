package com.nanoyatsu.nastodon.data.api.entity

import android.os.Parcelable
import com.nanoyatsu.nastodon.data.domain.Attachment
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
@JsonClass(generateAdapter = true)
data class APIAttachment(
    val id: String,
    val type: String,
    val url: String,
    @Json(name = "preview_url") val previewUrl: String,
    @Json(name = "remote_url") val remoteUrl: String?,
    @Json(name = "text_url") val textUrl: String?,
    // val meta:
    val description: String?,
    val blurhash: String? // added in 2.8.1
) : Parcelable {
    fun asDomainModel(): Attachment =
        Attachment(id, type, url, previewUrl, remoteUrl, textUrl, description, blurhash)
}

enum class MediaType() {
    UNKNOWN, IMAGE, GIFV, VIDEO, AUDIO;

    fun toLower() = this.name.toLowerCase(Locale.ROOT)
}
//    - unknown = unsupported or unrecognized file type
//    - image = Static image
//    - gifv = Looping, soundless animation
//    - video = Video clip
//    - audio = Audio track

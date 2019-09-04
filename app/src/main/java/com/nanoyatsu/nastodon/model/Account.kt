package com.nanoyatsu.nastodon.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Account(
    val id: String,
    val username: String,
    val acct: String,
    val displayName: String,
    val locked: Boolean,
    val createdAt: String,
    val followersCount: Int,
    val followingCount: Int,
    val statusesCount: Int,
    val note: String,
    val url: String,
    val avatar: String,
    val avatarStatic: String,
    val header: String,
    val headerStatic: String,
//    val emojis:Array<Emoji>,
    val moved: Account?,
//    val fields:Array<Hash>?,
    val bot: Boolean?
):Parcelable

//"account": {
//    "id": "45717",
//    "username": "nanoyatsu",
//    "acct": "nanoyatsu",
//    "display_name": "なのやつ",
//    "locked": false,
//    "created_at": "2018-08-31T16:29:54.926Z",
//    "note": "\u003cp\u003e謙虚さ…謙虚さだぞ\u003c/p\u003e",
//    "url": "https://qiitadon.com/@nanoyatsu",
//    "avatar": "https://file.qiitadon.com/accounts/avatars/000/045/717/original/a97f93c14f4348b42688bb72034b1d92.png",
//    "avatar_static": "https://file.qiitadon.com/accounts/avatars/000/045/717/original/a97f93c14f4348b42688bb72034b1d92.png",
//    "header": "https://file.qiitadon.com/accounts/headers/000/045/717/original/0d08b98cee437bdc.png",
//    "header_static": "https://file.qiitadon.com/accounts/headers/000/045/717/original/0d08b98cee437bdc.png",
//    "followers_count": 50,
//    "following_count": 25,
//    "statuses_count": 8382,
//    "qiita_username": "nanoyatsu"
//}

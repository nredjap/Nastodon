package com.nanoyatsu.nastodon.view.notice

import androidx.paging.DataSource
import com.nanoyatsu.nastodon.data.api.endpoint.MastodonApiNotifications
import com.nanoyatsu.nastodon.data.api.entity.Notification

class NoticeDataSourceFactory(
    private val noticeKind: NoticeViewModel.Kind,
    private val apiDir: MastodonApiNotifications,
    private val token: String
) : DataSource.Factory<String, Notification>() {
    override fun create(): DataSource<String, Notification> {
        return NoticeDataSource(noticeKind, apiDir, token)
    }
}
package com.nanoyatsu.nastodon.view.notice

import androidx.lifecycle.ViewModel
import com.nanoyatsu.nastodon.data.api.endpoint.MastodonApiNotifications
import com.nanoyatsu.nastodon.data.api.entity.APINotification
import com.nanoyatsu.nastodon.data.domain.NotificationType
import com.nanoyatsu.nastodon.data.repository.notice.NoticeRepository
import retrofit2.Response
import javax.inject.Inject

typealias NotificationsGetter = (suspend (MastodonApiNotifications, String, String?, String?) -> Response<List<APINotification>>)

class NoticeViewModel @Inject constructor(repo: NoticeRepository) : ViewModel() {
    enum class Kind(val getter: NotificationsGetter) { ALL(::allNoticeApiProvider), REPLY(::replyNoticeApiProvider) }

    private val repoResult = repo.posts()
    val notifications = repoResult.pagedList
    val networkState = repoResult.networkState
    val isInitialising = repoResult.isRefreshing
    private val refresh = repoResult.refresh
    private val retry = repoResult.retry

    fun refresh() = refresh.invoke()
    fun retry() = retry.invoke()

    companion object {
        const val NOTICE_PAGE_SIZE = 20
        private val excludeReply = NotificationType.values()
            .filterNot { it == NotificationType.MENTION }.map { it.value }.toTypedArray()

        suspend fun allNoticeApiProvider(
            apiDir: MastodonApiNotifications, token: String, maxId: String?, sinceId: String?
        ) = apiDir.getAllNotifications(token, maxId, sinceId, null, null, null, null)

        suspend fun replyNoticeApiProvider(
            apiDir: MastodonApiNotifications, token: String, maxId: String?, sinceId: String?
        ) = apiDir.getAllNotifications(token, maxId, sinceId, null, null, null, *excludeReply)
    }
}

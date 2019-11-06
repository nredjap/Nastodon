package com.nanoyatsu.nastodon.view.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nanoyatsu.layoutComponent.InfiniteScrollListener
import com.nanoyatsu.nastodon.R
import com.nanoyatsu.nastodon.model.AuthPreferenceManager
import com.nanoyatsu.nastodon.model.Status
import com.nanoyatsu.nastodon.presenter.MastodonApiManager
import com.nanoyatsu.nastodon.presenter.MastodonApiTimelines
import com.nanoyatsu.nastodon.view.adapter.TimelineAdapter
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.Response

class TimelineFragment() : Fragment() {
    enum class BundleKey { GET_METHOD }
    enum class GetMethod { HOME, LOCAL, GLOBAL, SEARCH }

    private var eventListener: EventListener? = null
    private lateinit var getMethod: GetMethod

    // todo 外から入れるようにする
    private lateinit var timelinesApi: MastodonApiTimelines
    private lateinit var pref: AuthPreferenceManager

    override fun onAttach(context: Context) {
        super.onAttach(context)
        eventListener = context as? EventListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { bundle ->
            getMethod =
                GetMethod.values().find { it.name == bundle.getString(BundleKey.GET_METHOD.name) } ?: GetMethod.HOME
        }

        val context = this.context ?: return
        pref = AuthPreferenceManager(context)
        if (pref.instanceUrl == "") return // そのまま認証に行ってもいい
        timelinesApi = MastodonApiManager(pref.instanceUrl).timelines
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.content_main, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // SwipeRefreshLayout 引っ張って更新するやつ
        swipe_refresh.setOnRefreshListener {
            CoroutineScope(context = Dispatchers.Main).launch {
                initTimeline()
                swipe_refresh.isRefreshing = false
            }
        }

        eventListener?.progressStart()
        CoroutineScope(context = Dispatchers.Main).launch {
            initTimeline()
            eventListener?.progressEnd()
        }
    }

    override fun onResume() {
        super.onResume()
    }

    private suspend fun initTimeline() {
        val context = this.context ?: return
        val pref = AuthPreferenceManager(context)
        if (pref.instanceUrl == "")
            return

        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        timelineView.layoutManager = layoutManager // fixme 画面回転を連続したりするとNPE
        val timeline = ArrayList<Status>() // todo ViewModelに持つ
        timelineView.adapter = TimelineAdapter(context, timeline)

        timelineView.clearOnScrollListeners()
        timelineView.addOnScrollListener(object : InfiniteScrollListener(layoutManager) {
            override fun onLoadMore(current_page: Int) {
                eventListener?.progressStart()
                CoroutineScope(context = Dispatchers.Main).launch {
                    reloadTimeline(timeline, timeline.last().id, null)
                    eventListener?.progressEnd()
                }
            }
        })

        reloadTimeline(timeline)
    }

    // todo 同じ型シグネチャで取得関数を用意する enumに紐付けたいが、staticとの絡みで案が必要
    private suspend fun callHomeTimeline(maxId: String?, sinceId: String?) =
        timelinesApi.getHomeTimeline(pref.accessToken, maxId, sinceId)

    private suspend fun callLocalPublicTimeline(maxId: String?, sinceId: String?) =
        timelinesApi.getPublicTimeline(authorization = pref.accessToken, local = true, maxId = maxId, sinceId = sinceId)

    private suspend fun callGlobalPublicTimeline(maxId: String?, sinceId: String?) =
        timelinesApi.getPublicTimeline(
            authorization = pref.accessToken, local = false, maxId = maxId, sinceId = sinceId
        )

    private fun returnTimelineGetter(getMethod: GetMethod): suspend ((String?, String?) -> Response<Array<Status>>) {
        val callApi = when (getMethod) {
            GetMethod.HOME -> ::callHomeTimeline
            GetMethod.LOCAL -> ::callLocalPublicTimeline
            GetMethod.GLOBAL -> ::callGlobalPublicTimeline
            GetMethod.SEARCH -> ::callHomeTimeline
        }
        return { maxId: String?, sinceId: String? -> callApi(maxId, sinceId) }
    }

    private suspend fun reloadTimeline(timeline: ArrayList<Status>, maxId: String? = null, sinceId: String? = null) {
        val getter = suspend { returnTimelineGetter(getMethod)(maxId, sinceId) }
        val toots = getByApi(getter)
        timeline.addAll(toots.toList())
        timelineView.adapter?.notifyDataSetChanged()
    }

    private suspend fun getByApi(getter: suspend () -> Response<Array<Status>>): Array<Status> {
        return try {
            val res = getter()
            res.body()
                ?: arrayOf() // todo レスポンスが期待通りじゃないときの処理 res.errorBody()
        } catch (e: HttpException) {
            e.printStackTrace()
            // todo 通信失敗のときの処理
            arrayOf()
        }
    }

    interface EventListener {
        fun progressStart()
        fun progressEnd()
    }

    companion object {
        fun newInstance(method: GetMethod) =
            TimelineFragment().apply {
                arguments = Bundle().apply {
                    putString(BundleKey.GET_METHOD.name, method.name)
                }
            }
    }

}
package com.nanoyatsu.nastodon.view.notice

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.nanoyatsu.nastodon.R
import com.nanoyatsu.nastodon.data.api.entity.Notification
import com.nanoyatsu.nastodon.data.api.entity.NotificationType
import com.nanoyatsu.nastodon.databinding.ItemNoticeBinding

class NoticeAdapter(private val context: Context) :
    PagedListAdapter<Notification, NoticeAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notice = getItem(position)
        holder.bind(context, notice!!)
    }

    class ViewHolder(val binding: ItemNoticeBinding) : RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val binding =
                    ItemNoticeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return ViewHolder(binding)
            }
        }

        fun bind(context: Context, notice: Notification) {
            binding.notice = notice

            val type = NotificationType.values().firstOrNull { it.value == notice.type }
            binding.description.text = if (type == null)
                context.getString(R.string.noticeDescriptionUndefined, notice.account.displayName)
            else
                context.getString(type.descriptionId, notice.account.displayName)

            // todo type別アイコン
            // binding.typeIcon.background = context.getDrawable(..)
        }
    }

    companion object {
        class DiffCallback : DiffUtil.ItemCallback<Notification>() {
            override fun areItemsTheSame(oldItem: Notification, newItem: Notification) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Notification, newItem: Notification) =
                oldItem == newItem
        }
    }
}
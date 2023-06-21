package com.sasy.nontag.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sasy.nontag.R
import com.sasy.nontag.model.ConnectedHistory

class DeviceHistoryAdapter(
    context: Context,
    dataList: List<ConnectedHistory>,
    private val onClickListener: (View, ConnectedHistory) -> Unit
) :
    RecyclerView.Adapter<DeviceHistoryAdapter.ViewHolder>() {

    private var dataList = emptyList<ConnectedHistory>()
    var mContext: Context = context

    init {
        this.dataList = dataList
        this.mContext = context
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView = itemView.findViewById(R.id.textView_device_name)
        var image: ImageView = itemView.findViewById(R.id.imageView_device_type)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.connected_history_layout_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataList[position]
        holder.title.text = data.deviceName
        if (null != data.imageIcon) {
            val resourceId: Int =
                mContext.resources.getIdentifier(data.imageIcon, "drawable", mContext.packageName)
            if (resourceId != 0) {
                holder.image.setImageResource(resourceId)
            }
        }
        holder.itemView.setOnClickListener { view ->
            onClickListener.invoke(view, data)
        }
    }

    override fun getItemCount() = dataList.size
}
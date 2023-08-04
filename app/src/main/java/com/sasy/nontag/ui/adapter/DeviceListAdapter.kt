package com.sasy.nontag.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.sasy.nontag.R
import com.sasy.nontag.model.ConnectedHistory

internal class DeviceListAdapter(
    private var mList: ArrayList<ConnectedHistory>,
    private val mContext: Context,
    private val onClickListener: (View, ConnectedHistory) -> Unit
) : RecyclerView.Adapter<DeviceListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.connected_history_layout_item, parent, false)
        return ViewHolder(view)
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        var title: TextView = itemView.findViewById(R.id.textView_device_name)
        var deviceAddress: TextView = itemView.findViewById(R.id.textView_device_address)
        var image: ImageView = itemView.findViewById(R.id.imageView_device_type)
        var imageViewUnPair: ImageView = itemView.findViewById(R.id.imageViewUnPair)
        val dataLayout: ConstraintLayout = itemView.findViewById(R.id.dataLayout)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = mList[position]
        holder.title.text = data.deviceName
        holder.deviceAddress.text = data.device_id
        if (null != data.imageIcon) {
            val resourceId: Int =
                mContext.resources.getIdentifier(data.imageIcon, "drawable", mContext.packageName)
            if (resourceId != 0) {
                holder.image.setImageResource(resourceId)
            }
        }
        if (data.isPaired) {
            holder.imageViewUnPair.visibility = View.VISIBLE
        } else {
            holder.imageViewUnPair.visibility = View.GONE
        }
        holder.dataLayout.setOnClickListener { view ->
            onClickListener.invoke(view, data)
        }
        holder.imageViewUnPair.setOnClickListener { view ->
            onClickListener.invoke(view, data)
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun clearData() {
        mList.clear()
        notifyDataSetChanged()
    }
}
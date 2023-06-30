/*
 *
 *  * Created by Dhiraj Pandey on 20/09/22, 9:16 AM
 *  * Copyright (c) 2022 . Mindpool Technologies Limited, All Rights Reserved.
 *  * Last modified 19/09/22, 8:00 PM
 *
 */

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
import com.sasy.nontag.model.DetailsMenuItem

class DeviceListAdapter(
    private val mContext: Context,
    private val mList: List<ConnectedHistory>,
    private val onClickListener: (View, ConnectedHistory) -> Unit
) : RecyclerView.Adapter<DeviceListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.connected_history_layout_item, parent, false)

        return ViewHolder(view)
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
        holder.itemView.setOnClickListener { view ->
            onClickListener.invoke(view, data)
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        var title: TextView = itemView.findViewById(R.id.textView_device_name)
        var deviceAddress: TextView = itemView.findViewById(R.id.textView_device_address)
        var image: ImageView = itemView.findViewById(R.id.imageView_device_type)
    }
}
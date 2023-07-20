package com.sasy.nontag.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.sasy.nontag.R
import com.sasy.nontag.model.DevInfoModel

class TagListAdapter(
    context: Context,
    dataList: List<DevInfoModel>,
) :
    RecyclerView.Adapter<TagListAdapter.ViewHolder>() {

    private var dataList = emptyList<DevInfoModel>()
    var mContext: Context = context

    init {
        this.dataList = dataList
        this.mContext = context
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txtKayName: MaterialTextView = itemView.findViewById(R.id.txtKayName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.tag_list_layout_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataList[position]
        holder.txtKayName.text = data.key
    }

    override fun getItemCount() = dataList.size
}
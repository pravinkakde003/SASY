package com.sasy.nontag.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.sasy.nontag.R
import com.sasy.nontag.model.DetailsMenuItem

class MenuAdapter(
    context: Context,
    dataList: List<DetailsMenuItem>,
    private val onClickListener: (View, DetailsMenuItem) -> Unit
) :
    RecyclerView.Adapter<MenuAdapter.ViewHolder>() {

    private var dataList = emptyList<DetailsMenuItem>()
    var mContext: Context = context

    init {
        this.dataList = dataList
        this.mContext = context
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var buttonText: MaterialButton = itemView.findViewById(R.id.buttonText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.menu_layout_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataList[position]
        holder.buttonText.text = data.name
        holder.buttonText.setOnClickListener { view ->
            onClickListener.invoke(view, data)
        }
    }

    override fun getItemCount() = dataList.size
}
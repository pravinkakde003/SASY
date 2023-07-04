package com.sasy.nontag.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sasy.nontag.R
import com.sasy.nontag.model.ConnectedHistory
import com.sasy.nontag.model.DeviceListDataModel

class DeviceListTitleAdapter(
    private val context: Context,
    private val mList: ArrayList<DeviceListDataModel>,
    private val onClickListener: (View, ConnectedHistory) -> Unit
) :
    RecyclerView.Adapter<DeviceListTitleAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.title_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemsViewModel = mList[position]
        itemsViewModel.menuList?.let {
            if (itemsViewModel.menuList.isNotEmpty()) {
                holder.listTitle.text = itemsViewModel.categoryTitle
                holder.menuListRecyclerView.adapter =
                    DeviceListAdapter(
                        mContext = context, it, onClickListener
                    )
            }
        }
        val mLayoutManager: RecyclerView.LayoutManager =
            object : LinearLayoutManager(context) {
                override fun canScrollVertically(): Boolean {
                    return false
                }
            }
        holder.menuListRecyclerView.layoutManager = mLayoutManager
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val listTitle: TextView = itemView.findViewById(R.id.recyclerview_title)
        val menuListRecyclerView: RecyclerView = itemView.findViewById(R.id.menu_list_recycler_view)
    }
}

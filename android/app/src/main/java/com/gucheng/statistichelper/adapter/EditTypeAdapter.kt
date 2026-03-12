package com.gucheng.statistichelper.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gucheng.statistichelper.R
import com.gucheng.statistichelper.database.entity.ItemType

/**
 * Created on 2021/7/11.
 */
class EditTypeAdapter(typeList:List<ItemType>,val listener:TypeListener) : RecyclerView.Adapter<EditTypeAdapter.EditTypeViewHolder>() {

    private var mTypeList: List<ItemType> = typeList

    class EditTypeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val typeName = itemView.findViewById<TextView>(R.id.type_name)
        val editBtn = itemView.findViewById<Button>(R.id.edit_btn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EditTypeViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_edit_type, parent, false)
        return EditTypeViewHolder(view)
    }

    override fun onBindViewHolder(holder: EditTypeViewHolder, position: Int) {
        holder.typeName.text = mTypeList.get(position).typeName
        holder.editBtn.setOnClickListener {
            listener.edit(mTypeList.get(position))
        }

    }

    override fun getItemCount(): Int {
        if (mTypeList == null) {
            return 0
        } else {
            return mTypeList.size
        }
    }

    interface TypeListener {
        fun edit(type:ItemType)
    }

}
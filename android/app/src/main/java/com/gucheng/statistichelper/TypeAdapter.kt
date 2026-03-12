package com.gucheng.statistichelper

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gucheng.statistichelper.database.entity.ItemType

class TypeAdapter(selectListener: ItemFragment.TypeSelectListener?) :
    ListAdapter<ItemType, TypeAdapter.TypeViewHolder>(ITEM_COMPARATOR) {

    init {
        listener = selectListener
    }

    class TypeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val typeName = itemView.findViewById<TextView>(R.id.type)

        companion object {
            fun create(parent: ViewGroup): TypeViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.type_item, parent, false)
                return TypeViewHolder(view)
            }
        }

        fun bind(itemType: ItemType) {
            typeName.text = itemType.typeName
            typeName.setOnClickListener {
                listener?.typeSelect(itemType)
            }
        }

    }

    companion object {
        var listener: ItemFragment.TypeSelectListener? = null
        private val ITEM_COMPARATOR = object : DiffUtil.ItemCallback<ItemType>() {
            override fun areItemsTheSame(oldItem: ItemType, newItem: ItemType): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ItemType, newItem: ItemType): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TypeViewHolder {
        return TypeViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: TypeViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }

    fun unRegisterListener() {
        listener = null
    }

}
package com.gucheng.statistichelper.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gucheng.statistichelper.R
import com.gucheng.statistichelper.Utils.formatAmount
import com.gucheng.statistichelper.adapter.ChangeDetailsAdapter.ChangeViewHolder
import com.gucheng.statistichelper.database.entity.ChangeRecord

/**
 * Created on 2021/11/30.
 */
class ChangeDetailsAdapter(private val mDatas: ArrayList<ChangeRecord>?, private val mType:Int) :
    RecyclerView.Adapter<ChangeViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChangeViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_change_detail, parent, false)
        return ChangeViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChangeViewHolder, position: Int) {
        val (_, createTime, changeAmount, remark, _, _, amountAfterModified) = mDatas!![position]
        holder.dateTxt.text = createTime
        if (changeAmount!! > 0) {
            holder.amountTxt.text = "+" + formatAmount(
                changeAmount
            )
        } else {
            holder.amountTxt.text = formatAmount(changeAmount)
        }
        holder.remarkTxt.text = remark
        holder.amountAfterModified.text =
            formatAmount(amountAfterModified)
        if (amountAfterModified == 0.0) {
            holder.amountAfterModified.visibility = View.GONE
        }
//        if (mType == -1) {
//            holder.balanceLayout.visibility = View.GONE
//        }
    }

    override fun getItemCount(): Int {
        return mDatas?.size ?: 0
    }

    inner class ChangeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var dateTxt: TextView
        var amountTxt: TextView
        var remarkTxt: TextView
        var balanceLayout: View
        var amountAfterModified: TextView

        init {
            dateTxt = itemView.findViewById(R.id.date)
            amountTxt = itemView.findViewById(R.id.change_amount)
            remarkTxt = itemView.findViewById(R.id.remark)
            amountAfterModified = itemView.findViewById(R.id.amount_after_change)
            balanceLayout = itemView.findViewById(R.id.balance_layout)
            //            itemBalance = itemView.findViewById(R.id.balance);
        }
    }
}
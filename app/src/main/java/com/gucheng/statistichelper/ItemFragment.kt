package com.gucheng.statistichelper

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gucheng.statistichelper.activity.EditTypeActivity
import com.gucheng.statistichelper.database.entity.ItemType
import com.gucheng.statistichelper.database.MainActivityViewModel

class ItemFragment(viewModel: MainActivityViewModel) : DialogFragment() {
    private var viewModel: MainActivityViewModel
    private  var listener: TypeSelectListener? = null
    private var typeAdapter: TypeAdapter? = null

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    init {
        this.viewModel = viewModel
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as TypeSelectListener
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
        typeAdapter?.unRegisterListener()
    }


    interface TypeSelectListener {
        fun typeSelect(itemType: ItemType)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.item_fragment, container, false)
        var recyclerView = view.findViewById<RecyclerView>(R.id.item_recyclerview)
        val newTypeBtn = view.findViewById<Button>(R.id.new_type)
        newTypeBtn.setOnClickListener {
            val intent = Intent(it.context, EditTypeActivity::class.java)
            startActivity(intent)
        }
        typeAdapter = TypeAdapter(listener)
        recyclerView.adapter = typeAdapter
        recyclerView.layoutManager = LinearLayoutManager(container?.context)
        viewModel.allTypes.observe(owner = this) { types ->
            types.let { typeAdapter?.submitList(it) }
        }
        return view
    }


}
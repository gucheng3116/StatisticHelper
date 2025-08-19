package com.gucheng.statistichelper.activity

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.asLiveData
import androidx.lifecycle.observe
import androidx.recyclerview.widget.RecyclerView
import com.gucheng.statistichelper.AccountApplication
import com.gucheng.statistichelper.R
import com.gucheng.statistichelper.adapter.EditTypeAdapter
import com.gucheng.statistichelper.database.entity.ItemType
import com.gucheng.statistichelper.vm.EditTypeViewModel
import com.gucheng.statistichelper.vm.EditTypeViewModelFactory

class EditTypeActivity : AppCompatActivity(), EditTypeAdapter.TypeListener {
    private lateinit var mAdapter: EditTypeAdapter
    private val viewModel: EditTypeViewModel by viewModels {
        EditTypeViewModelFactory(
            (application as AccountApplication).typeRepository,
            (application as AccountApplication).itemRepository
        )
    }
    private var list = ArrayList<ItemType>()
    private lateinit var addBtn : Button
    private lateinit var typeEdt : EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_type)
        addBtn = findViewById(R.id.add_type)
        typeEdt = findViewById(R.id.type_name)
        setTitle(R.string.manage_type)
        val recyclerView = findViewById<RecyclerView>(R.id.type_recyclerview)
        mAdapter = EditTypeAdapter(list, this)
        recyclerView.adapter = mAdapter
        val items = viewModel.queryAllType().asLiveData()
        items.observe(owner = this) { types ->
            list.clear()
            list.addAll(types)
            mAdapter.notifyDataSetChanged()
        }
        addBtn.setOnClickListener {
            var type = typeEdt.text.toString()
            if (TextUtils.isEmpty(type)) {
                Toast.makeText(this@EditTypeActivity,R.string.type_name_not_null,Toast.LENGTH_SHORT).show()
            }
            var itemType = ItemType(typeName = type)
            viewModel.insert(itemType)
            typeEdt.setText("")
        }

    }

    override fun edit(type: ItemType) {
        var builder = AlertDialog.Builder(this)
        var view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_type, null)
        var typeEdt = view.findViewById<EditText>(R.id.item_name)
        typeEdt.setText(type.typeName)
        typeEdt.setSelection(type.typeName?.length ?: 0)
        builder.setTitle(R.string.edit_type)
            .setView(view)
            .setPositiveButton(R.string.confirm, { id, dialog ->
                type.typeName = typeEdt.text.toString()
                viewModel.updateType(type)
            }).setNegativeButton(R.string.cancel, null)
            .setNeutralButton(R.string.delete, { dialog, id ->
                delete(type)
            })
        builder.create().show()
    }

    fun delete(type: ItemType) {
        var builder = AlertDialog.Builder(this)
        builder.setPositiveButton(R.string.confirm, { dialog, which ->
            viewModel.delete(type)
        })
            .setNegativeButton(R.string.cancel, null)
            .setMessage("确认删除类型 %s 么?删除后该类型下的记录也会被删除".format(type.typeName))
        builder.create().show()
    }


}
package com.gucheng.statistichelper.fragments

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.RecyclerView
import com.gucheng.statistichelper.AccountApplication
import com.gucheng.statistichelper.R
import com.gucheng.statistichelper.adapter.EditTypeAdapter
import com.gucheng.statistichelper.database.entity.ItemType
import com.gucheng.statistichelper.vm.EditTypeViewModel
import com.gucheng.statistichelper.vm.EditTypeViewModelFactory

class EditTypeFragment : Fragment(), EditTypeAdapter.TypeListener {
    private lateinit var mAdapter: EditTypeAdapter
    private val viewModel: EditTypeViewModel by viewModels {
        EditTypeViewModelFactory(
            (requireActivity().application as AccountApplication).typeRepository,
            (requireActivity().application as AccountApplication).itemRepository
        )
    }

    private val list = ArrayList<ItemType>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.activity_edit_type, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().setTitle(R.string.manage_type)

        val addBtn = view.findViewById<Button>(R.id.add_type)
        val typeEdt = view.findViewById<EditText>(R.id.type_name)
        val recyclerView = view.findViewById<RecyclerView>(R.id.type_recyclerview)

        mAdapter = EditTypeAdapter(list, this)
        recyclerView.adapter = mAdapter

        val items = viewModel.queryAllType().asLiveData()
        items.observe(viewLifecycleOwner) { types ->
            list.clear()
            list.addAll(types)
            mAdapter.notifyDataSetChanged()
        }

        addBtn.setOnClickListener {
            val type = typeEdt.text.toString()
            if (TextUtils.isEmpty(type)) {
                Toast.makeText(requireContext(), R.string.type_name_not_null, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val itemType = ItemType(typeName = type)
            viewModel.insert(itemType)
            typeEdt.setText("")
        }
    }

    override fun edit(type: ItemType) {
        val builder = AlertDialog.Builder(requireContext())
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_type, null)
        val typeEdt = view.findViewById<EditText>(R.id.item_name)
        typeEdt.setText(type.typeName)
        typeEdt.setSelection(type.typeName?.length ?: 0)
        builder.setTitle(R.string.edit_type)
            .setView(view)
            .setPositiveButton(R.string.confirm) { _, _ ->
                type.typeName = typeEdt.text.toString()
                viewModel.updateType(type)
            }
            .setNegativeButton(R.string.cancel, null)
            .setNeutralButton(R.string.delete) { _, _ ->
                delete(type)
            }
        builder.create().show()
    }

    private fun delete(type: ItemType) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton(R.string.confirm) { _, _ ->
            viewModel.delete(type)
        }
            .setNegativeButton(R.string.cancel, null)
            .setMessage("确认删除类型 %s 么?删除后该类型下的记录也会被删除".format(type.typeName))
        builder.create().show()
    }
}

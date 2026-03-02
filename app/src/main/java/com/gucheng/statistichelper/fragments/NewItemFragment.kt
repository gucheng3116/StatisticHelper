package com.gucheng.statistichelper.fragments

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.gucheng.statistichelper.AccountApplication
import com.gucheng.statistichelper.ItemFragment
import com.gucheng.statistichelper.R
import com.gucheng.statistichelper.database.MainActivityViewModel
import com.gucheng.statistichelper.database.MainActivityViewModelFactory
import com.gucheng.statistichelper.database.entity.ItemRecord
import com.gucheng.statistichelper.database.entity.ItemType

class NewItemFragment : Fragment(), ItemFragment.TypeSelectListener {
    interface Navigator {
        fun onNewItemCreated(itemRecord: ItemRecord)
        fun openEditType()
    }

    private var navigator: Navigator? = null

    private val viewModel: MainActivityViewModel by viewModels {
        MainActivityViewModelFactory(
            (requireActivity().application as AccountApplication).itemRepository,
            (requireActivity().application as AccountApplication).typeRepository,
            (requireActivity().application as AccountApplication).dailyReportRepository,
            (requireActivity().application as AccountApplication).changeRecordRepository
        )
    }

    private lateinit var fragment: ItemFragment
    private lateinit var amountEdt: EditText
    private lateinit var typeEdt: EditText

    override fun onAttach(context: Context) {
        super.onAttach(context)
        navigator = context as? Navigator
    }

    override fun onDetach() {
        super.onDetach()
        navigator = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.activity_new_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().setTitle(R.string.new_property)

        amountEdt = view.findViewById(R.id.amount)
        typeEdt = view.findViewById(R.id.type)
        val saveBtn = view.findViewById<Button>(R.id.button_save)
        val typeEditBtn = view.findViewById<Button>(R.id.type_edit)
        val signBtn = view.findViewById<ImageView>(R.id.sign)

        saveBtn.setOnClickListener {
            val itemRecord = ItemRecord()
            Log.d("Donald", "amountEdt.text is ${amountEdt.text}, size is ${amountEdt.text.length}")
            if (TextUtils.isEmpty(amountEdt.text) || amountEdt.text.toString() == "-") {
                Toast.makeText(requireContext(), R.string.amount_cannot_be_null, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(typeEdt.text)) {
                Toast.makeText(requireContext(), R.string.type_cannot_be_null, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (viewModel.selectType == null) {
                return@setOnClickListener
            }
            itemRecord.amount = amountEdt.text.toString().toDouble()
            itemRecord.typeName = viewModel.selectType!!.typeName
            itemRecord.typeId = viewModel.selectType!!.id
            navigator?.onNewItemCreated(itemRecord)
            parentFragmentManager.popBackStack()
        }

        fragment = ItemFragment(viewModel)
        typeEdt.setOnClickListener {
            fragment.show(childFragmentManager, "select")
        }

        typeEditBtn.setOnClickListener {
            navigator?.openEditType()
        }

        signBtn.setOnClickListener {
            onSignClick()
        }
    }

    private fun onSignClick() {
        val amount = amountEdt.text.toString()
        if (amount.isNotEmpty() && amount.startsWith("-")) {
            amountEdt.setText(amount.substring(1))
        } else {
            amountEdt.setText("-$amount")
            amountEdt.setSelection(amountEdt.length())
        }
    }

    override fun typeSelect(itemType: ItemType) {
        typeEdt.setText(itemType.typeName)
        viewModel.selectType = itemType
        fragment.dismiss()
    }

    override fun editType() {
        navigator?.openEditType()
    }
}

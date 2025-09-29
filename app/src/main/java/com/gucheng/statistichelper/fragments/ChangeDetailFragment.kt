package com.gucheng.statistichelper.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.gucheng.statistichelper.AccountApplication
import com.gucheng.statistichelper.R
import com.gucheng.statistichelper.Utils
import com.gucheng.statistichelper.adapter.ChangeDetailsAdapter
import com.gucheng.statistichelper.database.entity.ChangeRecord
import com.gucheng.statistichelper.vm.ChangeDetailViewModel
import com.gucheng.statistichelper.vm.ChangeDetailViewModelFactory
import kotlinx.coroutines.launch
import kotlin.text.toDouble

class ChangeDetailFragment : Fragment() {

    private val viewModel : ChangeDetailViewModel by viewModels {
        ChangeDetailViewModelFactory(
            (requireActivity().application as AccountApplication).changeRecordRepository
        )
    }

    private var mType = -1
    private var mTypeName: String? = ""
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: ChangeDetailsAdapter
    private val mDatas = ArrayList<ChangeRecord>()
    companion object {
        fun newInstance(type: Int, typeName: String?, balance: String?) : ChangeDetailFragment {
            val fragment = ChangeDetailFragment()
            val args = Bundle()
            args.putInt(EXTRA_TYPE, type)
            args.putString(EXTRA_TYPE_NAME, typeName)
            args.putString(EXTRA_BALANCE, balance)
            fragment.arguments = args
            return fragment
        }
        const val EXTRA_TYPE = "extra_type"
        const val EXTRA_TYPE_NAME = "extra_type_name"
        const val EXTRA_BALANCE = "extra_balance"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    fun updateChangeRecords(changeRecords:List<ChangeRecord>, total:Double) {
        if (changeRecords.isEmpty()) {
            return
        }
        if (changeRecords[0].amountAfterModified == 0.0 || mType == -1) {
            changeRecords[0].amountAfterModified = total
        }
        for (i in 1 until changeRecords.size) {
            if (changeRecords[i].amountAfterModified == 0.0 || mType == -1) {
                if (i > 0) {
                    changeRecords[i].amountAfterModified = changeRecords[i - 1].amountAfterModified - (changeRecords[i-1].changeAmount?:0.0)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView = inflater.inflate(R.layout.fragment_change_detail, container, false)
        mRecyclerView = rootView.findViewById(R.id.recyclerView)
        var total: String? = null
        arguments?.let {
            mType = it.getInt(EXTRA_TYPE, -1)
            mTypeName = it.getString(EXTRA_TYPE_NAME)
            total = it.getString(EXTRA_BALANCE)
        }

//        mType = intent.getIntExtra(EXTRA_TYPE, -1)
//        mTypeName = intent.getStringExtra(EXTRA_TYPE_NAME)
        val nameTxt = rootView.findViewById<TextView>(R.id.type_name)
        val balanceTxt = rootView.findViewById<TextView>(R.id.balance)

        total?.let { balanceTxt.text = getString(R.string.current_amount,  Utils.formatAmount(it.toDouble())) }

        nameTxt.text = mTypeName
        mAdapter = ChangeDetailsAdapter(mDatas, mType)
        mRecyclerView.adapter = mAdapter
//        val scope = CoroutineScope(Job())
        viewModel.viewModelScope.launch {
            var changeRecords : List<ChangeRecord>? = null
            if (mType == -1) {
                changeRecords = viewModel.queryAllRecords2()
            } else {
                changeRecords = viewModel.queryTypeRecords2(mType)
            }
            Log.d("change_detail","changeRecords size is " + changeRecords.size)
            updateChangeRecords(changeRecords, total?.toDoubleOrNull() ?: 0.0)
            mDatas.addAll(changeRecords)

//            var balance = intent.getStringExtra(EXTRA_BALANCE)?.toDoubleOrNull()
            mRecyclerView.post {
                mAdapter.notifyDataSetChanged()
            }
        }

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().title = "收支明细"
    }


}
package com.gucheng.statistichelper.activity

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.gucheng.statistichelper.AccountApplication
import com.gucheng.statistichelper.R
import com.gucheng.statistichelper.Utils
import com.gucheng.statistichelper.adapter.ChangeDetailsAdapter
import com.gucheng.statistichelper.database.entity.ChangeRecord
import com.gucheng.statistichelper.vm.ChangeDetailViewModel
import com.gucheng.statistichelper.vm.ChangeDetailViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ChangeDetailsActivity : AppCompatActivity() {
    private var mType = -1
    private var mTypeName: String? = ""
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: ChangeDetailsAdapter
    private val mDatas = ArrayList<ChangeRecord>()

    private val viewModel : ChangeDetailViewModel by viewModels {
        ChangeDetailViewModelFactory(
            (application as AccountApplication).changeRecordRepository
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_details)
        mRecyclerView = findViewById(R.id.recyclerView)
        title = "收支明细"
        mType = intent.getIntExtra(EXTRA_TYPE, -1)
        mTypeName = intent.getStringExtra(EXTRA_TYPE_NAME)
        val nameTxt = findViewById<TextView>(R.id.type_name)
        val balanceTxt = findViewById<TextView>(R.id.balance)
        val total = intent.getStringExtra(EXTRA_BALANCE)
        total?.run { balanceTxt.text = "当前金额: " + Utils.formatAmount(total.toDouble()) }

        nameTxt.text = mTypeName
        mAdapter = ChangeDetailsAdapter(mDatas, mType)
        mRecyclerView.adapter = mAdapter
        val scope = CoroutineScope(Job())
        scope.launch {
            var changeRecords : List<ChangeRecord>? = null
            if (mType == -1) {
                changeRecords = viewModel.queryAllRecords2()
            } else {
                changeRecords = viewModel.queryTypeRecords2(mType)
            }
            if (changeRecords != null) {
                Log.d("change_detail","changeRecords size is " + changeRecords.size)
                updateChangeRecords(changeRecords, total?.toDoubleOrNull() ?: 0.0)
                mDatas.addAll(changeRecords)
            } else {
                Log.d("change_detail","changeRecords is null")
            }
//            var balance = intent.getStringExtra(EXTRA_BALANCE)?.toDoubleOrNull()
            mRecyclerView.post {
                mAdapter.notifyDataSetChanged()
            }
        }

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

    companion object {
        const val EXTRA_TYPE = "extra_type"
        const val EXTRA_TYPE_NAME = "extra_type_name"
        const val EXTRA_BALANCE = "extra_balance"
    }
}
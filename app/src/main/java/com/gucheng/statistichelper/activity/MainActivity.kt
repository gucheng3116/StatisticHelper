package com.gucheng.statistichelper.activity

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.*
import android.widget.*
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.recyclerview.widget.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.gucheng.statistichelper.AccountApplication
import com.gucheng.statistichelper.ProtocolUtil
import com.gucheng.statistichelper.ProtocolUtil.KEY_AGREE_USER_PROTOCOL
import com.gucheng.statistichelper.ProtocolUtil.KEY_VERSION_OF_AGREE_USER_PROTOCOL
import com.gucheng.statistichelper.ProtocolUtil.current_protocol_version
import com.gucheng.statistichelper.R
import com.gucheng.statistichelper.Utils
import com.gucheng.statistichelper.adapter.RecordAdapter
import com.gucheng.statistichelper.database.MainActivityViewModel
import com.gucheng.statistichelper.database.MainActivityViewModelFactory
import com.gucheng.statistichelper.database.entity.ChangeRecord
import com.gucheng.statistichelper.database.entity.ItemRecord
import com.umeng.commonsdk.UMConfigure
import com.yanzhenjie.recyclerview.*
import java.util.*
import java.util.concurrent.Executor


class MainActivity : AppCompatActivity(), RecordAdapter.ItemListener {
    val TAG = "MainActivity";
    val handler = Handler();
    private val REQUEST_CODE_NEW_ITEM = 1
    private lateinit var adapter: RecordAdapter
    lateinit var recyclerView: SwipeRecyclerView
    val mDataList: ArrayList<ItemRecord> = ArrayList()
    var amount = 0.0
    var amountView: TextView? = null

    private val viewModel: MainActivityViewModel by viewModels {
        MainActivityViewModelFactory(
            (application as AccountApplication).itemRepository,
            (application as AccountApplication).typeRepository,
            (application as AccountApplication).dailyReportRepository,
            (application as AccountApplication).changeRecordRepository
        )
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("Donald", "MainActivity onCreate ")
        setContentView(R.layout.activity_main)
        setTitle(getString(R.string.property_statistc))
        if (!ProtocolUtil.isAgreeLatestVersion(this)) {
            showUserProtocol()
        } else {
//            UMConfigure.init(
//                this,
//                Utils.UMEN_KEY,
//                Utils.APP_CHANNEL,
//                UMConfigure.DEVICE_TYPE_PHONE,
//                null
//            )
//            Bugly.init(getApplicationContext(), "0d3fd3563c", false)
        }
        val fab = findViewById<FloatingActionButton>(R.id.floatingActionButton)
        fab.setOnClickListener {
            val intent = Intent(this@MainActivity, NewItemActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_NEW_ITEM)
        }
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setSwipeMenuCreator(swipeMenuCreator)
        recyclerView.setOnItemMenuClickListener(mItemMenuClickListener)
        adapter = RecordAdapter(this, mDataList)
        val footer: View = buildFooterView()
        recyclerView.addFooterView(footer)
        recyclerView.adapter = adapter

        itemTouchHelper.attachToRecyclerView(recyclerView)

        var emptyView = findViewById<View>(R.id.empty_view)
//        viewModel.allRecords.observe(this){

//        }

        viewModel.allRecords.observe(this) { records ->
            records.let { records ->
                mDataList.clear()
                mDataList.addAll(records)
                adapter.notifyDataSetChanged()
            }
            var sum = 0.0

            adapter.setTotalAmount(records.let {
                for (item in it) {
                    if (item.amount != null) {
                        sum += item.amount!!
                    }
                }
                amount = sum
                amountView?.setText(Utils.formatAmount(amount))
                sum
            })
            if (records == null || records.isEmpty()) {
                emptyView.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                emptyView.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
        }
//        VersionChecker().checkVersion(
//            this,
//            packageManager,
//            BuildConfig.VERSION_NAME,
//            "gucheng3116",
//            "StatisticHelperRelease"
//        )
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.options_menu, menu)
        return true
    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        super.onOptionsItemSelected(item)
//        when (item.itemId) {
//            R.id.privacy_protocal -> {
//                val intent = Intent(this@MainActivity, ProtocolActivity::class.java)
//                intent.putExtra(ProtocolActivity.PROTOCOL_TYPE, ProtocolActivity.MODE_PRIVACY)
//                startActivity(intent)
//            }
//            R.id.user_protocal -> {
//                val intent = Intent(this@MainActivity, ProtocolActivity::class.java)
//                intent.putExtra(ProtocolActivity.PROTOCOL_TYPE, ProtocolActivity.MODE_PROTOCOL)
//                startActivity(intent)
//            }
//        }
//        return true
//    }

    private val executor = Executor { command -> handler.post(command) }

    @RequiresApi(Build.VERSION_CODES.N)
    fun showDialog() {
        val promptInfo: BiometricPrompt.PromptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("指纹登录") //设置大标题
            .setNegativeButtonText("取消") //设置取消按钮
            .build()


        //需要提供的参数callback
        val biometricPrompt = BiometricPrompt(this@MainActivity,
            executor, object : BiometricPrompt.AuthenticationCallback() {
                //各种异常的回调
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(
                        applicationContext,
                        "Authentication error: $errString", Toast.LENGTH_SHORT
                    )
                        .show()
                }

                //认证成功的回调
                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)
                    Log.d(TAG, "success")
                    Toast.makeText(
                        applicationContext,
                        "Authentication success: ", Toast.LENGTH_SHORT
                    )
                        .show()
                    // User has verified the signature, cipher, or message
                    // authentication code (MAC) associated with the crypto object,
                    // so you can use it in your app's crypto-driven workflows.
                }

                //认证失败的回调
                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(
                        applicationContext, "Authentication failed",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            })

        // 显示认证对话框
        biometricPrompt.authenticate(promptInfo)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (REQUEST_CODE_NEW_ITEM == requestCode && RESULT_OK == resultCode) {
            val itemRecord = data?.getParcelableExtra<ItemRecord>(NewItemActivity.EXTRA_NEW_ITEM)
            if (itemRecord != null) {
                viewModel.insertRecord(itemRecord)
                val changeRecord = ChangeRecord()
                changeRecord.changeAmount = itemRecord.amount
                changeRecord.remark = getString(R.string.new_add)
                changeRecord.amountAfterModified = itemRecord.amount?:0.0
                changeRecord.typeId = itemRecord.typeId?:-1
                changeRecord.typeName = itemRecord.typeName?:""
                viewModel.insertChangeRecord(changeRecord)

            }
        }
    }

    override fun delete(record: ItemRecord) {

        val builder = AlertDialog.Builder(this)
        builder.setMessage(R.string.confirm_delete)
            .setPositiveButton(R.string.confirm, { dialog, id ->
                viewModel.deleteTypeRecord(record)
                val changeRecord = ChangeRecord()
                changeRecord.changeAmount = -1 * (record.amount ?: 0.0)
                changeRecord.remark = getString(R.string.delete)
                changeRecord.amountAfterModified = 0.0
                changeRecord.typeId = record.typeId ?: -1
                changeRecord.typeName = record.typeName ?: ""
                viewModel.insertChangeRecord(changeRecord)
            }).setNegativeButton(R.string.cancel, null)
        val dialog = builder.create()
        dialog.show()
    }

    override fun edit(record: ItemRecord) {
        val view = LayoutInflater.from(this).inflate(R.layout.record_edit_item, null)
        val amountEdt = view.findViewById<EditText>(R.id.amount)
        val layout = view.findViewById<LinearLayout>(R.id.change_layout)
        val changeAmountText = view.findViewById<TextView>(R.id.change_amount)
        val signEdt = view.findViewById<View>(R.id.sign)
        signEdt.setOnClickListener {
            onClickSign(amountEdt)
        }
        amountEdt.setText(record.amount.toString())
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //do nothing
            }

            override fun afterTextChanged(s: Editable?) {
                layout.visibility = View.VISIBLE
                var tempAmount = 0.0
                if (TextUtils.isEmpty(amountEdt.text)) {
                    tempAmount = 0.0
                } else {
                    tempAmount = amountEdt.text.toString().toDoubleOrNull() ?: 0.0
                }
                changeAmountText.setText("变动了 " + (tempAmount - record.amount!!))
            }

        }
        amountEdt.addTextChangedListener(textWatcher)
        val typeText = view.findViewById<TextView>(R.id.type)
        typeText.setText(record.typeName)
        val builder = AlertDialog.Builder(this)
        builder.setView(view).setTitle(R.string.edit_record)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.confirm, { id, dialog ->
                val changeRecord = ChangeRecord(
                    typeId = record.typeId ?: 0,
                    typeName = record.typeName ?: "",
                    id = null,
                    remark = ""
                )
                changeRecord.amountAfterModified = amountEdt.text.toString().toDoubleOrNull() ?: 0.0
                changeRecord.changeAmount =
                    changeRecord.amountAfterModified - (record.amount ?: 0.0)
                if (changeRecord.changeAmount == 0.0) {
                    return@setPositiveButton
                }
                record.amount = amountEdt.text.toString().toDoubleOrNull() ?: 0.0
                changeRecord.remark = view.findViewById<EditText>(R.id.remark).text.toString()
                viewModel.insertChangeRecord(changeRecord)
                record.createTime = Utils.timestampToDate(System.currentTimeMillis())
                viewModel.insertRecord(record)

                Log.d(
                    "gucheng",
                    "edit thread id is " + Thread.currentThread().id
                            + ",name is " + Thread.currentThread().name
                )
            }).setNeutralButton(R.string.delete, { id, dialog ->
                delete(record)
            })
        val dialog = builder.create()
        dialog.show()

    }

    fun onClickSign(amountEdt: EditText) {
        val amount = amountEdt.text.toString()
        if (amount.length > 0 && amount.startsWith("-")) {
            amountEdt.setText(amount.substring(1))
        } else {
            amountEdt.setText("-" + amount)
            amountEdt.setSelection(amountEdt.length())
        }
    }

    private fun showUserProtocol() {
        val builder = AlertDialog.Builder(this)
        val view = LayoutInflater.from(this).inflate(R.layout.user_protocol, null);
        builder.setView(view).setCancelable(false)
        val dialog = builder.create()
        val userPromptTxt = view.findViewById<TextView>(R.id.user_hint)
        val userHint = getString(R.string.user_hint)
        val privacyString = getString(R.string.privacy_policy)
        val serviceString = getString(R.string.service_protocol)
        val privacyStart = userHint.indexOf(privacyString)
        val privacyEnd = privacyStart + privacyString.length
        val serviceStart = userHint.indexOf(serviceString)
        val serviceEnd = serviceStart + serviceString.length
        val sp = SpannableString(getString(R.string.user_hint))
        userPromptTxt.movementMethod = LinkMovementMethod.getInstance()
        sp.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(this@MainActivity, ProtocolActivity::class.java)
                intent.putExtra(ProtocolActivity.PROTOCOL_TYPE, ProtocolActivity.MODE_PRIVACY)
                startActivity(intent)
            }
        }, privacyStart, privacyEnd, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        sp.setSpan(Color.BLUE, privacyStart, privacyEnd, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        sp.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(this@MainActivity, ProtocolActivity::class.java)
                intent.putExtra(ProtocolActivity.PROTOCOL_TYPE, ProtocolActivity.MODE_PROTOCOL)
                startActivity(intent)
            }
        }, serviceStart, serviceEnd, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        sp.setSpan(Color.BLUE, serviceStart, serviceEnd, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)


        userPromptTxt.setText(sp)
        val agreeBtn = view.findViewById<Button>(R.id.agree)
        agreeBtn.setOnClickListener {
            var editor = Utils.getAppPref(this).edit()
            editor.putBoolean(KEY_AGREE_USER_PROTOCOL, true)
            editor.putInt(KEY_VERSION_OF_AGREE_USER_PROTOCOL, current_protocol_version)
            editor.apply()
            dialog.dismiss()
            UMConfigure.init(
                this,
                Utils.UMEN_KEY,
                Utils.APP_CHANNEL,
                UMConfigure.DEVICE_TYPE_PHONE,
                null
            )
//            Bugly.init(getApplicationContext(), "0d3fd3563c", false)
        }
        val cancelBtn = view.findViewById<Button>(R.id.cancel)
        cancelBtn.setOnClickListener {
            dialog.dismiss()
            finish()
        }
        dialog.show()
    }

    override fun onDestroy() {
        adapter.unRegisterListener()
        adapter.setFooterView(null)
        super.onDestroy()
    }


    val itemTouchHelperCallback = object : ItemTouchHelper.Callback() {
        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            val dragFlags: Int
            val swipeFlags: Int
            if (recyclerView.layoutManager is GridLayoutManager) {
                dragFlags =
                    ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                swipeFlags = 0
            } else {
                dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
                swipeFlags = 0
            }
            return makeMovementFlags(dragFlags, swipeFlags)
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            val fromPosition = viewHolder.adapterPosition //得到拖动ViewHolder的position
            val toPosition = target.adapterPosition //得到目标ViewHolder的position
            if (fromPosition < toPosition) {
                for (i in fromPosition until toPosition) {
                    Collections.swap(mDataList, i, i + 1)
                }
            } else {
                for (i in fromPosition downTo toPosition + 1) {
                    Collections.swap(mDataList, i, i - 1)
                }
            }
            Log.d("Donald", "fromPosition is $fromPosition, toPosition is $toPosition")
            for (i in 0 until mDataList.size) {
                viewModel.updateRecordOrder(mDataList[i], i)
            }
            adapter.notifyItemMoved(fromPosition, toPosition)
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, i: Int) {}
    }
    val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)

    val swipeMenuCreator = object : SwipeMenuCreator {
        override fun onCreateMenu(leftMenu: SwipeMenu?, rightMenu: SwipeMenu?, position: Int) {
            val width = resources.getDimensionPixelSize(R.dimen.dp_70)
            // 1. MATCH_PARENT 自适应高度，保持和Item一样高;
            val height = resources.getDimensionPixelSize(R.dimen.dp_70)
            val deleteItem: SwipeMenuItem = SwipeMenuItem(this@MainActivity)
                .setImage(R.drawable.ic_delete)
                .setWidth(width).setText("删除")
                .setHeight(height)
                .setTextColorResource(R.color.colorPrimary)
            rightMenu?.addMenuItem(deleteItem)
        }
    }

    private val mItemMenuClickListener =
        OnItemMenuClickListener { menuBridge, position ->
            menuBridge.closeMenu()
            val direction = menuBridge.direction // 左侧还是右侧菜单。
            val menuPosition = menuBridge.position // 菜单在RecyclerView的Item中的Position。
            delete(mDataList[position])
        }


    private fun buildFooterView(): View {
        var footerView: View = LayoutInflater.from(this).inflate(R.layout.record_footer, null);
        amountView = footerView.findViewById(R.id.total_amount)
        amountView?.setText(Utils.formatAmount(amount))
        amountView?.setOnClickListener {
            var intent = Intent(footerView.context, ChangeDetailsActivity::class.java)
            intent.putExtra(ChangeDetailsActivity.EXTRA_TYPE, -1)
            intent.putExtra(ChangeDetailsActivity.EXTRA_TYPE_NAME, "总资产")
            intent.putExtra(
                ChangeDetailsActivity.EXTRA_BALANCE,
                RecordAdapter.RecordViewHolder.amount.toString()
            )
            startActivity(intent)
        }

        val changeTrend: TextView = footerView.findViewById(R.id.change_trend)
        changeTrend.setOnClickListener { v ->
            val intent = Intent(v.context, KLineActivity::class.java)
            v.context.startActivity(intent)
        }
        val propertyShare: TextView = footerView.findViewById(R.id.property_share)
        propertyShare.setOnClickListener { v ->
            val intent = Intent(v.context, ShareActivity::class.java)
            v.context.startActivity(intent)
        }
        return footerView
    }


}
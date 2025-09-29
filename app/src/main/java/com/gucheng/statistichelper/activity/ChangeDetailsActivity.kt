package com.gucheng.statistichelper.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gucheng.statistichelper.R
import com.gucheng.statistichelper.fragments.ChangeDetailFragment
import com.gucheng.statistichelper.fragments.ChangeDetailFragment.Companion.EXTRA_BALANCE
import com.gucheng.statistichelper.fragments.ChangeDetailFragment.Companion.EXTRA_TYPE
import com.gucheng.statistichelper.fragments.ChangeDetailFragment.Companion.EXTRA_TYPE_NAME

class ChangeDetailsActivity : AppCompatActivity() {
    private var mType = -1
    private var mTypeName: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_details)
        if (savedInstanceState == null) {
            mType = intent.getIntExtra(EXTRA_TYPE, -1)
            mTypeName = intent.getStringExtra(EXTRA_TYPE_NAME)
        } else {
            mType = savedInstanceState.getInt(EXTRA_TYPE, -1)
            mTypeName = savedInstanceState.getString(EXTRA_TYPE_NAME)
        }
        val fragment = ChangeDetailFragment.newInstance(mType, mTypeName, intent.getStringExtra(EXTRA_BALANCE))
        supportFragmentManager.beginTransaction().add(R.id.main_content, fragment).commit()

    }
}
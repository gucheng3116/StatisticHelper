package com.gucheng.statistichelper.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.gucheng.statistichelper.R

class ProtocolActivity : AppCompatActivity() {


    companion object {
         const val PROTOCOL_TYPE = "protocol_type"
         const val MODE_PROTOCOL = 1;
         const val MODE_PRIVACY = 2;
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_protocol)
        val titleTxt = findViewById<TextView>(R.id.protocol_title)
        val contentTxt = findViewById<TextView>(R.id.protocol_content)
        val mode = intent.getIntExtra(PROTOCOL_TYPE, MODE_PROTOCOL)
        if (mode == MODE_PROTOCOL) {
            titleTxt.setText(R.string.service_protocal_title)
            contentTxt.setText(R.string.service_protocal_content)
            setTitle(R.string.service_protocal_title)
        } else {
            titleTxt.setText(R.string.privacy_policy_title)
            contentTxt.setText(R.string.privacy_policy_content)
            setTitle(R.string.privacy_policy_title)
        }

    }
}
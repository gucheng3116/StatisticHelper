package com.gucheng.statistichelper.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.gucheng.statistichelper.R

class ProtocolFragment : Fragment() {
    companion object {
        const val PROTOCOL_TYPE = "protocol_type"
        const val MODE_PROTOCOL = 1
        const val MODE_PRIVACY = 2

        fun newInstance(mode: Int): ProtocolFragment {
            return ProtocolFragment().apply {
                arguments = Bundle().apply {
                    putInt(PROTOCOL_TYPE, mode)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.activity_protocol, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val titleTxt = view.findViewById<TextView>(R.id.protocol_title)
        val contentTxt = view.findViewById<TextView>(R.id.protocol_content)
        val mode = arguments?.getInt(PROTOCOL_TYPE, MODE_PROTOCOL) ?: MODE_PROTOCOL

        if (mode == MODE_PROTOCOL) {
            titleTxt.setText(R.string.service_protocal_title)
            contentTxt.setText(R.string.service_protocal_content)
            requireActivity().setTitle(R.string.service_protocal_title)
        } else {
            titleTxt.setText(R.string.privacy_policy_title)
            contentTxt.setText(R.string.privacy_policy_content)
            requireActivity().setTitle(R.string.privacy_policy_title)
        }
    }
}

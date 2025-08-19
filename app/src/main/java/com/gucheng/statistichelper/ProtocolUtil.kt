package com.gucheng.statistichelper

import android.content.Context
import android.content.SharedPreferences

/**
 * Created on 2023/11/6.
 */
object ProtocolUtil {
    val KEY_AGREE_USER_PROTOCOL = "agree_user_protocol"
    val KEY_VERSION_OF_AGREE_USER_PROTOCOL = "version_of_agree_user_protocol"
    const val current_protocol_version = 3

    fun isAgreeLatestVersion(context: Context): Boolean {
        var prefs: SharedPreferences = Utils.getAppPref(context)
        return prefs.getInt(KEY_VERSION_OF_AGREE_USER_PROTOCOL, -1) == current_protocol_version
    }

//    private fun isAgreeUserProtocol(context: Context): Boolean {
//        var prefs: SharedPreferences = Utils.getAppPref(context: Context)
//        return prefs.getBoolean(KEY_AGREE_USER_PROTOCOL, false)
//    }
}
package com.gucheng.statistichelper

import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Double.formatAmount(): String {
    return String.format("%.2f", this)
}

// change following class to object
object Utils {

    private val APP_PREF_NAME = "statistic_helper"

    private val KEY_PREF_IGNORE_VERSION = "ignore_version"


    val APP_CHANNEL = ""
    const val UMEN_KEY = "610e49de3451547e683fecae"

    const val TIME_FORMAT = "yyyy-MM-dd HH:mm:ss"
    fun timestampToDate(milli: Long, format: String = TIME_FORMAT): String {
        var dateFormat: SimpleDateFormat = SimpleDateFormat(format, Locale.getDefault())
        return dateFormat.format(Date(milli))
    }

    fun dateToTimestamp(date: String?, format: String = TIME_FORMAT): Long {
        if (TextUtils.isEmpty(date)) {
            return 0;
        }
        var dateFormat = SimpleDateFormat(format, Locale.getDefault())
        var date2 = dateFormat.parse(date)
        return date2.time
    }

    fun getAppPref(context: Context): SharedPreferences {
        return context.getSharedPreferences(APP_PREF_NAME, Context.MODE_PRIVATE)
    }

    fun putIgnoreVersion(context: Context, versionCode: Int) {
        var editor = getAppPref(context).edit()
        editor.putInt(KEY_PREF_IGNORE_VERSION, versionCode)
        editor.apply()
    }

    fun getIgnoreVersion(context: Context): Int {
        val prefs = getAppPref(context)
        return prefs.getInt(KEY_PREF_IGNORE_VERSION, 0)
    }


    fun formatAmount(amount: Double): String {
        val decimalFormat = DecimalFormat("0.00")
        return decimalFormat.format(amount)
    }

    fun dp2px(context: Context, dp: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }

}
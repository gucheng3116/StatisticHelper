import android.content.Context
import android.content.SharedPreferences

object Utils {

    private val APP_PREF_NAME = "statistic_helper"

    private val KEY_PREF_IGNORE_VERSION = "ignore_version"

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

}
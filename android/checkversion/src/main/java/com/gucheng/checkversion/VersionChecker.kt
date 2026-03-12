package com.gucheng.checkversion

import Utils
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.widget.Toast
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class VersionChecker {

    private companion object {
        private const val TAG = "Donald"
        private const val GITHUB_API_URL =
            "https://api.github.com/repos/{owner}/{repo}/releases/latest"
    }
    var downloadUrl = ""
    lateinit var packageManager:PackageManager
    lateinit var context: Context

    @SuppressLint("CheckResult")
    fun checkVersion(ctx: Context, manager: PackageManager, currentVersionName:String,owner: String,repo: String) {
        packageManager = manager
        context = ctx
        getLatestVersion(owner, repo).observeOn(AndroidSchedulers.mainThread()).subscribe {
            Log.d(TAG, "checkVersion: $it")
            val jsonObject = JSONObject(it)
            val versionCode = jsonObject.getInt("id")
            val versionName = jsonObject.getString("tag_name")
            val assetsArray = jsonObject.optJSONArray("assets")
            if (assetsArray != null) {
                if (assetsArray.length() > 0) {
                    downloadUrl = assetsArray.optJSONObject(0).optString("browser_download_url")
                }
            }
            Log.d("Donald_download", "downloadUrl is $downloadUrl")
            if (currentVersionName.equals(versionName)) {
                Log.d(TAG, "checkVersion: 已经是最新版本")
            } else {
                val ignoreVersionCode = Utils.getIgnoreVersion(context)
                if (ignoreVersionCode != versionCode) {
                    showPopupDialog(context, versionCode)
                } else {
                    Log.d(TAG, "ignore this new version, versionCode is $versionCode")
                }
                Log.d(TAG, "checkVersion: 有新版本，快去更新吧")
//                Toast.makeText(context, "有新版本，快去更新吧", Toast.LENGTH_SHORT).show()
            }
            //popup dialog for update or not
            Log.d(TAG, "checkVersion: versionCode is $versionCode, versionName is $versionName")
        }
    }

    fun showPopupDialog(context: Context, versionCode: Int) {
        val alertDialog = AlertDialog.Builder(context)
            .setTitle("发现新版本")
            .setMessage("有新版本可用，是否立即更新？")
            .setPositiveButton("更新") { dialog, which ->
                // 点击“更新”按钮时的操作
                // 可以跳转到应用商店或下载链接来下载更新版本的APK文件
                downloadApk()
            }
            .setNegativeButton("下次再说") { dialog, which ->
                // 点击“取消”按钮时的操作
                dialog.dismiss()
            }.setNeutralButton("忽略该版本") {dialog, which ->
                Utils.putIgnoreVersion(context, versionCode)
                dialog.dismiss()
            }
        alertDialog.show()

    }

    fun downloadApk() {

        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(downloadUrl)

        if (intent.resolveActivity(packageManager) != null) {
            context.startActivity(intent)
        } else {
            Toast.makeText(context, "无法打开浏览器，请前往应用宝更新", Toast.LENGTH_SHORT).show()
        }

    }

    private fun getLatestVersion(owner: String, repo: String): Observable<String> {
        return Observable.just(1).observeOn(Schedulers.io()).map{
            val apiUrl = GITHUB_API_URL.replace("{owner}", owner).replace("{repo}", repo)
            Log.i("Donald", "name is ${Thread.currentThread().name}")
            println("name is ${Thread.currentThread().name}")
            val url = URL(apiUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }
                reader.close()
                Log.d("Donald", response.toString())
                response.toString()
            } else {
                Log.e(TAG, "HTTP error code: $responseCode")
                "error"
            }
        }.onErrorComplete()
    }


}

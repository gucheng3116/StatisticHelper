package com.gucheng.statistichelper

import android.app.Application
import android.util.Log
import com.gucheng.statistichelper.database.AccountDatabase
import com.gucheng.statistichelper.database.repository.ChangeRecordRepository
import com.gucheng.statistichelper.database.repository.DailyReportRepository
import com.gucheng.statistichelper.database.repository.ItemRecordRepository
import com.gucheng.statistichelper.database.repository.ItemTypeRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class AccountApplication : Application() {
    companion object {
        val applicationScope = CoroutineScope(SupervisorJob())
    }

    val database by lazy { AccountDatabase.getDatabase(this, applicationScope) }
    val itemRepository by lazy { ItemRecordRepository(database.itemRecordDao()) }
    val typeRepository by lazy { ItemTypeRepository(database.itemTypeDao()) }
    val dailyReportRepository by lazy { DailyReportRepository(database.dailyReportDao()) }
    val changeRecordRepository by lazy { ChangeRecordRepository(database.changeRecordDao()) }

    override fun onCreate() {
        super.onCreate()
        init()
        val name = packageManager.getPackageInfo(packageName, 0).versionName
        Log.d("Donald", "version name is $name")
//        val versionCode = packageManager.getPackageInfo(packageName, 0).versionCode
//        Log.d("Donald", "version code is $versionCode")
//        VersionChecker().checkVersion(this)
    }

    private fun init() {
//        val dailyRequest = PeriodicWorkRequestBuilder<DailyWork>(1, TimeUnit.DAYS).build()
//        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
//            "dailywork",
//            ExistingPeriodicWorkPolicy.KEEP, dailyRequest
//        )

//        UMConfigure.setLogEnabled(false)
//        UMConfigure.preInit(this@AccountApplication, "610e49de3451547e683fecae", "")
//        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO)
//        MobclickAgent.setSessionContinueMillis(100 * 1000)

    }
}
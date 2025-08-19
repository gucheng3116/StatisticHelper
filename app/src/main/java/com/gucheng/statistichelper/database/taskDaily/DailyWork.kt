package com.gucheng.statistichelper.database.taskDaily

import android.content.Context
//import androidx.work.CoroutineWorker
//import androidx.work.WorkerParameters
import com.gucheng.statistichelper.AccountApplication
import com.gucheng.statistichelper.Utils
import com.gucheng.statistichelper.database.AccountDatabase
import com.gucheng.statistichelper.database.entity.DailyReport
import kotlinx.coroutines.*
import org.json.JSONObject
import java.text.DecimalFormat
import java.util.*

class DailyWork(appContext: Context
//                , workerParameters: WorkerParameters
)
//    : CoroutineWorker(appContext, workerParameters)
{

    init {
        mContext = appContext
    }

    companion object {
        private lateinit var mContext: Context
        val MILLIS_OF_DAY = 1000 * 60 * 60 * 24
    }

//    override suspend fun doWork(): Result {
//
//        withContext(Dispatchers.IO) {
//            val calendar: Calendar = Calendar.getInstance()
//            calendar.add(Calendar.DATE, -1)
//            val itemRecordDao = AccountDatabase.getDatabase(
//                mContext,
//                AccountApplication.applicationScope
//            ).itemRecordDao()
//            val dailyReportDao = AccountDatabase.getDatabase(
//                mContext,
//                AccountApplication.applicationScope
//            ).dailyReportDao()
//            var size = getDaySize()
//
//            for (i in 1..size) {
//                var offset: Int = (i * -1).toInt()
//                var dailyReport = dailyReportDao.queryDateReport(getDate(offset))
//                if (dailyReport?.isNotEmpty()) {
//                    break;
//                }
//                var records = itemRecordDao.getAllRecordByTime(getDate(-1))
//                var sum: Double = 0.0;
//                var jsonObject = JSONObject()
//                for (item in records) {
//                    jsonObject.put(item.typeName, item.amount)
//                    sum += item.amount ?: 0.0
//                }
//                var report = DailyReport()
//                report.items = jsonObject.toString()
//                report.date = getDate(offset)
//                val format = DecimalFormat("0.##")
//                report.total = format.format(sum).toDouble()
//                dailyReportDao.insert(report)
//
//            }
//
//        }
//        return Result.success()
//    }

    fun getDate(offset: Int = 0): String {
        val calendar: Calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, offset)
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        return Utils.timestampToDate(calendar.timeInMillis)
    }

    suspend fun getDaySize(): Long {
        val itemRecordDao = AccountDatabase.getDatabase(
            mContext,
            GlobalScope
        ).itemRecordDao()
        var itemRecord = itemRecordDao.getEarlistRecord()
        var earlistTimestamp = Utils.dateToTimestamp(itemRecord.createTime)
        var now = System.currentTimeMillis()
        return (now - earlistTimestamp) / MILLIS_OF_DAY + 1
    }
}
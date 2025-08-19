package com.gucheng.statistichelper.database.repository

import android.util.Log
import androidx.annotation.WorkerThread
import com.gucheng.statistichelper.database.dao.DailyReportDao
import com.gucheng.statistichelper.database.entity.DailyReport

/**
 * Created on 2021/7/13.
 */
class DailyReportRepository(private val dailyReportDao: DailyReportDao) {


    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun queryAll(): List<DailyReport> {
        Log.d(
            "gucheng",
            "queryAll report thread id is " + Thread.currentThread().id
                    + ",name is " + Thread.currentThread().name
        )
        return dailyReportDao.queryAll()
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun queryLast10():List<DailyReport> {
        return dailyReportDao.queryLast10()
    }


    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun queryMonthlyReport() : List<DailyReport> {
        return dailyReportDao.queryMonthlyReport()
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun queryWeeklyReport() : List<DailyReport> {
        return dailyReportDao.queryWeeklyReport()
    }

    suspend fun insertDailyReport(dailyReport: DailyReport) {
        dailyReportDao.insert(dailyReport)
    }

    suspend fun update(dailyReport: DailyReport) {
        dailyReportDao.update(dailyReport)
    }

    suspend fun queryDateReport(date: String): List<DailyReport> {
        return dailyReportDao.queryDateReport(date)
    }

}
package com.gucheng.statistichelper.database.dao

import androidx.room.*
import com.gucheng.statistichelper.database.entity.DailyReport


@Dao
interface DailyReportDao {

    @Insert
    suspend fun insert(dailyReport: DailyReport)

    @Update
    suspend fun update(dailyReport: DailyReport)

    @Delete
    suspend fun delete(dailyReport: DailyReport)

    @Query("select * from daily_report")
    fun queryAll(): List<DailyReport>

    @Query("select * from (select * from daily_report order by id desc limit 10) order by date")
    suspend fun queryLast10(): List<DailyReport>

    @Query("select * from daily_report where date in (select max(date) from daily_report group by substr(date,0,8)) order by date ")
    suspend fun queryMonthlyReport(): List<DailyReport>

    @Query("select * from daily_report where date in (select max(date)  from daily_report group by strftime('%W', date)) order by date ")
    suspend fun queryWeeklyReport(): List<DailyReport>

    @Query("select * from daily_report where date=:date")
    suspend fun queryDateReport(date: String): List<DailyReport>
}
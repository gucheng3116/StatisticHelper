package com.gucheng.statistichelper.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gucheng.statistichelper.Utils


@Entity(tableName = "daily_report")
data class DailyReport(
    @PrimaryKey(autoGenerate = true) var id: Int? = null,
    var items: String? = "",
    var total: Double? = 0.0,
    var date: String? = Utils.timestampToDate(System.currentTimeMillis()),
    var createTime: String? = Utils.timestampToDate(System.currentTimeMillis())
)
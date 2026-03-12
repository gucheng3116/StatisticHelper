package com.gucheng.statistichelper.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "weekly_report")
data class WeeklyReport(@PrimaryKey(autoGenerate = true) var id: Int?,
                        var items: String?,
                        var total: Int = 0,
                        var date: Date
)
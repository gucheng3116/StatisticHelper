package com.gucheng.statistichelper.database.pojo

import androidx.room.ColumnInfo

/**
 * Created on 2021/7/28.
 */
data class MonthReport (
    @ColumnInfo(name = "month") val month:String?,

    @ColumnInfo(name = "items") val items:String?,

    @ColumnInfo(name = "total") val total:Double?,

    @ColumnInfo(name = "createTime") val createTime:String?

)
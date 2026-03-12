package com.gucheng.statistichelper.database.entity

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.gucheng.statistichelper.Utils

/**
 * Created on 2021/10/11.
 */
@Entity(tableName = "change_record")
data class ChangeRecord (
    @PrimaryKey(autoGenerate = true) var id: Int? = null,
    var createTime: String? = Utils.timestampToDate(System.currentTimeMillis()),
    var changeAmount: Double? = 0.0,
    var remark:String? = "",
    var typeId:Int = 0,
    var typeName:String = "",
    var amountAfterModified: Double = 0.0
)
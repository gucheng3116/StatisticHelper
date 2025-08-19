package com.gucheng.statistichelper.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gucheng.statistichelper.Utils

@Entity(tableName = "item_type")
data class ItemType(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    var typeName: String?,
    val createTime: String? = Utils.timestampToDate(System.currentTimeMillis())
)
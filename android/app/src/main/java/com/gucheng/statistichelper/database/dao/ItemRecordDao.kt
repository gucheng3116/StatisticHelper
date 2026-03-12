package com.gucheng.statistichelper.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gucheng.statistichelper.Utils
import com.gucheng.statistichelper.database.entity.ItemRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemRecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItemRecord(record: ItemRecord)

    @Query("select * from item_record where id in (select max(id) from item_record where isDel = 0 group by typeId) order by typeOrder,typeId")
    fun getAllRecord(): Flow<List<ItemRecord>>

    @Query("select * from item_record where id in (select max(id) from item_record group by typeId) and isDel = 0 and createTime <= :time")
    fun getAllRecordByTime(time: String = Utils.timestampToDate(System.currentTimeMillis())): List<ItemRecord>

    @Query("update item_record set isDel=1 where typeId = :typeId")
    suspend fun deleteTypeRecord(typeId: Int)

    @Query("update item_record set typeOrder=:typeOrder where id=:id")
    suspend fun updateRecordOrder(typeOrder:Int,id:Int)

    @Query("select * from item_record order by id asc limit 1")
    suspend fun getEarlistRecord():ItemRecord

    @Query("select * from item_record where id in (select max(id) from item_record where isDel = 0 and amount > 0 group by typeId) order by typeName")
    suspend fun getPositiveItems():List<ItemRecord>

    @Query("select * from item_record where id in (select max(id) from item_record where isDel = 0 and amount < 0 group by typeId) order by typeName")
    suspend fun getNegativeItems():List<ItemRecord>
}

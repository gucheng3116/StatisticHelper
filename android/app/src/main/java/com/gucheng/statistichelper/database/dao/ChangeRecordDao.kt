package com.gucheng.statistichelper.database.dao

import androidx.room.*
import com.gucheng.statistichelper.database.entity.ChangeRecord

/**
 * Created on 2021/10/14.
 */
@Dao
interface ChangeRecordDao {

    @Insert
    suspend fun insert(record:ChangeRecord)

    @Delete
    suspend fun delete(record: ChangeRecord)

    @Update
    suspend fun update(record: ChangeRecord)

    @Query("select * from change_record order by createTime desc")
    suspend fun queryRecords() : List<ChangeRecord>

    @Query("select * from change_record where typeId = :type order by createTime desc")
    suspend fun queryTypeRecords(type:Int) : List<ChangeRecord>
}
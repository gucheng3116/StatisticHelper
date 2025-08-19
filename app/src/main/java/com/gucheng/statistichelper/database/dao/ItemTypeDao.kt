package com.gucheng.statistichelper.database.dao

import androidx.room.*
import com.gucheng.statistichelper.database.entity.ItemType
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemTypeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItemType(itemType: ItemType)

    @Query("select * from item_type")
    fun getAllItem(): Flow<List<ItemType>>

    @Update
    suspend fun updateType(itemType: ItemType)

    @Delete
    suspend fun delete(itemType: ItemType)
}
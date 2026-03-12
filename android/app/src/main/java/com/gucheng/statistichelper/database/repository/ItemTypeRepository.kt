package com.gucheng.statistichelper.database.repository

import android.text.TextUtils
import androidx.annotation.WorkerThread
import com.gucheng.statistichelper.database.dao.ItemTypeDao
import com.gucheng.statistichelper.database.entity.ItemType
import kotlinx.coroutines.flow.Flow

class ItemTypeRepository(private val itemTypeDao: ItemTypeDao) {
    val allTypes: Flow<List<ItemType>> = itemTypeDao.getAllItem()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(itemType: ItemType) {
        if (TextUtils.isEmpty(itemType.typeName)) {
            return
        }
        itemTypeDao.insertItemType(itemType)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
     fun queryAll() : Flow<List<ItemType>> {
        return itemTypeDao.getAllItem()
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun updateType(itemType:ItemType) {
        return itemTypeDao.updateType(itemType)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(itemType:ItemType) {
        return itemTypeDao.delete(itemType)
    }
}
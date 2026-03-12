package com.gucheng.statistichelper.database.repository

import androidx.annotation.WorkerThread
import com.gucheng.statistichelper.database.dao.ChangeRecordDao
import com.gucheng.statistichelper.database.entity.ChangeRecord

/**
 * Created on 2021/10/15.
 */
class ChangeRecordRepository(private val changeRecordDao: ChangeRecordDao) {

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun queryRecords(): List<ChangeRecord> {
        return changeRecordDao.queryRecords()
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun queryTypeRecords(type:Int): List<ChangeRecord> {
        return changeRecordDao.queryTypeRecords(type)
    }

    suspend fun insertRecord(record: ChangeRecord) {
        return changeRecordDao.insert(record)
    }

    suspend fun deleteRecord(record: ChangeRecord) {
        return changeRecordDao.delete(record)
    }

    suspend fun updateRecord(record:ChangeRecord) {
        return changeRecordDao.update(record)
    }


}
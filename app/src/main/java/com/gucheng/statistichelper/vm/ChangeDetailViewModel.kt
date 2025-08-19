package com.gucheng.statistichelper.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gucheng.statistichelper.database.entity.ChangeRecord
import com.gucheng.statistichelper.database.repository.ChangeRecordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException
import kotlin.coroutines.suspendCoroutine

/**
 * Created on 2021/11/30.
 */
class ChangeDetailViewModel(private val changeRecordRepository: ChangeRecordRepository) : ViewModel() {
    suspend fun queryAllRecords(): List<ChangeRecord> {
        return changeRecordRepository.queryRecords()
    }

    suspend fun queryAllRecords2(): List<ChangeRecord> {
        return suspendCoroutine { continuation->
            viewModelScope.launch {
                continuation.resumeWith(Result.success(changeRecordRepository.queryRecords()))
            }
        }
    }

    suspend fun queryTypeRecords(type:Int) : List<ChangeRecord> {
        return changeRecordRepository.queryTypeRecords(type)
    }

    suspend fun queryTypeRecords2(type:Int) : List<ChangeRecord> {
        return suspendCoroutine { continuation ->
            viewModelScope.launch {
                continuation.resumeWith(Result.success(changeRecordRepository.queryTypeRecords(type)))
            }
        }
    }
}


class ChangeDetailViewModelFactory(private val changeRecordRepository: ChangeRecordRepository)
    :ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChangeDetailViewModel::class.java)) {
            return ChangeDetailViewModel(changeRecordRepository) as T
        }
        throw IllegalArgumentException("unknown model class")

    }
}
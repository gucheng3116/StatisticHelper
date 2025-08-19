package com.gucheng.statistichelper.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gucheng.statistichelper.database.repository.DailyReportRepository
import com.gucheng.statistichelper.database.repository.ItemRecordRepository
import com.gucheng.statistichelper.database.entity.ItemRecord
import kotlinx.coroutines.launch
import kotlin.coroutines.suspendCoroutine

/**
 * Created on 2021/7/19.
 */
class ShareViewModel(
    private val recordRepository: ItemRecordRepository,
    private val dailyReportRepository: DailyReportRepository
) : ViewModel() {
     suspend fun getPositiveItems():List<ItemRecord> {
         return suspendCoroutine { continuation ->
             viewModelScope.launch {
                 var result = recordRepository.getPositiveItems()
                 continuation.resumeWith(Result.success(result))
             }
         }
     }

    suspend fun getNegativeItems():List<ItemRecord> {
        return suspendCoroutine { continuation ->
            viewModelScope.launch {
                var result = recordRepository.getNegativeItems()
                for (item in result) {
                    item.amount = (item.amount?:0.0) * (-1)
                }
                continuation.resumeWith(Result.success(result))
            }
        }
    }
}

class ShareViewModelFactory(
    private val recordRepository: ItemRecordRepository,
    private val dailyReportRepository: DailyReportRepository
):ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShareViewModel::class.java)) {
            return ShareViewModel(recordRepository,dailyReportRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}


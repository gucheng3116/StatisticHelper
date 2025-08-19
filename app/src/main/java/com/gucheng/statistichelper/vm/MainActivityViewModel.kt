package com.gucheng.statistichelper.database

import android.util.Log
import androidx.lifecycle.*
import com.gucheng.statistichelper.database.entity.ChangeRecord
import com.gucheng.statistichelper.database.entity.DailyReport
import com.gucheng.statistichelper.database.entity.ItemRecord
import com.gucheng.statistichelper.database.entity.ItemType
import com.gucheng.statistichelper.database.repository.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import kotlin.coroutines.suspendCoroutine
import com.gucheng.statistichelper.Utils
import kotlinx.coroutines.withContext
import org.json.JSONArray


class MainActivityViewModel(
    private val recordRepository: ItemRecordRepository,
    private val typeRepository: ItemTypeRepository,
    private val dailyReportRepository: DailyReportRepository,
    private val changeRecordReposity: ChangeRecordRepository
) : ViewModel() {
    val allRecords: LiveData<List<ItemRecord>> = recordRepository.allRecords.asLiveData()
    val allTypes: LiveData<List<ItemType>> = typeRepository.allTypes.asLiveData()
    var selectType: ItemType? = null

    fun insertRecord(itemRecord: ItemRecord) = viewModelScope.launch {

        recordRepository.insert(itemRecord)
    }

    fun deleteTypeRecord(itemRecord: ItemRecord) = viewModelScope.launch {
        if (itemRecord.typeId != null) {
            recordRepository.deleteTypeRecord(itemRecord.typeId!!)
        }

    }

    fun insertType(itemType: ItemType) = viewModelScope.launch {
        typeRepository.insert(itemType)
    }

    fun updateRecordOrder(itemRecord: ItemRecord, order:Int) = viewModelScope.launch {
        recordRepository.updateRecordOrder(order, itemRecord.id ?: -1)
    }

    suspend fun getAll():List<DailyReport> {

        return suspendCoroutine { continuation ->
            var result:ArrayList<DailyReport> = ArrayList<DailyReport>()
            viewModelScope.launch(Dispatchers.IO) {
                result.addAll(dailyReportRepository.queryLast10())
                var records:List<ItemRecord> = recordRepository.getAllRecordByTime()
                val dailyRecord = DailyReport()
                if (records.isNotEmpty()) {
                    var sum = 0.0
                    var jsonObject = JSONObject()
                    for (itemRecord in records) {
                        sum += itemRecord.amount?:0.0
                        jsonObject.put(itemRecord.typeName,itemRecord.amount)

                    }
                    dailyRecord.createTime = Utils.timestampToDate(System.currentTimeMillis())
                    dailyRecord.date = Utils.timestampToDate(System.currentTimeMillis())
                    dailyRecord.items = jsonObject.toString()
                    dailyRecord.total = sum
                    result.add(dailyRecord)
                }

                Log.d("gucheng","result is " + result.size)
                continuation.resumeWith(Result.success(result))
            }
        }
    }

    fun insertChangeRecord(changeRecord : ChangeRecord) = viewModelScope.launch {
        changeRecordReposity.insertRecord(changeRecord)
        withContext(Dispatchers.IO) {
            // 在IO线程中执行数据库操作
            Log.d("gucheng", "insertChangeRecord thread id is " + Thread.currentThread().id
                    + ",name is " + Thread.currentThread().name)
            var records:List<ItemRecord> = recordRepository.getAllRecordByTime()
            if (records.isEmpty()) {
                // 如果没有记录，则插入一条默认的日报
                return@withContext
            }
            val jsonArray = JSONArray()
            for (item in records) {
                val obj = JSONObject()
                obj.put("typeId", item.typeId)
                obj.put("amount", item.amount)
                obj.put("typeName", item.typeName)
                jsonArray.put(obj)
            }
            val itemsJson = jsonArray.toString()
            val dailyReport = DailyReport(
                items = itemsJson,
                total = records.sumOf { it.amount ?: 0.0 },
                date = Utils.timestampToDate(System.currentTimeMillis(), "yyyy-MM-dd"),
                createTime = Utils.timestampToDate(System.currentTimeMillis())
            )

            // 获取今天的日期字符串
            val today = Utils.timestampToDate(System.currentTimeMillis(), "yyyy-MM-dd")

            // 查询今天的日报
            val todayReports = dailyReportRepository.queryDateReport(today)
            if (todayReports.isNotEmpty()) {
                // 更新已有日报
                val reportToUpdate = todayReports[0].copy(
                    items = itemsJson,
                    total = dailyReport.total,
                )
                dailyReportRepository.update(reportToUpdate)
            } else {
                // 插入新日报
                dailyReportRepository.insertDailyReport(dailyReport)
            }
        }

    }

}

class MainActivityViewModelFactory(
    private val recordRepository: ItemRecordRepository,
    private val typeRepository: ItemTypeRepository,
    private val dailyReportRepository: DailyReportRepository,
    private val changeRecordReposity: ChangeRecordRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainActivityViewModel::class.java)) {
            return MainActivityViewModel(recordRepository, typeRepository, dailyReportRepository, changeRecordReposity) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}
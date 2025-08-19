package com.gucheng.statistichelper.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gucheng.statistichelper.database.entity.ItemType
import com.gucheng.statistichelper.database.repository.ItemRecordRepository
import com.gucheng.statistichelper.database.repository.ItemTypeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

/**
 * Created on 2021/8/5.
 */
class EditTypeViewModel(private val itemTypeRepository: ItemTypeRepository,
                        private val itemRecordRepository: ItemRecordRepository) :ViewModel(){
    fun queryAllType(): Flow<List<ItemType>> {
        return itemTypeRepository.queryAll()
    }

    fun updateType(itemType:ItemType) {
        viewModelScope.launch {
            itemTypeRepository.updateType(itemType)
        }
    }

    fun delete(itemType:ItemType) {
        viewModelScope.launch {
            itemTypeRepository.delete(itemType)
            itemRecordRepository.deleteTypeRecord(itemType.id!!)
        }
    }

    fun insert(itemType:ItemType) {
        viewModelScope.launch {
            itemTypeRepository.insert(itemType)
        }
    }
}

class EditTypeViewModelFactory(private val itemTypeRepository: ItemTypeRepository,
                               private val itemRecordRepository: ItemRecordRepository)
    :ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditTypeViewModel::class.java)) {
            return EditTypeViewModel(itemTypeRepository,itemRecordRepository) as T
        }
        throw IllegalArgumentException("Unknown model class")
    }

}
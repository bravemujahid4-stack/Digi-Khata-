package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.data.local.AccountDao
import com.example.data.local.AccountTransactionEntity
import com.example.data.local.AppDatabase
import com.example.data.model.AccountEntryDto
import com.example.data.model.SyncState
import com.example.data.model.TransactionItem
import com.example.data.remote.FirebaseClient
import com.example.data.remote.FirebaseRtdbApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class AccountRepository(
    private val accountDao: AccountDao,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private var firebaseApi: FirebaseRtdbApi = FirebaseClient.createService()
    private val _syncState = MutableStateFlow(SyncState.OFFLINE)
    val syncState: StateFlow<SyncState> = _syncState.asStateFlow()

    private val _lastSyncTime = MutableStateFlow(System.currentTimeMillis())
    val lastSyncTime: StateFlow<Long> = _lastSyncTime.asStateFlow()

    val allTransactions: Flow<List<TransactionItem>> = accountDao.getAllTransactionsFlow().map { entities ->
        entities.map { it.toTransactionItem() }
    }

    val recentNames: Flow<List<String>> = accountDao.getRecentNamesFlow()

    fun updateDatabaseUrl(newUrl: String) {
        try {
            firebaseApi = FirebaseClient.createService(newUrl)
        } catch (e: Exception) {
            Log.e("AccountRepository", "Failed to update database URL", e)
        }
    }

    suspend fun syncWithRemote(): Result<Int> = withContext(dispatcher) {
        _syncState.value = SyncState.SYNCING
        try {
            val response = firebaseApi.getAllEntries()
            if (response.isSuccessful) {
                val dataMap = response.body()
                val remoteEntities = mutableListOf<AccountTransactionEntity>()

                if (dataMap != null) {
                    for ((key, dto) in dataMap) {
                        remoteEntities.add(
                            AccountTransactionEntity(
                                id = key,
                                name = dto.name,
                                amount = dto.amount,
                                handler = dto.handler,
                                type = dto.type,
                                note = dto.note ?: "",
                                timestamp = if (dto.timestamp > 0) dto.timestamp else System.currentTimeMillis(),
                                isSynced = true
                            )
                        )
                    }
                }

                // If remote is successful, update Room database
                accountDao.clearAll()
                if (remoteEntities.isNotEmpty()) {
                    accountDao.insertOrUpdateAll(remoteEntities)
                }

                _syncState.value = SyncState.LIVE
                _lastSyncTime.value = System.currentTimeMillis()
                Result.success(remoteEntities.size)
            } else {
                _syncState.value = SyncState.ERROR
                Result.failure(Exception("HTTP error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e("AccountRepository", "Sync failed", e)
            _syncState.value = SyncState.OFFLINE
            Result.failure(e)
        }
    }

    suspend fun addTransaction(
        name: String,
        amount: Double,
        handler: String,
        type: String,
        note: String,
        timestamp: Long = System.currentTimeMillis()
    ): Result<TransactionItem> = withContext(dispatcher) {
        val tempId = "local_" + UUID.randomUUID().toString().take(8)
        val localEntity = AccountTransactionEntity(
            id = tempId,
            name = name.trim(),
            amount = amount,
            handler = handler,
            type = type,
            note = note.trim(),
            timestamp = timestamp,
            isSynced = false
        )
        // Optimistically insert locally first
        accountDao.insertOrUpdate(localEntity)

        try {
            val dto = AccountEntryDto(
                name = name.trim(),
                amount = amount,
                handler = handler,
                type = type,
                note = note.trim(),
                timestamp = timestamp
            )
            val response = firebaseApi.addEntry(dto)
            if (response.isSuccessful && response.body() != null) {
                val generatedKey = response.body()!!.name
                // Replace local temporary ID with Firebase RTDB key
                accountDao.deleteById(tempId)
                val syncedEntity = localEntity.copy(id = generatedKey, isSynced = true)
                accountDao.insertOrUpdate(syncedEntity)
                _syncState.value = SyncState.LIVE
                _lastSyncTime.value = System.currentTimeMillis()
                Result.success(syncedEntity.toTransactionItem())
            } else {
                _syncState.value = SyncState.OFFLINE
                Result.success(localEntity.toTransactionItem())
            }
        } catch (e: Exception) {
            Log.e("AccountRepository", "Remote add failed, cached locally", e)
            _syncState.value = SyncState.OFFLINE
            Result.success(localEntity.toTransactionItem())
        }
    }

    suspend fun updateTransaction(item: TransactionItem): Result<Unit> = withContext(dispatcher) {
        val entity = AccountTransactionEntity.fromTransactionItem(item)
        accountDao.update(entity)

        try {
            val dto = AccountEntryDto(
                name = item.name,
                amount = item.amount,
                handler = item.handler,
                type = item.type,
                note = item.note,
                timestamp = item.timestamp
            )
            val response = firebaseApi.updateEntry(item.id, dto)
            if (response.isSuccessful) {
                accountDao.update(entity.copy(isSynced = true))
                _syncState.value = SyncState.LIVE
                _lastSyncTime.value = System.currentTimeMillis()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("AccountRepository", "Remote update failed", e)
            Result.failure(e)
        }
    }

    suspend fun deleteTransaction(id: String): Result<Unit> = withContext(dispatcher) {
        accountDao.deleteById(id)
        try {
            firebaseApi.deleteEntry(id)
            _syncState.value = SyncState.LIVE
            _lastSyncTime.value = System.currentTimeMillis()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("AccountRepository", "Remote delete failed", e)
            Result.failure(e)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: AccountRepository? = null

        fun getInstance(context: Context): AccountRepository {
            return INSTANCE ?: synchronized(this) {
                val db = AppDatabase.getDatabase(context)
                val instance = AccountRepository(db.accountDao())
                INSTANCE = instance
                instance
            }
        }
    }
}

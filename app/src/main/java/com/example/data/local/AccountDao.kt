package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {

    @Query("SELECT * FROM account_transactions ORDER BY timestamp DESC")
    fun getAllTransactionsFlow(): Flow<List<AccountTransactionEntity>>

    @Query("SELECT * FROM account_transactions ORDER BY timestamp DESC")
    suspend fun getAllTransactions(): List<AccountTransactionEntity>

    @Query("SELECT * FROM account_transactions WHERE isSynced = 0")
    suspend fun getUnsyncedTransactions(): List<AccountTransactionEntity>

    @Query("SELECT DISTINCT name FROM account_transactions WHERE name != '' ORDER BY timestamp DESC LIMIT 20")
    fun getRecentNamesFlow(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(transaction: AccountTransactionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAll(transactions: List<AccountTransactionEntity>)

    @Update
    suspend fun update(transaction: AccountTransactionEntity)

    @Query("DELETE FROM account_transactions WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM account_transactions")
    suspend fun clearAll()
}

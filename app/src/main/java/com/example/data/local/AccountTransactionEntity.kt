package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.TransactionItem

@Entity(tableName = "account_transactions")
data class AccountTransactionEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val amount: Double,
    val handler: String,
    val type: String,
    val note: String,
    val timestamp: Long,
    val isSynced: Boolean = true
) {
    fun toTransactionItem(): TransactionItem {
        return TransactionItem(
            id = id,
            name = name,
            amount = amount,
            handler = handler,
            type = type,
            note = note,
            timestamp = timestamp,
            isSynced = isSynced
        )
    }

    companion object {
        fun fromTransactionItem(item: TransactionItem): AccountTransactionEntity {
            return AccountTransactionEntity(
                id = item.id,
                name = item.name,
                amount = item.amount,
                handler = item.handler,
                type = item.type,
                note = item.note,
                timestamp = item.timestamp,
                isSynced = item.isSynced
            )
        }
    }
}

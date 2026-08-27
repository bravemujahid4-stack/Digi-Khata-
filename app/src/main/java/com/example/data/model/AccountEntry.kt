package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AccountEntryDto(
    @Json(name = "name") val name: String = "",
    @Json(name = "amount") val amount: Double = 0.0,
    @Json(name = "handler") val handler: String = "Munawar",
    @Json(name = "type") val type: String = "GOT",
    @Json(name = "note") val note: String? = "",
    @Json(name = "timestamp") val timestamp: Long = System.currentTimeMillis()
)

@JsonClass(generateAdapter = true)
data class PushResponse(
    @Json(name = "name") val name: String
)

enum class HandlerType(val displayName: String) {
    MUNAWAR("Munawar"),
    KHALID("Khalid");

    companion object {
        fun fromString(value: String): HandlerType {
            return if (value.equals("Khalid", ignoreCase = true)) KHALID else MUNAWAR
        }
    }
}

enum class EntryType(val displayName: String, val label: String) {
    GOT("GOT", "Cash In (GOT)"),
    GAVE("GAVE", "Cash Out (GAVE)");

    companion object {
        fun fromString(value: String): EntryType {
            return if (value.equals("GAVE", ignoreCase = true)) GAVE else GOT
        }
    }
}

data class TransactionItem(
    val id: String,
    val name: String,
    val amount: Double,
    val handler: String,
    val type: String,
    val note: String,
    val timestamp: Long,
    val isSynced: Boolean = true
) {
    val isCashIn: Boolean
        get() = type.equals("GOT", ignoreCase = true)

    val formattedAmount: String
        get() = "Rs. %,.0f".format(amount)
}

data class LedgerBalances(
    val munawarBalance: Double = 0.0,
    val khalidBalance: Double = 0.0,
    val totalBalance: Double = 0.0,
    val totalGotAmount: Double = 0.0,
    val totalGaveAmount: Double = 0.0,
    val totalEntriesCount: Int = 0,
    val munawarEntriesCount: Int = 0,
    val khalidEntriesCount: Int = 0
)

enum class SyncState {
    LIVE,
    SYNCING,
    OFFLINE,
    ERROR
}

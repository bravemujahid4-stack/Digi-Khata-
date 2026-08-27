package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.EntryType
import com.example.data.model.HandlerType
import com.example.data.model.LedgerBalances
import com.example.data.model.SyncState
import com.example.data.model.TransactionItem
import com.example.data.remote.FirebaseClient
import com.example.data.repository.AccountRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class SortOption(val title: String) {
    NEWEST("Newest First"),
    OLDEST("Oldest First"),
    HIGHEST_AMOUNT("Highest Amount"),
    LOWEST_AMOUNT("Lowest Amount")
}

enum class DateFilter(val title: String) {
    ALL("All Time"),
    TODAY("Today"),
    THIS_WEEK("This Week"),
    THIS_MONTH("This Month")
}

sealed class UiEvent {
    data class ShowSnackbar(val message: String) : UiEvent()
    data class ShareReport(val text: String) : UiEvent()
}

class AccountViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AccountRepository.getInstance(application)

    val syncState: StateFlow<SyncState> = repository.syncState
    val lastSyncTime: StateFlow<Long> = repository.lastSyncTime
    val recentNames: StateFlow<List<String>> = repository.recentNames.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allTransactions: StateFlow<List<TransactionItem>> = repository.allTransactions.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Filter and Search states
    val searchQuery = MutableStateFlow("")
    val selectedHandlerFilter = MutableStateFlow<String?>(null) // null = all, "Munawar", "Khalid"
    val selectedTypeFilter = MutableStateFlow<String?>(null) // null = all, "GOT", "GAVE"
    val selectedDateFilter = MutableStateFlow(DateFilter.ALL)
    val selectedSortOption = MutableStateFlow(SortOption.NEWEST)

    // Form inputs state
    val formCustName = MutableStateFlow("")
    val formAmount = MutableStateFlow("")
    val formHandler = MutableStateFlow(HandlerType.MUNAWAR)
    val formType = MutableStateFlow(EntryType.GOT)
    val formNote = MutableStateFlow("")
    val isFormSubmitting = MutableStateFlow(false)

    // Active Edit Dialog Transaction State
    val editingTransaction = MutableStateFlow<TransactionItem?>(null)
    val deletingTransaction = MutableStateFlow<TransactionItem?>(null)
    val showAnalyticsDialog = MutableStateFlow(false)
    val showConfigDialog = MutableStateFlow(false)

    private val _uiEvents = MutableSharedFlow<UiEvent>()
    val uiEvents: SharedFlow<UiEvent> = _uiEvents.asSharedFlow()

    private var pollingJob: Job? = null

    data class FilterCriteria(
        val query: String = "",
        val handler: String? = null,
        val type: String? = null,
        val dateFilter: DateFilter = DateFilter.ALL,
        val sortOption: SortOption = SortOption.NEWEST
    )

    private val filterCriteria: Flow<FilterCriteria> = combine(
        searchQuery,
        selectedHandlerFilter,
        selectedTypeFilter,
        selectedDateFilter,
        selectedSortOption
    ) { query, handler, type, dateFilter, sortOption ->
        FilterCriteria(query, handler, type, dateFilter, sortOption)
    }

    // Balances calculation directly derived from all transactions
    val ledgerBalances: StateFlow<LedgerBalances> = allTransactions.map { txList ->
        var mCash = 0.0
        var kCash = 0.0
        var gotTotal = 0.0
        var gaveTotal = 0.0
        var mCount = 0
        var kCount = 0

        for (tx in txList) {
            val isGot = tx.type.equals("GOT", ignoreCase = true)
            val amt = tx.amount
            if (isGot) gotTotal += amt else gaveTotal += amt

            if (tx.handler.equals("Munawar", ignoreCase = true)) {
                mCount++
                if (isGot) mCash += amt else mCash -= amt
            } else if (tx.handler.equals("Khalid", ignoreCase = true)) {
                kCount++
                if (isGot) kCash += amt else kCash -= amt
            }
        }

        LedgerBalances(
            munawarBalance = mCash,
            khalidBalance = kCash,
            totalBalance = mCash + kCash,
            totalGotAmount = gotTotal,
            totalGaveAmount = gaveTotal,
            totalEntriesCount = txList.size,
            munawarEntriesCount = mCount,
            khalidEntriesCount = kCount
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LedgerBalances()
    )

    // Filtered and Sorted list for UI feed
    val filteredTransactions: StateFlow<List<TransactionItem>> = combine(
        allTransactions,
        filterCriteria
    ) { txList, criteria ->
        var result = txList

        // Search query
        if (criteria.query.isNotBlank()) {
            val q = criteria.query.trim().lowercase(Locale.ROOT)
            result = result.filter {
                it.name.lowercase(Locale.ROOT).contains(q) ||
                        it.note.lowercase(Locale.ROOT).contains(q) ||
                        it.formattedAmount.lowercase(Locale.ROOT).contains(q)
            }
        }

        // Handler filter
        if (criteria.handler != null) {
            result = result.filter { it.handler.equals(criteria.handler, ignoreCase = true) }
        }

        // Type filter
        if (criteria.type != null) {
            result = result.filter { it.type.equals(criteria.type, ignoreCase = true) }
        }

        // Date filter
        if (criteria.dateFilter != DateFilter.ALL) {
            val startOfToday = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            val startOfWeek = Calendar.getInstance().apply {
                set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            val startOfMonth = Calendar.getInstance().apply {
                set(Calendar.DAY_OF_MONTH, 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            result = when (criteria.dateFilter) {
                DateFilter.TODAY -> result.filter { it.timestamp >= startOfToday }
                DateFilter.THIS_WEEK -> result.filter { it.timestamp >= startOfWeek }
                DateFilter.THIS_MONTH -> result.filter { it.timestamp >= startOfMonth }
                DateFilter.ALL -> result
            }
        }

        // Sorting
        when (criteria.sortOption) {
            SortOption.NEWEST -> result.sortedByDescending { it.timestamp }
            SortOption.OLDEST -> result.sortedBy { it.timestamp }
            SortOption.HIGHEST_AMOUNT -> result.sortedByDescending { it.amount }
            SortOption.LOWEST_AMOUNT -> result.sortedBy { it.amount }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        // Initial sync and start background periodic polling for live updates
        syncNow()
        startLivePolling()
    }

    fun startLivePolling() {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            while (isActive) {
                delay(12000) // Poll every 12 seconds for seamless multi-device updates
                repository.syncWithRemote()
            }
        }
    }

    fun syncNow() {
        viewModelScope.launch {
            val result = repository.syncWithRemote()
            if (result.isSuccess) {
                _uiEvents.emit(UiEvent.ShowSnackbar("Synced live with server (${result.getOrDefault(0)} entries)"))
            } else {
                _uiEvents.emit(UiEvent.ShowSnackbar("Offline mode: Using cached records"))
            }
        }
    }

    fun onAddQuickAmount(addAmount: Double) {
        val current = formAmount.value.toDoubleOrNull() ?: 0.0
        val newAmount = current + addAmount
        formAmount.value = if (newAmount % 1 == 0.0) newAmount.toInt().toString() else newAmount.toString()
    }

    fun submitNewEntry() {
        val name = formCustName.value.trim()
        val amountStr = formAmount.value.trim()
        val amount = amountStr.toDoubleOrNull()

        if (name.isBlank()) {
            viewModelScope.launch { _uiEvents.emit(UiEvent.ShowSnackbar("Please enter Customer / Supplier Name")) }
            return
        }

        if (amount == null || amount <= 0) {
            viewModelScope.launch { _uiEvents.emit(UiEvent.ShowSnackbar("Please enter a valid positive Amount")) }
            return
        }

        viewModelScope.launch {
            isFormSubmitting.value = true
            val handler = formHandler.value.displayName
            val type = formType.value.displayName
            val note = formNote.value.trim()

            val result = repository.addTransaction(
                name = name,
                amount = amount,
                handler = handler,
                type = type,
                note = note,
                timestamp = System.currentTimeMillis()
            )

            isFormSubmitting.value = false
            if (result.isSuccess) {
                // Reset form fields
                formCustName.value = ""
                formAmount.value = ""
                formNote.value = ""
                _uiEvents.emit(UiEvent.ShowSnackbar("Saved entry for $name (Rs. ${"%,.0f".format(amount)})"))
            } else {
                _uiEvents.emit(UiEvent.ShowSnackbar("Saved locally (will sync when online)"))
            }
        }
    }

    fun deleteTransaction(item: TransactionItem) {
        viewModelScope.launch {
            repository.deleteTransaction(item.id)
            deletingTransaction.value = null
            _uiEvents.emit(UiEvent.ShowSnackbar("Deleted entry for ${item.name}"))
        }
    }

    fun saveEditedTransaction(
        id: String,
        name: String,
        amount: Double,
        handler: String,
        type: String,
        note: String,
        timestamp: Long
    ) {
        viewModelScope.launch {
            val updated = TransactionItem(
                id = id,
                name = name.trim(),
                amount = amount,
                handler = handler,
                type = type,
                note = note.trim(),
                timestamp = timestamp
            )
            repository.updateTransaction(updated)
            editingTransaction.value = null
            _uiEvents.emit(UiEvent.ShowSnackbar("Updated entry for ${updated.name}"))
        }
    }

    fun updateCustomDatabaseUrl(url: String) {
        repository.updateDatabaseUrl(url)
        syncNow()
    }

    fun shareLedgerReport(context: Context) {
        val balances = ledgerBalances.value
        val txList = allTransactions.value
        val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        val dateStr = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())

        val sb = StringBuilder()
        sb.append("📊 *MAS ACCOUNTS - DAILY LEDGER REPORT*\n")
        sb.append("📅 Date: $dateStr\n")
        sb.append("──────────────────────\n")
        sb.append("💼 *Munawar Cash:* Rs. %,.0f\n".format(balances.munawarBalance))
        sb.append("💼 *Khalid Cash:* Rs. %,.0f\n".format(balances.khalidBalance))
        sb.append("💰 *Total Cash in Hand:* Rs. %,.0f\n".format(balances.totalBalance))
        sb.append("──────────────────────\n")
        sb.append("📈 Total Received (GOT): Rs. %,.0f\n".format(balances.totalGotAmount))
        sb.append("📉 Total Given (GAVE): Rs. %,.0f\n".format(balances.totalGaveAmount))
        sb.append("📝 Total Entries: ${balances.totalEntriesCount}\n\n")

        sb.append("📜 *Recent Transactions:*\n")
        val recentItems = txList.take(15)
        if (recentItems.isEmpty()) {
            sb.append("No transactions recorded yet.\n")
        } else {
            for (tx in recentItems) {
                val icon = if (tx.isCashIn) "🟢" else "🔴"
                val timeStr = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(tx.timestamp))
                sb.append("$icon *${tx.name}* | ${tx.type} | Rs. %,.0f\n".format(tx.amount))
                sb.append("   👤 ${tx.handler} • 🕒 $timeStr")
                if (tx.note.isNotBlank()) sb.append(" • 💬 ${tx.note}")
                sb.append("\n")
            }
        }
        sb.append("\n_Generated via MAS Accounts Multi-Device App_")

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, sb.toString())
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share Ledger Summary")
        context.startActivity(shareIntent)
    }

    fun shareTransactionSlip(context: Context, item: TransactionItem) {
        val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        val dateStr = dateFormat.format(Date(item.timestamp))

        val text = buildString {
            append("🧾 *MAS ACCOUNTS RECEIPT / SLIP*\n")
            append("──────────────────────\n")
            append("👤 *Customer / Supplier:* ${item.name}\n")
            append("💵 *Amount:* Rs. %,.0f\n".format(item.amount))
            append("🏷️ *Type:* ${if (item.isCashIn) "Cash In (GOT)" else "Cash Out (GAVE)"}\n")
            append("👨‍💼 *Handler:* ${item.handler}\n")
            append("🕒 *Date & Time:* $dateStr\n")
            if (item.note.isNotBlank()) {
                append("📝 *Note:* ${item.note}\n")
            }
            append("──────────────────────\n")
            append("Status: Verified in Multi-Device Ledger\n")
        }

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share Transaction Slip")
        context.startActivity(shareIntent)
    }
}

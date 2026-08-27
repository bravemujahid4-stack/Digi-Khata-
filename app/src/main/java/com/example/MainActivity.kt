package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.EntryType
import com.example.data.model.HandlerType
import com.example.data.model.TransactionItem
import com.example.ui.components.AnalyticsSummaryDialog
import com.example.ui.components.BalanceSummaryCards
import com.example.ui.components.DatabaseConfigDialog
import com.example.ui.components.DeleteConfirmDialog
import com.example.ui.components.HeaderSection
import com.example.ui.components.NewEntryCard
import com.example.ui.components.TransactionDetailDialog
import com.example.ui.components.TransactionFeed
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AccountViewModel
import com.example.ui.viewmodel.UiEvent

class MainActivity : ComponentActivity() {

    private val viewModel: AccountViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainScreen(viewModel: AccountViewModel) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    // State Collection
    val syncState by viewModel.syncState.collectAsStateWithLifecycle()
    val ledgerBalances by viewModel.ledgerBalances.collectAsStateWithLifecycle()
    val filteredTransactions by viewModel.filteredTransactions.collectAsStateWithLifecycle()
    val allTransactions by viewModel.allTransactions.collectAsStateWithLifecycle()
    val recentNames by viewModel.recentNames.collectAsStateWithLifecycle()

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedHandlerFilter by viewModel.selectedHandlerFilter.collectAsStateWithLifecycle()
    val selectedTypeFilter by viewModel.selectedTypeFilter.collectAsStateWithLifecycle()
    val selectedDateFilter by viewModel.selectedDateFilter.collectAsStateWithLifecycle()
    val selectedSortOption by viewModel.selectedSortOption.collectAsStateWithLifecycle()

    val formCustName by viewModel.formCustName.collectAsStateWithLifecycle()
    val formAmount by viewModel.formAmount.collectAsStateWithLifecycle()
    val formHandler by viewModel.formHandler.collectAsStateWithLifecycle()
    val formType by viewModel.formType.collectAsStateWithLifecycle()
    val formNote by viewModel.formNote.collectAsStateWithLifecycle()
    val isFormSubmitting by viewModel.isFormSubmitting.collectAsStateWithLifecycle()

    val editingTransaction by viewModel.editingTransaction.collectAsStateWithLifecycle()
    val deletingTransaction by viewModel.deletingTransaction.collectAsStateWithLifecycle()
    val showAnalyticsDialog by viewModel.showAnalyticsDialog.collectAsStateWithLifecycle()
    val showConfigDialog by viewModel.showConfigDialog.collectAsStateWithLifecycle()

    // Handle UI Events
    LaunchedEffect(viewModel) {
        viewModel.uiEvents.collect { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is UiEvent.ShareReport -> {
                    viewModel.shareLedgerReport(context)
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 720.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1. Header (MAS ACCOUNTS, Live Sync Status, Actions)
                HeaderSection(
                    syncState = syncState,
                    onRefreshClick = { viewModel.syncNow() },
                    onAnalyticsClick = { viewModel.showAnalyticsDialog.value = true },
                    onShareClick = { viewModel.shareLedgerReport(context) },
                    onSettingsClick = { viewModel.showConfigDialog.value = true }
                )

                // 2. Summary Balance Cards (Munawar, Khalid, Total)
                BalanceSummaryCards(
                    balances = ledgerBalances,
                    onMunawarCardClick = {
                        viewModel.selectedHandlerFilter.value =
                            if (selectedHandlerFilter == "Munawar") null else "Munawar"
                    },
                    onKhalidCardClick = {
                        viewModel.selectedHandlerFilter.value =
                            if (selectedHandlerFilter == "Khalid") null else "Khalid"
                    },
                    onTotalCardClick = {
                        viewModel.selectedHandlerFilter.value = null
                        viewModel.selectedTypeFilter.value = null
                    }
                )

                // 3. New Entry Card (Name, Amount, Handler, Type, Note, Submit)
                NewEntryCard(
                    custName = formCustName,
                    onCustNameChange = { viewModel.formCustName.value = it },
                    amount = formAmount,
                    onAmountChange = { viewModel.formAmount.value = it },
                    handler = formHandler,
                    onHandlerChange = { viewModel.formHandler.value = it },
                    entryType = formType,
                    onEntryTypeChange = { viewModel.formType.value = it },
                    note = formNote,
                    onNoteChange = { viewModel.formNote.value = it },
                    recentNames = recentNames,
                    onQuickAmountAdd = { viewModel.onAddQuickAmount(it) },
                    onSubmit = { viewModel.submitNewEntry() },
                    isSubmitting = isFormSubmitting
                )

                // 4. Live Entries Feed (Search, Filters, Sort, Transactions Table/Cards)
                TransactionFeed(
                    transactions = filteredTransactions,
                    totalCount = allTransactions.size,
                    searchQuery = searchQuery,
                    onSearchQueryChange = { viewModel.searchQuery.value = it },
                    selectedHandlerFilter = selectedHandlerFilter,
                    onHandlerFilterChange = { viewModel.selectedHandlerFilter.value = it },
                    selectedTypeFilter = selectedTypeFilter,
                    onTypeFilterChange = { viewModel.selectedTypeFilter.value = it },
                    selectedDateFilter = selectedDateFilter,
                    onDateFilterChange = { viewModel.selectedDateFilter.value = it },
                    selectedSortOption = selectedSortOption,
                    onSortOptionChange = { viewModel.selectedSortOption.value = it },
                    onItemClick = { item -> viewModel.editingTransaction.value = item },
                    onEditClick = { item -> viewModel.editingTransaction.value = item },
                    onDeleteClick = { item -> viewModel.deletingTransaction.value = item },
                    onShareSlipClick = { item -> viewModel.shareTransactionSlip(context, item) }
                )

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }

    // Dialogs
    editingTransaction?.let { item ->
        TransactionDetailDialog(
            item = item,
            onDismiss = { viewModel.editingTransaction.value = null },
            onEditSave = { name, amount, handler, type, note ->
                viewModel.saveEditedTransaction(
                    id = item.id,
                    name = name,
                    amount = amount,
                    handler = handler,
                    type = type,
                    note = note,
                    timestamp = item.timestamp
                )
            },
            onDelete = {
                viewModel.editingTransaction.value = null
                viewModel.deletingTransaction.value = item
            },
            onShare = {
                viewModel.shareTransactionSlip(context, item)
            }
        )
    }

    deletingTransaction?.let { item ->
        DeleteConfirmDialog(
            item = item,
            onConfirm = { viewModel.deleteTransaction(item) },
            onDismiss = { viewModel.deletingTransaction.value = null }
        )
    }

    if (showAnalyticsDialog) {
        AnalyticsSummaryDialog(
            balances = ledgerBalances,
            transactions = allTransactions,
            onDismiss = { viewModel.showAnalyticsDialog.value = false },
            onShareReport = {
                viewModel.showAnalyticsDialog.value = false
                viewModel.shareLedgerReport(context)
            }
        )
    }

    if (showConfigDialog) {
        DatabaseConfigDialog(
            onSaveUrl = { newUrl ->
                viewModel.updateCustomDatabaseUrl(newUrl)
            },
            onDismiss = { viewModel.showConfigDialog.value = false }
        )
    }
}

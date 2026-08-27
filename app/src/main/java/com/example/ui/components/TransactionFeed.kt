package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TransactionItem
import com.example.ui.theme.KhalidCardBg
import com.example.ui.theme.KhalidCardText
import com.example.ui.theme.MunawarCardBg
import com.example.ui.theme.MunawarCardText
import com.example.ui.theme.VibrantBorder
import com.example.ui.theme.VibrantGreen
import com.example.ui.theme.VibrantGreenBg
import com.example.ui.theme.VibrantGreenText
import com.example.ui.theme.VibrantNavyContainer
import com.example.ui.theme.VibrantNavyDark
import com.example.ui.theme.VibrantNavyPrimary
import com.example.ui.theme.VibrantRose
import com.example.ui.theme.VibrantRoseBg
import com.example.ui.theme.VibrantRoseText
import com.example.ui.theme.VibrantSurface
import com.example.ui.theme.VibrantSurfaceVariant
import com.example.ui.theme.VibrantTextMuted
import com.example.ui.theme.VibrantTextSecondary
import com.example.ui.viewmodel.DateFilter
import com.example.ui.viewmodel.SortOption
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TransactionFeed(
    transactions: List<TransactionItem>,
    totalCount: Int,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedHandlerFilter: String?,
    onHandlerFilterChange: (String?) -> Unit,
    selectedTypeFilter: String?,
    onTypeFilterChange: (String?) -> Unit,
    selectedDateFilter: DateFilter,
    onDateFilterChange: (DateFilter) -> Unit,
    selectedSortOption: SortOption,
    onSortOptionChange: (SortOption) -> Unit,
    onItemClick: (TransactionItem) -> Unit,
    onEditClick: (TransactionItem) -> Unit,
    onDeleteClick: (TransactionItem) -> Unit,
    onShareSlipClick: (TransactionItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var showSortMenu by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("transaction_feed_card"),
        shape = RoundedCornerShape(24.dp),
        color = VibrantSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, VibrantBorder),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Live Entries Feed",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = VibrantNavyDark,
                        fontSize = 17.sp
                    )
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = VibrantNavyContainer,
                    modifier = Modifier.testTag("entry_count_badge")
                ) {
                    Text(
                        text = "$totalCount entries",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = VibrantNavyDark
                        )
                    )
                }
            }

            // Search Bar & Sort Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("feed_search_input"),
                    placeholder = { Text("Search by name, note, amount...", fontSize = 13.sp, color = VibrantTextMuted) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = VibrantTextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear search", tint = VibrantTextSecondary, modifier = Modifier.size(18.dp))
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VibrantNavyPrimary,
                        unfocusedBorderColor = VibrantBorder,
                        focusedTextColor = VibrantNavyDark,
                        unfocusedTextColor = VibrantNavyDark
                    )
                )

                // Sort Button
                Box {
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { showSortMenu = true }
                            .testTag("feed_sort_button"),
                        shape = RoundedCornerShape(12.dp),
                        color = VibrantSurfaceVariant
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = "Sort",
                                tint = VibrantNavyDark,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false }
                    ) {
                        SortOption.values().forEach { sort ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = sort.title,
                                        fontWeight = if (sort == selectedSortOption) FontWeight.Bold else FontWeight.Normal,
                                        color = VibrantNavyDark
                                    )
                                },
                                onClick = {
                                    onSortOptionChange(sort)
                                    showSortMenu = false
                                }
                            )
                        }
                    }
                }
            }

            // Filter Chips
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // All Filter
                FilterChipItem(
                    label = "All",
                    isSelected = selectedHandlerFilter == null && selectedTypeFilter == null,
                    selectedBg = VibrantNavyDark,
                    selectedTextColor = Color.White,
                    onClick = {
                        onHandlerFilterChange(null)
                        onTypeFilterChange(null)
                    },
                    testTag = "filter_all"
                )

                // Handler Filters
                FilterChipItem(
                    label = "Munawar",
                    isSelected = selectedHandlerFilter == "Munawar",
                    selectedBg = MunawarCardBg,
                    selectedTextColor = MunawarCardText,
                    onClick = {
                        onHandlerFilterChange(if (selectedHandlerFilter == "Munawar") null else "Munawar")
                    },
                    testTag = "filter_munawar"
                )

                FilterChipItem(
                    label = "Khalid",
                    isSelected = selectedHandlerFilter == "Khalid",
                    selectedBg = KhalidCardBg,
                    selectedTextColor = KhalidCardText,
                    onClick = {
                        onHandlerFilterChange(if (selectedHandlerFilter == "Khalid") null else "Khalid")
                    },
                    testTag = "filter_khalid"
                )

                // Type Filters
                FilterChipItem(
                    label = "GOT (In)",
                    isSelected = selectedTypeFilter == "GOT",
                    selectedBg = VibrantGreenBg,
                    selectedTextColor = VibrantGreenText,
                    onClick = {
                        onTypeFilterChange(if (selectedTypeFilter == "GOT") null else "GOT")
                    },
                    testTag = "filter_got"
                )

                FilterChipItem(
                    label = "GAVE (Out)",
                    isSelected = selectedTypeFilter == "GAVE",
                    selectedBg = VibrantRoseBg,
                    selectedTextColor = VibrantRoseText,
                    onClick = {
                        onTypeFilterChange(if (selectedTypeFilter == "GAVE") null else "GAVE")
                    },
                    testTag = "filter_gave"
                )
            }

            // Date Range Filter Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                DateFilter.values().forEach { filter ->
                    DateFilterChipItem(
                        filter = filter,
                        isSelected = filter == selectedDateFilter,
                        onClick = { onDateFilterChange(filter) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Divider(color = VibrantBorder, thickness = 1.dp)

            // Table / Card List of Transactions
            if (transactions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp)
                        .testTag("empty_feed_state"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ReceiptLong,
                            contentDescription = null,
                            tint = VibrantTextMuted,
                            modifier = Modifier.size(40.dp)
                        )
                        Text(
                            text = if (searchQuery.isNotEmpty() || selectedHandlerFilter != null || selectedTypeFilter != null)
                                "No records found matching filters."
                            else
                                "No transactions recorded yet.",
                            style = MaterialTheme.typography.bodyMedium.copy(color = VibrantTextSecondary),
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.testTag("transactions_list")
                ) {
                    transactions.forEach { item ->
                        TransactionRowItem(
                            item = item,
                            onClick = { onItemClick(item) },
                            onEdit = { onEditClick(item) },
                            onDelete = { onDeleteClick(item) },
                            onShare = { onShareSlipClick(item) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FilterChipItem(
    label: String,
    isSelected: Boolean,
    selectedBg: Color = VibrantNavyDark,
    selectedTextColor: Color = Color.White,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .testTag(testTag),
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) selectedBg else VibrantSurfaceVariant
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) selectedTextColor else VibrantNavyDark
        )
    }
}

@Composable
fun DateFilterChipItem(
    filter: DateFilter,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .testTag("date_filter_${filter.name}"),
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) VibrantNavyDark else Color.Transparent
    ) {
        Text(
            text = filter.title,
            modifier = Modifier.padding(vertical = 5.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color.White else VibrantTextSecondary
        )
    }
}

@Composable
fun TransactionRowItem(
    item: TransactionItem,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isMunawar = item.handler.equals("Munawar", ignoreCase = true)
    val timeFormatted = remember(item.timestamp) {
        val date = Date(item.timestamp)
        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(date)
    }
    val dateFormatted = remember(item.timestamp) {
        val date = Date(item.timestamp)
        SimpleDateFormat("dd MMM", Locale.getDefault()).format(date)
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag("transaction_row_${item.id}"),
        shape = RoundedCornerShape(16.dp),
        color = VibrantSurfaceVariant.copy(alpha = 0.65f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left Column: Customer name & Note & Date
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = VibrantNavyDark,
                            fontSize = 15.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Handler Badge
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isMunawar) MunawarCardBg else KhalidCardBg
                    ) {
                        Text(
                            text = item.handler,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isMunawar) MunawarCardText else KhalidCardText
                        )
                    }
                }

                if (item.note.isNotBlank()) {
                    Text(
                        text = item.note,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = VibrantTextSecondary,
                            fontSize = 11.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Text(
                    text = "$dateFormatted • $timeFormatted",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = VibrantTextMuted,
                        fontSize = 10.sp
                    )
                )
            }

            // Right Column: Type Badge, Amount & Quick Actions
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Type Badge (GOT / GAVE)
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (item.isCashIn) VibrantGreenBg else VibrantRoseBg
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (item.isCashIn) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                                contentDescription = null,
                                tint = if (item.isCashIn) VibrantGreenText else VibrantRoseText,
                                modifier = Modifier.size(10.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = item.type,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (item.isCashIn) VibrantGreenText else VibrantRoseText
                            )
                        }
                    }

                    // Amount
                    Text(
                        text = "Rs. %,.0f".format(item.amount),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = VibrantNavyDark,
                            fontSize = 15.sp
                        )
                    )
                }

                // Quick Action Icons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onShare,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share Slip",
                            tint = VibrantTextSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = VibrantTextSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = VibrantRoseText.copy(alpha = 0.8f),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

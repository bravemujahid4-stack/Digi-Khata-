package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.LedgerBalances
import com.example.data.model.TransactionItem
import com.example.ui.theme.KhalidCardBg
import com.example.ui.theme.KhalidCardText
import com.example.ui.theme.MunawarCardBg
import com.example.ui.theme.MunawarCardText
import com.example.ui.theme.TotalCardBg
import com.example.ui.theme.TotalCardText
import com.example.ui.theme.VibrantBorder
import com.example.ui.theme.VibrantGreen
import com.example.ui.theme.VibrantGreenText
import com.example.ui.theme.VibrantNavyDark
import com.example.ui.theme.VibrantRose
import com.example.ui.theme.VibrantRoseText
import com.example.ui.theme.VibrantSurface
import com.example.ui.theme.VibrantSurfaceVariant
import com.example.ui.theme.VibrantTextSecondary

@Composable
fun AnalyticsSummaryDialog(
    balances: LedgerBalances,
    transactions: List<TransactionItem>,
    onDismiss: () -> Unit,
    onShareReport: () -> Unit
) {
    // Calculate top parties
    val topParties = remember(transactions) {
        transactions.groupBy { it.name }
            .mapValues { entry -> entry.value.sumOf { it.amount } }
            .toList()
            .sortedByDescending { it.second }
            .take(6)
    }

    val totalVolume = remember(balances) {
        balances.totalGotAmount + balances.totalGaveAmount
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = VibrantSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, VibrantBorder),
            shadowElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("analytics_summary_dialog")
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(VibrantNavyDark, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Analytics,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Ledger Analytics & Report",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = VibrantNavyDark
                            )
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = VibrantTextSecondary)
                    }
                }

                Divider(color = VibrantBorder)

                LazyColumn(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Total Flow Card
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(VibrantSurfaceVariant, RoundedCornerShape(16.dp))
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Cash In vs Cash Out",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = VibrantNavyDark
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Total GOT: ₨ %,.0f".format(balances.totalGotAmount),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = VibrantGreenText
                                )
                                Text(
                                    text = "Total GAVE: ₨ %,.0f".format(balances.totalGaveAmount),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = VibrantRoseText
                                )
                            }

                            val inRatio = if (totalVolume > 0) (balances.totalGotAmount / totalVolume).toFloat() else 0.5f
                            LinearProgressIndicator(
                                progress = { inRatio },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = VibrantGreen,
                                trackColor = VibrantRose
                            )
                        }
                    }

                    // Handler Balance Breakdown
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(VibrantSurfaceVariant, RoundedCornerShape(16.dp))
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Handler Holdings",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = VibrantNavyDark
                            )

                            DetailRow(
                                label = "Munawar Cash Balance",
                                value = "₨ %,.0f".format(balances.munawarBalance),
                                valueColor = MunawarCardText,
                                isBold = true
                            )
                            DetailRow(
                                label = "Munawar Total Entries",
                                value = "${balances.munawarEntriesCount} entries"
                            )
                            Divider(color = VibrantBorder)
                            DetailRow(
                                label = "Khalid Cash Balance",
                                value = "₨ %,.0f".format(balances.khalidBalance),
                                valueColor = KhalidCardText,
                                isBold = true
                            )
                            DetailRow(
                                label = "Khalid Total Entries",
                                value = "${balances.khalidEntriesCount} entries"
                            )
                            Divider(color = VibrantBorder)
                            DetailRow(
                                label = "Net Total Cash in Hand",
                                value = "₨ %,.0f".format(balances.totalBalance),
                                valueColor = TotalCardText,
                                isBold = true
                            )
                        }
                    }

                    // Top Customers / Suppliers
                    if (topParties.isNotEmpty()) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(VibrantSurfaceVariant, RoundedCornerShape(16.dp))
                                    .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Top Parties by Turnover",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = VibrantNavyDark
                                )

                                topParties.forEach { (name, volume) ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = name,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = VibrantNavyDark
                                        )
                                        Text(
                                            text = "₨ %,.0f".format(volume),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = VibrantNavyDark
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Share Full Ledger Report Button
                Button(
                    onClick = onShareReport,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VibrantNavyDark)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Export & Share Daily Report", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

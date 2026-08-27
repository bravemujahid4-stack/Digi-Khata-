package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LedgerBalances
import com.example.ui.theme.KhalidCardBg
import com.example.ui.theme.KhalidCardText
import com.example.ui.theme.MunawarCardBg
import com.example.ui.theme.MunawarCardText
import com.example.ui.theme.TotalCardBg
import com.example.ui.theme.TotalCardText
import com.example.ui.theme.TotalIconBoxBg
import com.example.ui.theme.VibrantGreen
import com.example.ui.theme.VibrantGreenText
import com.example.ui.theme.VibrantRose
import com.example.ui.theme.VibrantRoseText

@Composable
fun BalanceSummaryCards(
    balances: LedgerBalances,
    onMunawarCardClick: () -> Unit,
    onKhalidCardClick: () -> Unit,
    onTotalCardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Top 2 cards: Munawar & Khalid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            BalanceCard(
                handlerName = "MUNAWAR",
                amount = balances.munawarBalance,
                cardBg = MunawarCardBg,
                textColor = MunawarCardText,
                count = balances.munawarEntriesCount,
                onClick = onMunawarCardClick,
                testTag = "munawar_balance_card",
                modifier = Modifier.weight(1f)
            )

            BalanceCard(
                handlerName = "KHALID",
                amount = balances.khalidBalance,
                cardBg = KhalidCardBg,
                textColor = KhalidCardText,
                count = balances.khalidEntriesCount,
                onClick = onKhalidCardClick,
                testTag = "khalid_balance_card",
                modifier = Modifier.weight(1f)
            )
        }

        // Full width card: Total Cash in Hand
        TotalBalanceCard(
            amount = balances.totalBalance,
            totalEntries = balances.totalEntriesCount,
            gotAmount = balances.totalGotAmount,
            gaveAmount = balances.totalGaveAmount,
            onClick = onTotalCardClick,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun BalanceCard(
    handlerName: String,
    amount: Double,
    cardBg: Color,
    textColor: Color,
    count: Int,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onClick)
            .testTag(testTag),
        shape = RoundedCornerShape(24.dp),
        color = cardBg,
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 14.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = handlerName,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp,
                        color = textColor.copy(alpha = 0.7f)
                    )
                )

                Text(
                    text = "$count entries",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = textColor.copy(alpha = 0.6f)
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Column {
                Text(
                    text = "Balance",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 10.sp,
                        color = textColor.copy(alpha = 0.8f)
                    )
                )

                Text(
                    text = "₨ %,.0f".format(amount),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = textColor
                    ),
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun TotalBalanceCard(
    amount: Double,
    totalEntries: Int,
    gotAmount: Double,
    gaveAmount: Double,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onClick)
            .testTag("total_balance_card"),
        shape = RoundedCornerShape(24.dp),
        color = TotalCardBg,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 18.dp, vertical = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "TOTAL CASH IN HAND",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp,
                            color = TotalCardText.copy(alpha = 0.7f)
                        )
                    )

                    Text(
                        text = "• $totalEntries entries",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 10.sp,
                            color = TotalCardText.copy(alpha = 0.6f)
                        )
                    )
                }

                Text(
                    text = "₨ %,.0f".format(amount),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black,
                        color = TotalCardText
                    )
                )

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ArrowDownward,
                            contentDescription = null,
                            tint = VibrantGreenText,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "In: ₨ %,.0f".format(gotAmount),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = VibrantGreenText
                            )
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ArrowUpward,
                            contentDescription = null,
                            tint = VibrantRoseText,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "Out: ₨ %,.0f".format(gaveAmount),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = VibrantRoseText
                            )
                        )
                    }
                }
            }

            // Decorative Cash Box in #E9DDFF
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(TotalIconBoxBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AccountBalanceWallet,
                    contentDescription = null,
                    tint = TotalCardText,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SyncState
import com.example.ui.theme.VibrantAmber
import com.example.ui.theme.VibrantGreen
import com.example.ui.theme.VibrantGreenBg
import com.example.ui.theme.VibrantNavyContainer
import com.example.ui.theme.VibrantNavyDark
import com.example.ui.theme.VibrantNavyPrimary
import com.example.ui.theme.VibrantRose
import com.example.ui.theme.VibrantSurface
import com.example.ui.theme.VibrantSurfaceVariant
import com.example.ui.theme.VibrantTextMuted
import com.example.ui.theme.VibrantTextSecondary

@Composable
fun HeaderSection(
    syncState: SyncState,
    onRefreshClick: () -> Unit,
    onAnalyticsClick: () -> Unit,
    onShareClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("app_header"),
        shape = RoundedCornerShape(24.dp),
        color = VibrantSurface,
        shadowElevation = 2.dp,
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "MAS Accounts",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.5).sp,
                            color = VibrantNavyDark,
                            fontSize = 22.sp
                        )
                    )
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        SyncDotIndicator(syncState = syncState)
                        Text(
                            text = when (syncState) {
                                SyncState.LIVE -> "Cloud Sync Live"
                                SyncState.SYNCING -> "Syncing..."
                                SyncState.OFFLINE -> "Offline Cached"
                                SyncState.ERROR -> "Sync Offline"
                            },
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = VibrantTextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                letterSpacing = 0.6.sp
                            )
                        )
                    }
                }

                // Avatar / Profile icon badge as in Vibrant Palette
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SyncBadge(syncState = syncState, onBadgeClick = onRefreshClick)

                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(VibrantNavyContainer)
                            .clickable(onClick = onSettingsClick),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "M",
                            color = VibrantNavyDark,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Toolbar (Vibrant Palette style chips)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HeaderActionButton(
                    icon = Icons.Default.Refresh,
                    label = "Sync Now",
                    onClick = onRefreshClick,
                    testTag = "header_sync_button",
                    modifier = Modifier.weight(1f)
                )
                HeaderActionButton(
                    icon = Icons.Default.Analytics,
                    label = "Reports",
                    onClick = onAnalyticsClick,
                    testTag = "header_report_button",
                    modifier = Modifier.weight(1f)
                )
                HeaderActionButton(
                    icon = Icons.Default.Share,
                    label = "Share",
                    onClick = onShareClick,
                    testTag = "header_share_button",
                    modifier = Modifier.weight(1f)
                )
                HeaderActionButton(
                    icon = Icons.Default.Settings,
                    label = "Settings",
                    onClick = onSettingsClick,
                    testTag = "header_settings_button",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun HeaderActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .testTag(testTag),
        color = VibrantSurfaceVariant,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = VibrantNavyDark,
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                color = VibrantNavyDark,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun SyncDotIndicator(syncState: SyncState) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val dotColor = when (syncState) {
        SyncState.LIVE -> VibrantGreen
        SyncState.SYNCING -> VibrantNavyPrimary
        SyncState.OFFLINE -> VibrantAmber
        SyncState.ERROR -> VibrantRose
    }

    Box(
        modifier = Modifier
            .size(8.dp)
            .scale(if (syncState == SyncState.LIVE || syncState == SyncState.SYNCING) pulseScale else 1f)
            .background(dotColor, CircleShape)
    )
}

@Composable
fun SyncBadge(
    syncState: SyncState,
    onBadgeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (badgeBg, textColor, labelText) = when (syncState) {
        SyncState.LIVE -> Triple(VibrantGreenBg, VibrantGreen, "LIVE")
        SyncState.SYNCING -> Triple(VibrantNavyContainer, VibrantNavyPrimary, "SYNC...")
        SyncState.OFFLINE -> Triple(Color(0xFFFEF3C7), Color(0xFFB45309), "OFFLINE")
        SyncState.ERROR -> Triple(Color(0xFFFFEBEE), Color(0xFFC62828), "RETRY")
    }

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onBadgeClick)
            .testTag("sync_status_badge"),
        shape = RoundedCornerShape(12.dp),
        color = badgeBg
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = labelText,
                color = textColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }
    }
}

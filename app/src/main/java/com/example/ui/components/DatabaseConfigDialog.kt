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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.remote.FirebaseClient
import com.example.ui.theme.VibrantBorder
import com.example.ui.theme.VibrantNavyDark
import com.example.ui.theme.VibrantNavyPrimary
import com.example.ui.theme.VibrantSurface
import com.example.ui.theme.VibrantTextSecondary

@Composable
fun DatabaseConfigDialog(
    currentUrl: String = FirebaseClient.DEFAULT_BASE_URL,
    onSaveUrl: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var urlText by remember { mutableStateOf(currentUrl) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = VibrantSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, VibrantBorder),
            shadowElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("database_config_dialog")
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
                                imageVector = Icons.Default.Storage,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Database Sync Settings",
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

                Text(
                    text = "MAS Accounts syncs automatically in real-time with Firebase Realtime Database across all phones running the ledger.",
                    fontSize = 12.sp,
                    color = VibrantTextSecondary
                )

                OutlinedTextField(
                    value = urlText,
                    onValueChange = { urlText = it },
                    label = { Text("Firebase Realtime DB URL", color = VibrantTextSecondary) },
                    placeholder = { Text("https://your-database.firebaseio.com/") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VibrantNavyPrimary,
                        unfocusedBorderColor = VibrantBorder
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { urlText = FirebaseClient.DEFAULT_BASE_URL },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(14.dp), tint = VibrantNavyDark)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reset Default", fontSize = 11.sp, color = VibrantNavyDark)
                    }

                    Button(
                        onClick = {
                            onSaveUrl(urlText.trim())
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = VibrantNavyDark)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Save URL", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

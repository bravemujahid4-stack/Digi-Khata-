package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.EntryType
import com.example.data.model.HandlerType
import com.example.data.model.TransactionItem
import com.example.ui.theme.KhalidCardBg
import com.example.ui.theme.KhalidCardText
import com.example.ui.theme.MunawarCardBg
import com.example.ui.theme.MunawarCardText
import com.example.ui.theme.VibrantBorder
import com.example.ui.theme.VibrantGreen
import com.example.ui.theme.VibrantGreenBg
import com.example.ui.theme.VibrantGreenText
import com.example.ui.theme.VibrantNavyDark
import com.example.ui.theme.VibrantNavyPrimary
import com.example.ui.theme.VibrantRose
import com.example.ui.theme.VibrantRoseBg
import com.example.ui.theme.VibrantRoseText
import com.example.ui.theme.VibrantSurface
import com.example.ui.theme.VibrantSurfaceVariant
import com.example.ui.theme.VibrantTextMuted
import com.example.ui.theme.VibrantTextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TransactionDetailDialog(
    item: TransactionItem,
    onDismiss: () -> Unit,
    onEditSave: (String, Double, String, String, String) -> Unit,
    onDelete: () -> Unit,
    onShare: () -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf(item.name) }
    var editAmount by remember { mutableStateOf(if (item.amount % 1 == 0.0) item.amount.toInt().toString() else item.amount.toString()) }
    var editHandler by remember { mutableStateOf(HandlerType.fromString(item.handler)) }
    var editType by remember { mutableStateOf(EntryType.fromString(item.type)) }
    var editNote by remember { mutableStateOf(item.note) }

    val formattedDate = remember(item.timestamp) {
        SimpleDateFormat("dd MMMM yyyy, hh:mm:ss a", Locale.getDefault()).format(Date(item.timestamp))
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = VibrantSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, VibrantBorder),
            shadowElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("transaction_detail_dialog")
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Dialog Header
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
                                imageVector = Icons.Default.Receipt,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (isEditing) "Edit Transaction" else "Transaction Voucher",
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

                if (!isEditing) {
                    // View Mode Voucher Details
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(VibrantSurfaceVariant, RoundedCornerShape(16.dp))
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        DetailRow(label = "Customer / Supplier", value = item.name, isBold = true)
                        DetailRow(
                            label = "Amount",
                            value = "₨ %,.0f".format(item.amount),
                            valueColor = if (item.isCashIn) VibrantGreenText else VibrantRoseText,
                            isBold = true
                        )
                        DetailRow(
                            label = "Type",
                            value = if (item.isCashIn) "Cash In (GOT)" else "Cash Out (GAVE)"
                        )
                        DetailRow(
                            label = "Handler",
                            value = item.handler,
                            valueColor = if (item.handler == "Munawar") MunawarCardText else KhalidCardText
                        )
                        if (item.note.isNotBlank()) {
                            DetailRow(label = "Notes", value = item.note)
                        }
                        DetailRow(label = "Timestamp", value = formattedDate)
                        DetailRow(label = "Entry ID", value = item.id.take(12) + "...")
                    }

                    // Action Buttons: Share, Edit, Delete
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onShare,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp), tint = VibrantNavyDark)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Share", fontSize = 12.sp, color = VibrantNavyDark, fontWeight = FontWeight.SemiBold)
                        }

                        Button(
                            onClick = { isEditing = true },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = VibrantNavyDark)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Edit", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }

                        Button(
                            onClick = onDelete,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = VibrantRose)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Delete", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                } else {
                    // Edit Mode Form
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = editName,
                            onValueChange = { editName = it },
                            label = { Text("Customer / Supplier", color = VibrantTextSecondary) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = VibrantNavyPrimary,
                                unfocusedBorderColor = VibrantBorder
                            )
                        )

                        OutlinedTextField(
                            value = editAmount,
                            onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) editAmount = it },
                            label = { Text("Amount (Rs.)", color = VibrantTextSecondary) },
                            prefix = { Text("Rs. ", fontWeight = FontWeight.Bold, color = VibrantNavyDark) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = VibrantNavyPrimary,
                                unfocusedBorderColor = VibrantBorder
                            )
                        )

                        // Handler Chips
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            HandlerOptionChip(
                                name = "Munawar",
                                isSelected = editHandler == HandlerType.MUNAWAR,
                                selectedBg = MunawarCardBg,
                                selectedBorder = VibrantNavyPrimary,
                                selectedText = MunawarCardText,
                                onClick = { editHandler = HandlerType.MUNAWAR },
                                testTag = "edit_handler_munawar",
                                modifier = Modifier.weight(1f)
                            )
                            HandlerOptionChip(
                                name = "Khalid",
                                isSelected = editHandler == HandlerType.KHALID,
                                selectedBg = KhalidCardBg,
                                selectedBorder = Color(0xFF50606E),
                                selectedText = KhalidCardText,
                                onClick = { editHandler = HandlerType.KHALID },
                                testTag = "edit_handler_khalid",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Type Chips
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChipItem(
                                label = "Cash In (GOT)",
                                isSelected = editType == EntryType.GOT,
                                selectedBg = VibrantGreenBg,
                                selectedTextColor = VibrantGreenText,
                                onClick = { editType = EntryType.GOT },
                                testTag = "edit_type_got",
                                modifier = Modifier.weight(1f)
                            )
                            FilterChipItem(
                                label = "Cash Out (GAVE)",
                                isSelected = editType == EntryType.GAVE,
                                selectedBg = VibrantRoseBg,
                                selectedTextColor = VibrantRoseText,
                                onClick = { editType = EntryType.GAVE },
                                testTag = "edit_type_gave",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        OutlinedTextField(
                            value = editNote,
                            onValueChange = { editNote = it },
                            label = { Text("Note", color = VibrantTextSecondary) },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = VibrantNavyPrimary,
                                unfocusedBorderColor = VibrantBorder
                            )
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { isEditing = false }) {
                                Text("Cancel", color = VibrantNavyDark)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    val amt = editAmount.toDoubleOrNull() ?: item.amount
                                    onEditSave(
                                        editName.trim(),
                                        amt,
                                        editHandler.displayName,
                                        editType.displayName,
                                        editNote.trim()
                                    )
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = VibrantNavyDark)
                            ) {
                                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Save Changes")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailRow(
    label: String,
    value: String,
    valueColor: Color = Color.Unspecified,
    isBold: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = VibrantTextSecondary,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = if (valueColor != Color.Unspecified) valueColor else VibrantNavyDark
        )
    }
}

@Composable
fun DeleteConfirmDialog(
    item: TransactionItem,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Entry?", fontWeight = FontWeight.Bold, color = VibrantNavyDark) },
        text = {
            Text(
                "Are you sure you want to delete the entry for ${item.name} of Rs. ${"%,.0f".format(item.amount)}? This will remove it from all synced devices.",
                color = VibrantTextSecondary
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = VibrantRose)
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = VibrantNavyDark)
            }
        }
    )
}

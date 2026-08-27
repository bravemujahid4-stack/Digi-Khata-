package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EntryType
import com.example.data.model.HandlerType
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NewEntryCard(
    custName: String,
    onCustNameChange: (String) -> Unit,
    amount: String,
    onAmountChange: (String) -> Unit,
    handler: HandlerType,
    onHandlerChange: (HandlerType) -> Unit,
    entryType: EntryType,
    onEntryTypeChange: (EntryType) -> Unit,
    note: String,
    onNoteChange: (String) -> Unit,
    recentNames: List<String>,
    onQuickAmountAdd: (Double) -> Unit,
    onSubmit: () -> Unit,
    isSubmitting: Boolean,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("new_entry_card"),
        shape = RoundedCornerShape(24.dp),
        color = VibrantSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, VibrantBorder),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Card Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .background(VibrantNavyDark, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "New Entry",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "New Transaction",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = VibrantNavyDark,
                            fontSize = 17.sp
                        )
                    )
                }

                Text(
                    text = "Live Sync",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = VibrantGreen,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                )
            }

            // Customer Name Field
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                OutlinedTextField(
                    value = custName,
                    onValueChange = onCustNameChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("cust_name_input"),
                    label = { Text("Customer / Supplier Name *", color = VibrantTextSecondary) },
                    placeholder = { Text("e.g., Ali Fabrics, Tariq Bhai", color = VibrantTextMuted) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = VibrantTextSecondary
                        )
                    },
                    trailingIcon = {
                        if (custName.isNotEmpty()) {
                            IconButton(onClick = { onCustNameChange("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = VibrantTextSecondary)
                            }
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next
                    ),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VibrantNavyPrimary,
                        unfocusedBorderColor = VibrantBorder,
                        focusedTextColor = VibrantNavyDark,
                        unfocusedTextColor = VibrantNavyDark
                    )
                )

                // Recent Name suggestions chips
                if (recentNames.isNotEmpty() && custName.isEmpty()) {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        recentNames.take(4).forEach { name ->
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { onCustNameChange(name) }
                                    .testTag("recent_name_chip_$name"),
                                shape = RoundedCornerShape(8.dp),
                                color = VibrantSurfaceVariant
                            ) {
                                Text(
                                    text = name,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = VibrantNavyDark
                                )
                            }
                        }
                    }
                }
            }

            // Amount Field
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                OutlinedTextField(
                    value = amount,
                    onValueChange = { input ->
                        if (input.isEmpty() || input.matches(Regex("^\\d*\\.?\\d*$"))) {
                            onAmountChange(input)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("amount_input"),
                    label = { Text("Amount (Rs.) *", color = VibrantTextSecondary) },
                    placeholder = { Text("0", color = VibrantTextMuted) },
                    prefix = { Text("Rs. ", fontWeight = FontWeight.Bold, color = VibrantNavyDark) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next
                    ),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VibrantNavyPrimary,
                        unfocusedBorderColor = VibrantBorder,
                        focusedTextColor = VibrantNavyDark,
                        unfocusedTextColor = VibrantNavyDark
                    )
                )

                // Quick Amount Chips (+500, +1K, +5K, +10K, +50K)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val quickAmounts = listOf(
                        500.0 to "+500",
                        1000.0 to "+1k",
                        5000.0 to "+5k",
                        10000.0 to "+10k",
                        50000.0 to "+50k"
                    )
                    quickAmounts.forEach { (amt, label) ->
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onQuickAmountAdd(amt) }
                                .testTag("quick_amount_chip_$label"),
                            shape = RoundedCornerShape(10.dp),
                            color = VibrantSurfaceVariant
                        ) {
                            Text(
                                text = label,
                                modifier = Modifier.padding(vertical = 6.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = VibrantNavyDark
                            )
                        }
                    }
                }
            }

            // Handler Selector: Munawar / Khalid
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Handler:",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = VibrantNavyDark
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    HandlerOptionChip(
                        name = "Munawar",
                        isSelected = handler == HandlerType.MUNAWAR,
                        selectedBg = MunawarCardBg,
                        selectedBorder = VibrantNavyPrimary,
                        selectedText = MunawarCardText,
                        onClick = { onHandlerChange(HandlerType.MUNAWAR) },
                        testTag = "handler_munawar_radio",
                        modifier = Modifier.weight(1f)
                    )

                    HandlerOptionChip(
                        name = "Khalid",
                        isSelected = handler == HandlerType.KHALID,
                        selectedBg = KhalidCardBg,
                        selectedBorder = Color(0xFF50606E),
                        selectedText = KhalidCardText,
                        onClick = { onHandlerChange(HandlerType.KHALID) },
                        testTag = "handler_khalid_radio",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Type Selector: Cash In (GOT) / Cash Out (GAVE)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Type:",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = VibrantNavyDark
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TypeOptionChip(
                        label = "Cash In (GOT)",
                        subLabel = "+ Received",
                        icon = Icons.Default.ArrowDownward,
                        isSelected = entryType == EntryType.GOT,
                        selectedBg = VibrantGreenBg,
                        selectedBorder = VibrantGreen,
                        selectedText = VibrantGreenText,
                        onClick = { onEntryTypeChange(EntryType.GOT) },
                        testTag = "type_got_radio",
                        modifier = Modifier.weight(1f)
                    )

                    TypeOptionChip(
                        label = "Cash Out (GAVE)",
                        subLabel = "- Paid Out",
                        icon = Icons.Default.ArrowUpward,
                        isSelected = entryType == EntryType.GAVE,
                        selectedBg = VibrantRoseBg,
                        selectedBorder = VibrantRose,
                        selectedText = VibrantRoseText,
                        onClick = { onEntryTypeChange(EntryType.GAVE) },
                        testTag = "type_gave_radio",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Notes Field (Optional)
            OutlinedTextField(
                value = note,
                onValueChange = onNoteChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("note_input"),
                label = { Text("Notes (Optional)", color = VibrantTextSecondary) },
                placeholder = { Text("Invoice #, payment method, details...", color = VibrantTextMuted) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        onSubmit()
                    }
                ),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = VibrantNavyPrimary,
                    unfocusedBorderColor = VibrantBorder,
                    focusedTextColor = VibrantNavyDark,
                    unfocusedTextColor = VibrantNavyDark
                )
            )

            // Submit Button: "Save & Sync to All Phones"
            Button(
                onClick = {
                    focusManager.clearFocus()
                    onSubmit()
                },
                enabled = !isSubmitting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("save_and_sync_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = VibrantNavyDark,
                    contentColor = Color.White
                )
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Saving & Syncing...", fontWeight = FontWeight.Bold)
                } else {
                    Icon(
                        imageVector = Icons.Default.Sync,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Save & Sync to All Phones",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

@Composable
fun HandlerOptionChip(
    name: String,
    isSelected: Boolean,
    selectedBg: Color,
    selectedBorder: Color,
    selectedText: Color,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) selectedBorder else VibrantBorder,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .testTag(testTag),
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) selectedBg else Color.Transparent
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .border(
                        width = 2.dp,
                        color = if (isSelected) selectedBorder else VibrantBorder,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(selectedBorder, CircleShape)
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = name,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) selectedText else VibrantNavyDark,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun TypeOptionChip(
    label: String,
    subLabel: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    selectedBg: Color,
    selectedBorder: Color,
    selectedText: Color,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) selectedBorder else VibrantBorder,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .testTag(testTag),
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) selectedBg else Color.Transparent
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(
                        if (isSelected) selectedBorder else VibrantSurfaceVariant,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) Color.White else VibrantTextSecondary,
                    modifier = Modifier.size(14.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = label,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                    color = if (isSelected) selectedText else VibrantNavyDark,
                    fontSize = 12.sp
                )
                Text(
                    text = subLabel,
                    fontSize = 10.sp,
                    color = if (isSelected) selectedText.copy(alpha = 0.85f) else VibrantTextSecondary
                )
            }
        }
    }
}

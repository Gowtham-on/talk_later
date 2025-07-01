package com.cmp.talklater.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNotesBottomSheet(
    showSheet: Boolean,
    onDismiss: () -> Unit,
    onOptionSelected: (Pair<Int, String>) -> Unit,
) {
    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            val options = listOf(
                Pair(1, "1 Minutes"),
                Pair(5, "5 Minutes"),
                Pair(10, "10 Minutes"),
                Pair(15, "15 Minutes"),
                Pair(30, "30 Minutes"),
                Pair(60, "1 Hour"),
            )
            options.forEach { option ->
                ListItem(
                    headlineContent = {
                        Text(
                            option.second,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    modifier = Modifier.clickable {
                        onOptionSelected(option)
                        onDismiss()
                    },
                    colors = ListItemDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        }
    }
}

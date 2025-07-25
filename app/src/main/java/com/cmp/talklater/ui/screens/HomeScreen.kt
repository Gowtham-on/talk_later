package com.cmp.talklater.ui.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cmp.talklater.R
import com.cmp.talklater.model.ContactInfo
import com.cmp.talklater.model.GroupedContactInfo
import com.cmp.talklater.ui.components.ActionIcon
import com.cmp.talklater.ui.components.SwipeableItemsWithAction
import com.cmp.talklater.util.TimeUtil
import com.cmp.talklater.util.Utils
import com.cmp.talklater.util.ViewType
import com.cmp.talklater.viewmodel.ContactViewmodel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import java.util.Date
import androidx.core.net.toUri

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(viewModel: ContactViewmodel = hiltViewModel(), onOpenSettings: () -> Unit) {
    val contacts by viewModel.contacts.collectAsState()

    val groupedContacts = remember { mutableStateOf(Utils.getListByGrouping(contacts)) }

    val postNotificationState = rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        postNotificationState.launchPermissionRequest()
    }

    LaunchedEffect(contacts) {
        groupedContacts.value = Utils.getListByGrouping(contacts)
    }


    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onOpenSettings,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings")
            }
        }
    ) { padding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(Modifier.height(10.dp))
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Talk Later",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 15.dp)
                )
                Image(
                    painter = painterResource(
                        if (viewModel.viewType == ViewType.COLLAPSE)
                            R.drawable.collapse_svg
                        else
                            R.drawable.expand_svg
                    ),
                    contentDescription = "Content View",
                    modifier = Modifier
                        .padding(end = 15.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .size(30.dp)
                        .align(Alignment.CenterEnd)
                        .clickable(onClick = {
                            if (viewModel.viewType == ViewType.EXPANDED) {
                                viewModel.setListViewType(ViewType.COLLAPSE)
                            } else {
                                viewModel.setListViewType(ViewType.EXPANDED)
                            }
                        }),
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.inverseSurface),
                )
            }
            Spacer(Modifier.height(25.dp))

            if (contacts.isEmpty()) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight(fraction = 0.5f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(R.drawable.no_lists_found),
                            contentDescription = "No Logs Found",
                            modifier = Modifier
                                .size(200.dp)
                        )
                        Spacer(Modifier.height(5.dp))
                        Text(
                            "No call logs yet. \nNew call reminders will appear here.",
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.padding(
                        start = padding.calculateStartPadding(LayoutDirection.Ltr),
                        end = padding.calculateEndPadding(LayoutDirection.Ltr)
                    )
                ) {
                    if (viewModel.viewType == ViewType.EXPANDED)
                        items(contacts) { contact ->
                            GetCallItem(contact, viewModel)
                        }
                    else
                        items(groupedContacts.value) {
                            GetGroupedList(it, viewModel)
                        }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GetGroupedList(
    grouped: GroupedContactInfo,
    viewModel: ContactViewmodel,
) {
    GetCallItem(
        grouped.listOfContact.first(),
        viewModel,
        grouped.listOfContact.size,
        grouped.listOfContact,
    )
}

@Composable
fun GetCallItem(
    info: ContactInfo,
    viewmodel: ContactViewmodel,
    count: Int? = null,
    listOfContactInfo: List<ContactInfo>? = null,
) {
    val context = LocalContext.current
    var isRevealed by remember { mutableStateOf(false) }

    SwipeableItemsWithAction(
        onExpanded = {
            isRevealed = true
            Utils.vibrate(context)
        },
        isRevealed = isRevealed,
        isSwipeable = true,
        actions = {
            ActionViews(viewmodel, info,listOfContactInfo, isRevealed = {
                isRevealed = false
            })
        },
        content = {
            CallItemContent(count, info, context)
        }
    )
    Spacer(Modifier.height(15.dp))
    Box(
        modifier = Modifier
            .height(1.dp)
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f))
    ) { }
    Spacer(Modifier.height(10.dp))
}


fun openDialer(phoneNumber: String, context: android.content.Context) {
    val dialIntent = Intent(Intent.ACTION_DIAL).apply {
        data = "tel:$phoneNumber".toUri()
    }
    context.startActivity(dialIntent)
}


@Composable
fun CallItemContent(
    count: Int? = null,
    info: ContactInfo,
    context: Context
) {
    val type = when (info.type) {
        3 -> "Missed Call"
        5 -> "Rejected Call"
        else -> "Unknown"
    }
    val name = if (info.name.isBlank()) "Unknown" else info.name.toString()

    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Text(
            if (count != null) "$type ($count)" else type,
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            name,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(5.dp))
        Row {
            Text(
                info.number,
                style = MaterialTheme.typography.labelLarge,
            )
            Spacer(Modifier.width(15.dp))
            Text(
                TimeUtil.formatMillis(info.time.toLongOrNull() ?: Date().time),
                style = MaterialTheme.typography.labelLarge,
            )
        }
        Spacer(Modifier.height(10.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .clickable(onClick = {
                    openDialer(info.number.toString(), context)
                })
                .background(
                    color = MaterialTheme.colorScheme.primary,
                )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stringResource(R.string.call_back),
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                        .padding(start = 10.dp, end = 5.dp),
                    color = MaterialTheme.colorScheme.surface,
                    fontWeight = FontWeight.W600,
                    style = MaterialTheme.typography.bodyMedium
                )
                Icon(
                    imageVector = Icons.Filled.Call,
                    contentDescription = "Call Icon",
                    modifier = Modifier
                        .padding(end = 12.dp)
                        .size(15.dp),
                    tint = MaterialTheme.colorScheme.surface,
                )
            }
        }
    }
}

@Composable
fun ActionViews(
    viewmodel: ContactViewmodel,
    info: ContactInfo,
    listOfContactInfo: List<ContactInfo>?,
    isRevealed: () -> Unit,
) {
    ActionIcon(
        onClick = {
            if (listOfContactInfo.isNullOrEmpty()) {
                viewmodel.deleteContact(info)
            } else {
                for (contact in listOfContactInfo) {
                    viewmodel.deleteContact(contact)
                }
            }
            isRevealed()
        },
        backgroundColor = Color.Red,
        icon = Icons.Default.Delete,
        modifier = Modifier
            .fillMaxHeight()
            .width(150.dp)
    )
}

@Composable
fun DeleteLogsAlertDialog(
    showDialog: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Delete Logs?") },
            text = { Text("Are you sure you want to delete all call logs? This action cannot be undone.") },
            confirmButton = {
                Button(onClick = onConfirm) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        )
    }
}

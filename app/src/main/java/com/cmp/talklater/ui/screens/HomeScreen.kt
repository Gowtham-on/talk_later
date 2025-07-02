package com.cmp.talklater.ui.screens

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
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
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cmp.talklater.R
import com.cmp.talklater.model.ContactInfo
import com.cmp.talklater.runCallLogWorkerOnce
import com.cmp.talklater.util.TimeUtil
import com.cmp.talklater.viewmodel.ContactViewmodel
import com.cmp.talklater.worker.scheduleCallLogWorker
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.util.Date

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(viewModel: ContactViewmodel = hiltViewModel(), onOpenSettings: () -> Unit) {
    val contacts by viewModel.contacts.collectAsState()
    val permissionState = rememberPermissionState(Manifest.permission.READ_CALL_LOG)
    val postNotificationState = rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        scheduleCallLogWorker(context)
        permissionState.launchPermissionRequest()
    }

    LaunchedEffect(permissionState.status) {
        if (permissionState.status.isGranted) {
            postNotificationState.launchPermissionRequest()
            runCallLogWorkerOnce(context)
        }
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
            Text(
                "Talk Later",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(25.dp))
            LazyColumn(
                modifier = Modifier.padding(
                    start = padding.calculateStartPadding(LayoutDirection.Ltr),
                    end = padding.calculateEndPadding(LayoutDirection.Ltr)
                )
            ) {
                items(contacts) { contact ->
                    GetCallItem(contact)
                }
            }
        }
    }
}

@Composable
fun GetCallItem(info: ContactInfo) {
    val context = LocalContext.current
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
            type,
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
        data = Uri.parse("tel:$phoneNumber")
    }
    context.startActivity(dialIntent)
}
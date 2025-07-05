package com.cmp.talklater.ui.screens

import android.Manifest
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.cmp.talklater.R
import com.cmp.talklater.ui.components.AppHeader
import com.cmp.talklater.ui.components.ToggleSwitch
import com.cmp.talklater.util.AppUtils
import com.cmp.talklater.util.AppUtils.openAppSettings
import com.cmp.talklater.util.AppUtils.openNotificationSettings
import com.cmp.talklater.util.ThemeUtil
import com.cmp.talklater.util.Utils
import com.cmp.talklater.viewmodel.ContactViewmodel
import com.cmp.talklater.viewmodel.MainViewmodel
import com.cmp.talklater.worker.scheduler.cancelDeleteContactsWorker
import com.cmp.talklater.worker.scheduler.isDeleteContactsWorkerActive
import com.cmp.talklater.worker.scheduler.scheduleDeleteContactsWorker
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

@Composable
fun SettingsScreen(
    mainViewmodel: MainViewmodel = hiltViewModel(),
    contactViewmodel: ContactViewmodel = hiltViewModel(),
    onBack: () -> Unit
) {
    Column {
        AppHeader(
            title = stringResource(R.string.settings),
            onLeftIconClick = onBack,
            showLeftIcon = true
        )

        Column(
            modifier = Modifier
                .padding(horizontal = 17.dp)
                .verticalScroll(state = rememberScrollState())
        ) {
            Spacer(Modifier.padding(vertical = 5.dp))
            GetThemeChangeSection(mainViewmodel)
            Spacer(Modifier.padding(vertical = 15.dp))
            GetClearLogRow(contactViewmodel, onBack)
            Spacer(Modifier.padding(vertical = 15.dp))
            GetPermissionScreen()
            Spacer(Modifier.padding(vertical = 15.dp))
            GetPrivacySection()
            Spacer(Modifier.padding(vertical = 15.dp))
            GetAboutSection()
            Spacer(Modifier.padding(vertical = 15.dp))
        }
    }
}

@Composable
fun GetClearLogRow(contactViewmodel: ContactViewmodel, onBack: () -> Unit) {

    val context = LocalContext.current

    var isWorkerActive by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        isDeleteContactsWorkerActive(context) {
            isWorkerActive = it
        }
    }

    LaunchedEffect(isWorkerActive) {
        val prefs = context.getSharedPreferences("app_util", MODE_PRIVATE)
        prefs.edit { putBoolean("is_worker_active", isWorkerActive) }
    }

    var showDialog by remember { mutableStateOf(false) }

    DeleteLogsAlertDialog(
        showDialog = showDialog,
        onConfirm = {
            contactViewmodel.deleteAllContacts()
            showDialog = false
            onBack()
        },
        onDismiss = {
            showDialog = false
        }
    )

    Column {
        Text(
            text = "Logs",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.W600
        )
        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column (
                modifier = Modifier.weight(1f)
            ) {
                Text("Delete Log Everyday", style = MaterialTheme.typography.bodyLarge)
                Text(
                    "Keep your log list clean by deleting it every midnight.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            ToggleSwitch(
                checked = isWorkerActive,
                onCheckedChange = {
                    if (isWorkerActive) cancelDeleteContactsWorker(context)
                    else scheduleDeleteContactsWorker(context)
                },
                checkedTrackColor = MaterialTheme.colorScheme.primary,
                uncheckedTrackColor = MaterialTheme.colorScheme.secondary
            )
        }
        Spacer(Modifier.height(10.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    onClick = {
                        showDialog = true
                    }
                )
                .padding(vertical = 10.dp)
        ) {
            Text(
                "Clear All Logs",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Open"
            )
        }
    }
}

@Composable
fun GetThemeChangeSection(mainViewmodel: MainViewmodel) {
    Text(
        stringResource(R.string.appearance),
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.W600
    )
    Spacer(Modifier.padding(vertical = 10.dp))
    Column(
        verticalArrangement = Arrangement.spacedBy(15.dp),
    ) {
        GetThemeItem(ThemeUtil.LIGHT, mainViewmodel)
        GetThemeItem(ThemeUtil.DARK, mainViewmodel)
        GetThemeItem(ThemeUtil.SYSTEM, mainViewmodel)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GetReminderSection() {
    val timeList = remember {
        AppUtils.reminderDurations
    }
    var expanded by remember { mutableStateOf(false) }
    var selectedTime by remember { mutableStateOf(timeList[2]) }

    Column {
        Text(
            stringResource(R.string.reminders),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.W600
        )
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                stringResource(R.string.default_reminder_time),
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(Modifier.width(50.dp))
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    readOnly = true,
                    singleLine = true,
                    value = selectedTime,
                    onValueChange = {},
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryEditable, true),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        disabledContainerColor = MaterialTheme.colorScheme.secondary,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    timeList.forEach { time ->
                        DropdownMenuItem(
                            text = { Text(time) },
                            onClick = {
                                selectedTime = time
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun GetPermissionScreen() {
    val context = LocalContext.current
    val callPermission = rememberPermissionState(Manifest.permission.READ_CALL_LOG)
    val notificationPermission = rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
    var isOptDisabled by remember { mutableStateOf(Utils.isBatteryOptimizationDisabled(context)) }

    Column {
        Text(
            text = stringResource(R.string.permissions),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.W600
        )

        Spacer(Modifier.height(10.dp))
        PermissionToggleRow(
            label = "Call Log Permission",
            isGranted = callPermission.status.isGranted,
            onRequest = {
                if (callPermission.status.shouldShowRationale)
                    callPermission.launchPermissionRequest()
                else openAppSettings(context)
            },
            openSettings = { openAppSettings(context) }
        )
        Spacer(Modifier.height(10.dp))
        PermissionToggleRow(
            label = "Post Notification",
            isGranted = notificationPermission.status.isGranted,
            onRequest = {
                if (notificationPermission.status.shouldShowRationale)
                    notificationPermission.launchPermissionRequest()
                else openNotificationSettings(
                    context
                )
            },
            openSettings = { openNotificationSettings(context) }
        )
        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text("Battery Optimization", style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.height(5.dp))
                Text(
                    "Disable battery optimization to receive reminders on time.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(Modifier.width(50.dp))
            ToggleSwitch(
                checked = isOptDisabled,
                onCheckedChange = {
                    if (isOptDisabled) {
                        val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
                        context.startActivity(intent)
                    } else {
                        val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                        intent.data = "package:${context.packageName}".toUri()
                        context.startActivity(intent)
                    }

                },
                checkedTrackColor = MaterialTheme.colorScheme.primary,
                uncheckedTrackColor = MaterialTheme.colorScheme.secondary
            )
        }
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                isOptDisabled = Utils.isBatteryOptimizationDisabled(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
}

@Composable
private fun PermissionToggleRow(
    label: String,
    isGranted: Boolean,
    onRequest: () -> Unit,
    openSettings: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.width(50.dp))
        ToggleSwitch(
            checked = isGranted,
            onCheckedChange = {
                if (!isGranted) onRequest() else openSettings()
            },
            checkedTrackColor = MaterialTheme.colorScheme.primary,
            uncheckedTrackColor = MaterialTheme.colorScheme.secondary

        )
    }
}

@Composable
fun GetPrivacySection() {
    val context = LocalContext.current

    Text(
        stringResource(R.string.privacy),
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.W600
    )
    Spacer(Modifier.height(10.dp))
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = {
                    val url = "https://talklater-d0cc6.web.app/privacy-policy.html"
                    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                    context.startActivity(intent)
                }
            )
            .padding(vertical = 10.dp)
    ) {
        Text(
            "Privacy Policy",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "Open"
        )
    }
}

@Composable
fun GetAboutSection() {
    val context = LocalContext.current
    val packageManager = context.packageManager
    val packageName = context.packageName

    val versionName = try {
        packageManager.getPackageInfo(packageName, 0).versionName ?: "Unknown"
    } catch (e: PackageManager.NameNotFoundException) {
        ""
    }
    Text(
        stringResource(R.string.about),
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.W600
    )
    Spacer(Modifier.height(10.dp))
    if (!versionName.isBlank())
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
        ) {
            val context = LocalContext.current
            Text(
                "Version",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                versionName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = {
                    val email = "codemobilepros@gmail.com"
                    val subject = "Talk Later App Support"

                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = "mailto:$email".toUri()
                        putExtra(Intent.EXTRA_SUBJECT, subject)
                    }
                    context.startActivity(intent)
                }
            )
            .padding(vertical = 10.dp)
    ) {
        Text(
            "Contact Support",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "Open"
        )
    }


    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = {
                    val email = "codemobilepros@gmail.com"
                    val subject = "Feature Request"

                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = "mailto:$email".toUri()
                        putExtra(Intent.EXTRA_SUBJECT, subject)
                    }
                    context.startActivity(intent)
                }
            )
            .padding(vertical = 10.dp)
    ) {
        Text(
            "Feature Request / Bug Report",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "Open"
        )
    }
}

@Composable
fun GetThemeItem(type: ThemeUtil, mainViewmodel: MainViewmodel) {
    val context = LocalContext.current

    val prefs = context.getSharedPreferences("theme_pref", Context.MODE_PRIVATE)

    val themeText = remember {
        mutableStateOf(
            when (type) {
                ThemeUtil.LIGHT -> "Light"
                ThemeUtil.DARK -> "Dark"
                ThemeUtil.SYSTEM -> "System"
            }
        )
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                shape = RoundedCornerShape(12),
                color = MaterialTheme.colorScheme.secondary
            )
            .clickable {
                prefs.edit { putString("theme", type.name) }
                when (type) {
                    ThemeUtil.LIGHT -> mainViewmodel.setCurrentTheme(ThemeUtil.LIGHT)
                    ThemeUtil.DARK -> mainViewmodel.setCurrentTheme(ThemeUtil.DARK)
                    ThemeUtil.SYSTEM -> mainViewmodel.setCurrentTheme(ThemeUtil.SYSTEM)
                }
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(themeText.value, modifier = Modifier.padding(15.dp))
            RadioButton(
                selected = type == mainViewmodel.theme,
                onClick = {
                    mainViewmodel.setCurrentTheme(type)
                },
                colors = RadioButtonDefaults.colors(
                    selectedColor = MaterialTheme.colorScheme.primary,
                    unselectedColor = MaterialTheme.colorScheme.onSecondaryContainer
                ),
            )
        }
    }
}
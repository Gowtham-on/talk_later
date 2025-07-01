package com.cmp.talklater.ui.screens

import android.content.pm.PackageManager
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cmp.talklater.R
import com.cmp.talklater.ui.components.AppHeader
import com.cmp.talklater.util.AppUtils
import com.cmp.talklater.util.ThemeUtil
import com.cmp.talklater.viewmodel.MainViewmodel

@Composable
fun SettingsScreen(mainViewmodel: MainViewmodel = hiltViewModel(), onBack: () -> Unit) {
    Column {
        AppHeader(
            title = stringResource(R.string.settings),
            onLeftIconClick = onBack,
            showLeftIcon = true
        )

        Column(
            modifier = Modifier.padding(horizontal = 17.dp)
        ) {
            Spacer(Modifier.padding(vertical = 5.dp))
            GetThemeChangeSection(mainViewmodel)
            Spacer(Modifier.padding(vertical = 15.dp))
            GetReminderSection()
            Spacer(Modifier.padding(vertical = 15.dp))
            GetPrivacySection()
            Spacer(Modifier.padding(vertical = 15.dp))
            GetAboutSection()
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

@Composable
fun GetPrivacySection() {
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
                onClick = {}
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
                onClick = {}
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
}

@Composable
fun GetThemeItem(type: ThemeUtil, mainViewmodel: MainViewmodel) {
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
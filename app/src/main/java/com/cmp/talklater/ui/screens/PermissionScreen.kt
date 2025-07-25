package com.cmp.talklater.ui.screens

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cmp.talklater.R
import com.cmp.talklater.ui.components.Button
import com.cmp.talklater.util.AppUtils.openNotificationListenerSettings
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.cmp.talklater.util.Utils

@Composable
fun PermissionScreen(onPermissionGranted: () -> Unit, onPermissionDenied: () -> Unit) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val imageHeight = screenHeight * 0.65f
    val context = LocalContext.current
    var isEnabled by remember { mutableStateOf(Utils.isNotificationServiceEnabled(context)) }

    LaunchedEffect(isEnabled) {
        if (isEnabled) onPermissionGranted()
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                isEnabled = Utils.isNotificationServiceEnabled(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.verticalScroll(state = rememberScrollState())
    ) {
        Image(
            painter = painterResource(id = R.drawable.permission_image_png),
            contentDescription = "Permission image",
            modifier = Modifier
                .height(imageHeight)
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            "Permissions Required", style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(15.dp))
        Text(
            stringResource(R.string.permission_desc),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.width(350.dp),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(15.dp))
        Button(
            name = "Allow",
            fillColor = MaterialTheme.colorScheme.primary,
            fillMaxWidth = true,
            padding = 12,
            textStyle = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 12.dp),
            borderRadius = 12,
            textColor = MaterialTheme.colorScheme.surface,
            onClick = {
                openNotificationListenerSettings(context)
            }
        )
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            name = "Not Now",
            fillColor = MaterialTheme.colorScheme.primaryContainer,
            fillMaxWidth = true,
            padding = 12,
            textStyle = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 12.dp),
            borderRadius = 12,
            textColor = MaterialTheme.colorScheme.inverseSurface,
            onClick = onPermissionDenied
        )
        Spacer(modifier = Modifier.height(15.dp))

    }
}
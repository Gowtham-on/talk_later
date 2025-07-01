package com.cmp.talklater.ui.screens

import android.Manifest
import androidx.activity.compose.LocalActivity
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.cmp.talklater.R
import com.cmp.talklater.ui.components.Button
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.mohamedrejeb.calf.permissions.ExperimentalPermissionsApi

@OptIn(ExperimentalPermissionsApi::class,
    com.google.accompanist.permissions.ExperimentalPermissionsApi::class
)
@Composable
fun PermissionScreen(onPermissionGranted: () -> Unit, onPermissionDenied: () -> Unit) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val imageHeight = screenHeight * 0.65f
    val permissionState = rememberPermissionState(Manifest.permission.READ_CALL_LOG)

    LaunchedEffect(permissionState.status) {
        if (permissionState.status.isGranted) {
            onPermissionGranted()
        } else if (permissionState.status.shouldShowRationale.not()
            && !permissionState.status.isGranted
        ) {
            onPermissionDenied()
        }
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
                try {
                    permissionState.launchPermissionRequest()
                } catch (e: Exception) {
                    println()
                }
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
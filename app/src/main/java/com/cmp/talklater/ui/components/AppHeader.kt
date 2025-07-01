package com.cmp.talklater.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun AppHeader(
    modifier: Modifier = Modifier,
    title: String,
    showLeftIcon: Boolean = false,
    onLeftIconClick: () -> Unit = {},
    leftIcon: ImageVector? = null,
) {
    Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.background)
                .padding(top = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (showLeftIcon)
                Image(
                    alignment = Alignment.CenterStart,
                    imageVector = leftIcon ?: Icons.AutoMirrored.Outlined.KeyboardArrowLeft,
                    contentDescription = "Back Icon",
                    modifier = Modifier
                        .clickable(
                            onClick = onLeftIconClick
                        )
                        .size(50.dp)
                        .padding(10.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.inverseSurface)
                )
            else {
                Spacer(modifier = Modifier.width(48.dp))
            }
            Spacer(modifier = Modifier.width(48.dp))
        }
        Text(
            title,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp),
            fontWeight = FontWeight.Bold
        )
    }

}
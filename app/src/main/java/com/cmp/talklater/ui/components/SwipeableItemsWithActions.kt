package com.cmp.talklater.ui.components

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun SwipeableItemsWithAction(
    modifier: Modifier = Modifier,
    isRevealed: Boolean = false,
    actions: @Composable RowScope.() -> Unit,
    onExpanded: () -> Unit = {},
    onCollapsed: () -> Unit = {},
    content: @Composable () -> Unit,
    isSwipeable: Boolean = true,
) {

    var contextMenuWidth by remember {
        mutableFloatStateOf(0f)
    }

    val offset = remember {
        Animatable(initialValue = 0f)
    }

    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = isRevealed, contextMenuWidth) {
        if (isRevealed) {
            offset.animateTo(contextMenuWidth)
        } else {
            offset.animateTo(0f)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        Row(
            modifier = Modifier.align(Alignment.CenterEnd).onSizeChanged {
                contextMenuWidth = it.width.toFloat()
            },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            actions()
        }
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (isSwipeable) {
                        Modifier
                            .offset { -IntOffset(offset.value.roundToInt(), 0) }
                            .pointerInput(contextMenuWidth) {
                                detectHorizontalDragGestures(
                                    onHorizontalDrag = { _, dragAmount ->
                                        scope.launch {
                                            val newOffset =
                                                (offset.value - dragAmount).coerceIn(0f, contextMenuWidth)
                                            offset.snapTo(newOffset)
                                        }
                                    },
                                    onDragEnd = {
                                        when {
                                            offset.value >= contextMenuWidth / 2 -> {
                                                scope.launch {
                                                    offset.animateTo(contextMenuWidth)
                                                    onExpanded()
                                                }
                                            }
                                            else -> {
                                                scope.launch {
                                                    offset.animateTo(0f)
                                                    onCollapsed()
                                                }
                                            }
                                        }
                                    }
                                )
                            }
                    } else {
                        Modifier
                    }
                )

        ) {
            content()
        }
    }
}


@Composable
fun ActionIcon(
    onClick: () -> Unit,
    backgroundColor: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    tint: Color = Color.White,
    contentDescription: String? = null,
) {

    IconButton(
        onClick,
        modifier.background(backgroundColor)
    ) {
        Icon(
            imageVector = icon,
            contentDescription,
            tint = tint
        )
    }
}
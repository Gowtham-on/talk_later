package com.cmp.talklater.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
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
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.collections.chunked
import kotlin.collections.forEach
import kotlin.collections.last

@Composable
fun Button(
    name: String,
    borderRadius: Int? = null,
    borderWidth: Int? = null,
    fillColor: Color? = null,
    borderColor: Color? = null,
    textColor: Color? = null,
    width: Int? = null,
    height: Int? = null,
    animation: ButtonAnimation? = null,
    paddingHorizontal: Int? = null,
    paddingVertical: Int? = null,
    padding: Int? = null,
    paddingTop: Int? = null,
    paddingBottom: Int? = null,
    paddingLeft: Int? = null,
    paddingRight: Int? = null,
    enableHapticFeedback: Boolean = false,
    textStyle: TextStyle? = null,
    fontWeight: FontWeight? = null,
    fillMaxWidth: Boolean = false,
    textAlignment: TextAlign = TextAlign.Center,
    textContentAlignment: Alignment = Alignment.Center,
    animationIncreaseSize: Float = 1.15f,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {

    val haptic = LocalView.current

    val animWidth by animateFloatAsState(
        targetValue = if (animation == null) 1f else animationIncreaseSize,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    Box(
        modifier = modifier
            .width(width?.dp ?: Dp.Unspecified)
            .height(height?.dp ?: Dp.Unspecified)
            .graphicsLayer {
                scaleX = animWidth
                scaleY = animWidth
            }
            .then(if (fillMaxWidth) Modifier.fillMaxWidth() else Modifier)
            .clip(RoundedCornerShape(borderRadius ?: 0))
            .clickable(
                onClick = {
                    onClick()
                    if (enableHapticFeedback) {
                        haptic.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                    }
                }
            )
            .background(
                color = fillColor ?: Color.Transparent,
                shape = RoundedCornerShape(borderRadius ?: 0),
            )
            .border(
                width = borderWidth?.dp ?: Dp.Unspecified,
                color = borderColor ?: Color.Transparent,
                shape = RoundedCornerShape(borderRadius ?: 0),
            )

    ) {
        Text(
            text = name,
            color = textColor ?: Color.Black,
            style = textStyle ?: LocalTextStyle.current,
            fontWeight = fontWeight,
            textAlign = textAlignment,
            modifier = Modifier
                .align(textContentAlignment)
                .padding(all = padding?.dp ?: Dp.Unspecified)
                .padding(
                    horizontal = paddingHorizontal?.dp ?: Dp.Unspecified,
                    vertical = paddingVertical?.dp ?: Dp.Unspecified
                )
                .padding(
                    start = paddingLeft?.dp ?: Dp.Unspecified,
                    end = paddingRight?.dp ?: Dp.Unspecified,
                    top = paddingTop?.dp ?: Dp.Unspecified,
                    bottom = paddingBottom?.dp ?: Dp.Unspecified
                )
                .then(
                    if (fillMaxWidth) Modifier.fillMaxWidth() else Modifier
                )
        )
    }
}

enum class ButtonAnimation {
    SPRING
}

@Preview(showBackground = true)
@Composable
fun MHButtonPreview() {
    var selectedButton by remember { mutableIntStateOf(0) }
    val buttonData = listOf(
        Pair(1, "One Min"),
        Pair(2, "Two Min"),
        Pair(3, "Three Min"),
        Pair(4, "Four Min"),
    )

    Column {
        buttonData.chunked(2).forEach { rowButtons ->
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 10.dp,
                        vertical = 10.dp
                    )
            ) {
                rowButtons.forEach { (id, name) ->
                    Button(
                        name = name, borderRadius = 50,
                        borderWidth = 1,
                        fillColor = if (selectedButton == id) Color.LightGray else Color.White,
                        borderColor = Color.Black,
                        textColor = Color.DarkGray,
                        animation = if (selectedButton == id) ButtonAnimation.SPRING else null,
                        width = 130,
                        enableHapticFeedback = true,
                    ) { selectedButton = id }
                    if (id != rowButtons.last().first) Spacer(modifier = Modifier.width(20.dp))
                }
            }
        }
    }

}
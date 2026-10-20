package com.github.curiousnom.kernelflasher.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@ExperimentalUnitApi
@Composable
fun ColumnScope.FlashList(
    cardTitle: String,
    output: List<String>,
    isRunning: Boolean = false,
    isSuccess: Boolean? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val listState = rememberLazyListState()
    var hasDragged by remember { mutableStateOf(false) }
    val isDragged by listState.interactionSource.collectIsDraggedAsState()
    if (isDragged) {
        hasDragged = true
    }
    LaunchedEffect(output.size) {
        if (output.isNotEmpty() && !hasDragged) {
            listState.scrollToItem(output.size - 1)
        }
    }

    DataCard(
        title = cardTitle,
        button = {
            if (isRunning) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(12.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Running",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            } else if (isSuccess == true) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "Completed",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            } else if (isSuccess == false) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.errorContainer
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Error,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "Failed",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        }
    )

    AnimatedVisibility(
        visible = isRunning,
        enter = fadeIn(tween(180)) + expandVertically(tween(200)),
        exit = fadeOut(tween(180)) + shrinkVertically(tween(200))
    ) {
        Column {
            Spacer(Modifier.height(6.dp))
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceContainerHighest
            )
        }
    }

    Spacer(Modifier.height(8.dp))

    // Material 3 log container with colorful syntax highlighting
    Surface(
        modifier = Modifier
            .weight(1.0f)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .scrollbar(listState),
            state = listState
        ) {
            itemsIndexed(output) { _, message ->
                val textColor = when {
                    message.contains("error", ignoreCase = true) || message.contains("fail", ignoreCase = true) -> Color(0xFFFF6B6B)
                    message.contains("success", ignoreCase = true) || message.contains("done", ignoreCase = true) -> Color(0xFF66BB6A)
                    message.startsWith("!") || message.startsWith("*") -> Color(0xFFFFA726)
                    message.startsWith("-") || message.startsWith("+") -> Color(0xFF42A5F5)
                    else -> MaterialTheme.colorScheme.onSurface
                }
                Text(
                    text = message,
                    color = textColor,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(vertical = 1.dp)
                )
            }
        }
    }
    Spacer(Modifier.height(12.dp))
    content()
}

fun Modifier.scrollbar(
    state: LazyListState,
    width: Dp = 4.dp
): Modifier = composed {
    var visibleItemsCountChanged = false
    var visibleItemsCount by remember { mutableIntStateOf(state.layoutInfo.visibleItemsInfo.size) }
    if (visibleItemsCount != state.layoutInfo.visibleItemsInfo.size) {
        visibleItemsCountChanged = true
        visibleItemsCount = state.layoutInfo.visibleItemsInfo.size
    }

    val hidden = state.layoutInfo.visibleItemsInfo.size == state.layoutInfo.totalItemsCount
    val targetAlpha = if (!hidden && (state.isScrollInProgress || visibleItemsCountChanged)) 0.5f else 0f
    val delay = if (!hidden && (state.isScrollInProgress || visibleItemsCountChanged)) 0 else 250
    val duration = if (hidden || visibleItemsCountChanged) 0 else if (state.isScrollInProgress) 150 else 500

    val alpha by animateFloatAsState(
        targetValue = targetAlpha,
        animationSpec = tween(delayMillis = delay, durationMillis = duration),
        label = "scrollbarAlpha"
    )

    val thumbColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)

    drawWithContent {
        drawContent()

        val firstVisibleElementIndex = state.layoutInfo.visibleItemsInfo.firstOrNull()?.index
        val needDrawScrollbar = state.isScrollInProgress || visibleItemsCountChanged || alpha > 0.0f

        if (needDrawScrollbar && firstVisibleElementIndex != null && state.layoutInfo.totalItemsCount > 0) {
            val elementHeight = this.size.height / state.layoutInfo.totalItemsCount
            val scrollbarOffsetY = firstVisibleElementIndex * elementHeight
            val scrollbarHeight = state.layoutInfo.visibleItemsInfo.size * elementHeight

            drawRoundRect(
                color = thumbColor,
                topLeft = Offset(this.size.width - width.toPx(), scrollbarOffsetY),
                size = Size(width.toPx(), scrollbarHeight),
                cornerRadius = CornerRadius(width.toPx(), width.toPx()),
                alpha = alpha
            )
        }
    }
}

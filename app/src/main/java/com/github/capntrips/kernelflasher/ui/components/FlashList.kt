package com.github.capntrips.kernelflasher.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.rounded.Terminal
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@ExperimentalUnitApi
@Composable
fun ColumnScope.FlashList(
    cardTitle: String,
    output: List<String>,
    content: @Composable ColumnScope.() -> Unit
) {
    val listState = rememberLazyListState()
    var hasDragged by remember { mutableStateOf(false) }
    val isDragged by listState.interactionSource.collectIsDraggedAsState()
    if (isDragged) {
        hasDragged = true
    }
    var shouldScroll = false
    if (!hasDragged) {
        val firstVisible = listState.layoutInfo.visibleItemsInfo.firstOrNull()?.index
        if (firstVisible != null) {
            if (listState.layoutInfo.totalItemsCount - listState.layoutInfo.visibleItemsInfo.size > firstVisible) {
                shouldScroll = true
            }
        }
    }
    LaunchedEffect(output.size, shouldScroll) {
        if (output.isNotEmpty()) {
            listState.animateScrollToItem(output.size - 1)
        }
    }

    // Terminal Container
    Surface(
        modifier = Modifier
            .weight(1.0f)
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF0F1117),
        tonalElevation = 2.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF262933))
    ) {
        Column(Modifier.fillMaxSize()) {
            // Header bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF181B24))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF4CAF50))
                    )
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Rounded.Terminal,
                        contentDescription = null,
                        tint = Color(0xFFA8C7FA),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = cardTitle,
                        color = Color(0xFFE2E2E9),
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
                Surface(
                    shape = CircleShape,
                    color = Color(0xFF262933)
                ) {
                    Text(
                        text = "${output.size} lines",
                        color = Color(0xFF8E919A),
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            // Output Log area
            LazyColumn(
                modifier = Modifier
                    .weight(1.0f)
                    .fillMaxSize()
                    .padding(horizontal = 14.dp, vertical = 8.dp)
                    .scrollbar(listState),
                state = listState
            ) {
                itemsIndexed(output) { _, message ->
                    val textColor = when {
                        message.contains("error", ignoreCase = true) || message.contains("fail", ignoreCase = true) -> Color(0xFFFF8585)
                        message.contains("success", ignoreCase = true) || message.contains("done", ignoreCase = true) -> Color(0xFF81C784)
                        message.startsWith("!") || message.startsWith("*") -> Color(0xFFFFB74D)
                        message.startsWith("-") -> Color(0xFF64B5F6)
                        else -> Color(0xFFD6D8E1)
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
    val targetAlpha = if (!hidden && (state.isScrollInProgress || visibleItemsCountChanged)) 0.6f else 0f
    val delay = if (!hidden && (state.isScrollInProgress || visibleItemsCountChanged)) 0 else 250
    val duration = if (hidden || visibleItemsCountChanged) 0 else if (state.isScrollInProgress) 150 else 500

    val alpha by animateFloatAsState(
        targetValue = targetAlpha,
        animationSpec = tween(delayMillis = delay, durationMillis = duration),
        label = "scrollbarAlpha"
    )

    drawWithContent {
        drawContent()

        val firstVisibleElementIndex = state.layoutInfo.visibleItemsInfo.firstOrNull()?.index
        val needDrawScrollbar = state.isScrollInProgress || visibleItemsCountChanged || alpha > 0.0f

        if (needDrawScrollbar && firstVisibleElementIndex != null && state.layoutInfo.totalItemsCount > 0) {
            val elementHeight = this.size.height / state.layoutInfo.totalItemsCount
            val scrollbarOffsetY = firstVisibleElementIndex * elementHeight
            val scrollbarHeight = state.layoutInfo.visibleItemsInfo.size * elementHeight

            drawRoundRect(
                color = Color(0xFFA8C7FA),
                topLeft = Offset(this.size.width - width.toPx(), scrollbarOffsetY),
                size = Size(width.toPx(), scrollbarHeight),
                cornerRadius = CornerRadius(width.toPx(), width.toPx()),
                alpha = alpha
            )
        }
    }
}

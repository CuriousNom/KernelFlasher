package com.github.capntrips.kernelflasher.ui.screens.updates

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.SystemUpdate
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.github.capntrips.kernelflasher.R
import com.github.capntrips.kernelflasher.common.types.room.updates.DateSerializer
import com.github.capntrips.kernelflasher.ui.components.DataCard
import com.github.capntrips.kernelflasher.ui.components.DataRow
import com.github.capntrips.kernelflasher.ui.components.ViewButton
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalMaterial3Api
@ExperimentalSerializationApi
@Composable
fun ColumnScope.UpdatesContent(
    viewModel: UpdatesViewModel,
    navController: NavController
) {
    @Suppress("UNUSED_VARIABLE") val context = LocalContext.current
    DataCard(
        title = stringResource(R.string.updates),
        icon = Icons.Rounded.SystemUpdate
    )

    if (viewModel.updates.isNotEmpty()) {
        for (update in viewModel.updates.sortedByDescending { it.kernelDate }) {
            Spacer(Modifier.height(14.dp))
            DataCard(
                title = update.kernelName,
                icon = Icons.Rounded.SystemUpdate,
                button = {
                    AnimatedVisibility(!viewModel.isRefreshing) {
                        ViewButton(onClick = {
                            navController.navigate("updates/view/${update.id}")
                        })
                    }
                }
            ) {
                val cardWidth = remember { mutableIntStateOf(0) }
                DataRow(stringResource(R.string.version), update.kernelVersion, mutableMaxWidth = cardWidth)
                DataRow(stringResource(R.string.date_released), DateSerializer.formatter.format(update.kernelDate), mutableMaxWidth = cardWidth)
                DataRow(
                    label = stringResource(R.string.last_updated),
                    value = UpdatesViewModel.lastUpdatedFormatter.format(update.lastUpdated!!),
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    labelStyle = MaterialTheme.typography.labelMedium.copy(fontStyle = FontStyle.Italic),
                    valueColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    valueStyle = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
                    mutableMaxWidth = cardWidth
                )
            }
        }
    }

    AnimatedVisibility(!viewModel.isRefreshing) {
        Column {
            Spacer(Modifier.height(16.dp))
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(24.dp),
                onClick = { navController.navigate("updates/add") }
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.add),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

package com.github.capntrips.kernelflasher.ui.screens.updates

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.SystemUpdate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
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
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalMaterial3Api
@ExperimentalSerializationApi
@Composable
fun ColumnScope.UpdatesViewContent(
    viewModel: UpdatesViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    viewModel.currentUpdate?.let { currentUpdate ->
        DataCard(
            title = currentUpdate.kernelName,
            icon = Icons.Rounded.SystemUpdate
        ) {
            val cardWidth = remember { mutableIntStateOf(0) }
            DataRow(stringResource(R.string.version), currentUpdate.kernelVersion, mutableMaxWidth = cardWidth)
            DataRow(stringResource(R.string.date_released), DateSerializer.formatter.format(currentUpdate.kernelDate), mutableMaxWidth = cardWidth)
            DataRow(
                label = stringResource(R.string.last_updated),
                value = UpdatesViewModel.lastUpdatedFormatter.format(currentUpdate.lastUpdated!!),
                labelColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                labelStyle = MaterialTheme.typography.labelMedium.copy(fontStyle = FontStyle.Italic),
                valueColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                valueStyle = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
                mutableMaxWidth = cardWidth
            )
        }
        AnimatedVisibility(!viewModel.isRefreshing) {
            Column {
                Spacer(Modifier.height(14.dp))
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    FilledTonalButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(16.dp),
                        onClick = { viewModel.downloadChangelog { navController.navigate("updates/view/${currentUpdate.id}/changelog") } }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Description,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.changelog),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }

                    // Primary Download Button
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(16.dp),
                        onClick = { viewModel.downloadKernel(context) }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Download,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.download),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }

                    FilledTonalButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(16.dp),
                        onClick = { viewModel.update() }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.check_for_updates),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }

                    FilledTonalButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        ),
                        onClick = { viewModel.delete { navController.popBackStack() } }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.delete),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }
}

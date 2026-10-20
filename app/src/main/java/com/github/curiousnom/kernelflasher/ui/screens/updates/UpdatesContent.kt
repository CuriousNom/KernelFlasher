package com.github.curiousnom.kernelflasher.ui.screens.updates

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.SystemUpdate
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.github.curiousnom.kernelflasher.R
import com.github.curiousnom.kernelflasher.common.types.room.updates.DateSerializer
import com.github.curiousnom.kernelflasher.ui.components.DataCard
import com.github.curiousnom.kernelflasher.ui.components.DataRow
import com.github.curiousnom.kernelflasher.ui.components.ViewButton
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalMaterial3Api
@ExperimentalSerializationApi
@Composable
fun ColumnScope.UpdatesContent(
    viewModel: UpdatesViewModel,
    navController: NavController
) {
    @Suppress("UNUSED_VARIABLE") val context = LocalContext.current

    if (viewModel.updates.isNotEmpty()) {
        for (update in viewModel.updates.sortedByDescending { it.kernelDate }) {
            Spacer(Modifier.height(12.dp))
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

        AnimatedVisibility(!viewModel.isRefreshing) {
            Column {
                Spacer(Modifier.height(14.dp))
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(14.dp),
                    onClick = { navController.navigate("updates/add") }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.add),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    } else {
        Spacer(Modifier.height(36.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                modifier = Modifier.size(64.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Rounded.SystemUpdate,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }
            Spacer(Modifier.height(14.dp))
            Text(
                text = "No updates tracked",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Add a kernel update source URL to track new releases",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
            Spacer(Modifier.height(20.dp))
            Button(
                onClick = { navController.navigate("updates/add") },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.height(44.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
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

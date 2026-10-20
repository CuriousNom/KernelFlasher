package com.github.capntrips.kernelflasher.ui.screens.backups

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Backup
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.FolderOff
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.github.capntrips.kernelflasher.R
import com.github.capntrips.kernelflasher.common.PartitionUtil
import com.github.capntrips.kernelflasher.ui.components.DataCard
import com.github.capntrips.kernelflasher.ui.components.DataRow
import com.github.capntrips.kernelflasher.ui.components.DataSet
import com.github.capntrips.kernelflasher.ui.components.ViewButton

@Suppress("DEPRECATION")
@ExperimentalMaterial3Api
@Composable
fun ColumnScope.BackupsContent(
    viewModel: BackupsViewModel,
    navController: NavController
) {
    val context = LocalContext.current

    if (viewModel.currentBackup != null && viewModel.backups.containsKey(viewModel.currentBackup)) {
        DataCard(
            title = viewModel.currentBackup!!,
            icon = Icons.Rounded.Backup
        ) {
            val cardWidth = remember { mutableIntStateOf(0) }
            val currentBackup = viewModel.backups.getValue(viewModel.currentBackup!!)
            DataRow(stringResource(R.string.backup_type), currentBackup.type, mutableMaxWidth = cardWidth)
            DataRow(stringResource(R.string.kernel_version), currentBackup.kernelVersion, mutableMaxWidth = cardWidth, clickable = true)
            if (currentBackup.type == "raw") {
                DataRow(
                    label = stringResource(R.string.boot_sha1),
                    value = currentBackup.bootSha1?.take(8) ?: "",
                    valueStyle = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Medium
                    ),
                    mutableMaxWidth = cardWidth
                )
                if (currentBackup.hashes != null) {
                    val hashWidth = remember { mutableIntStateOf(0) }
                    DataSet(stringResource(R.string.hashes)) {
                        for (partitionName in PartitionUtil.PartitionNames) {
                            val hash = currentBackup.hashes.get(partitionName)
                            if (hash != null) {
                                DataRow(
                                    label = partitionName,
                                    value = hash.take(8),
                                    valueStyle = MaterialTheme.typography.bodyMedium.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    mutableMaxWidth = hashWidth
                                )
                            }
                        }
                    }
                }
            }
        }
        AnimatedVisibility(!viewModel.isRefreshing) {
            Column {
                Spacer(Modifier.height(14.dp))
                FilledTonalButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    ),
                    onClick = { viewModel.delete(context) { navController.popBackStack() } }
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
    } else {
        DataCard(
            title = stringResource(R.string.backups),
            icon = Icons.Rounded.Backup
        )

        AnimatedVisibility(viewModel.needsMigration) {
            Column {
                Spacer(Modifier.height(12.dp))
                FilledTonalButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    onClick = { viewModel.migrate(context) }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Sync,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.migrate),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }

        if (viewModel.backups.isNotEmpty()) {
            for (id in viewModel.backups.keys.sortedByDescending { it }) {
                val currentBackup = viewModel.backups[id]!!
                Spacer(Modifier.height(14.dp))
                DataCard(
                    title = id,
                    icon = Icons.Rounded.Backup,
                    button = {
                        AnimatedVisibility(!viewModel.isRefreshing) {
                            ViewButton(onClick = {
                                navController.navigate("backups/$id")
                            })
                        }
                    }
                ) {
                    val cardWidth = remember { mutableIntStateOf(0) }
                    if (currentBackup.type == "raw") {
                        DataRow(
                            label = stringResource(R.string.boot_sha1),
                            value = currentBackup.bootSha1?.take(8) ?: "",
                            valueStyle = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Medium
                            ),
                            mutableMaxWidth = cardWidth
                        )
                    }
                    DataRow(stringResource(R.string.kernel_version), currentBackup.kernelVersion, mutableMaxWidth = cardWidth, clickable = true)
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
                Icon(
                    imageVector = Icons.Rounded.FolderOff,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.no_backups_found),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

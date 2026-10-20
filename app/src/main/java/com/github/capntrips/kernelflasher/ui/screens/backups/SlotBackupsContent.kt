package com.github.capntrips.kernelflasher.ui.screens.backups

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Backup
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.FolderOff
import androidx.compose.material.icons.rounded.PowerSettingsNew
import androidx.compose.material.icons.rounded.Restore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.github.capntrips.kernelflasher.R
import com.github.capntrips.kernelflasher.common.PartitionUtil
import com.github.capntrips.kernelflasher.ui.components.DataCard
import com.github.capntrips.kernelflasher.ui.components.DataRow
import com.github.capntrips.kernelflasher.ui.components.DataSet
import com.github.capntrips.kernelflasher.ui.components.FlashList
import com.github.capntrips.kernelflasher.ui.components.SlotCard
import com.github.capntrips.kernelflasher.ui.components.ViewButton
import com.github.capntrips.kernelflasher.ui.screens.slot.SlotViewModel

@ExperimentalMaterial3Api
@ExperimentalUnitApi
@Composable
fun ColumnScope.SlotBackupsContent(
    slotViewModel: SlotViewModel,
    backupsViewModel: BackupsViewModel,
    slotSuffix: String,
    navController: NavController
) {
    val context = LocalContext.current
    val currentRoute = navController.currentDestination?.route ?: ""

    if (!currentRoute.contains("/backups/{backupId}/restore")) {
        SlotCard(
            title = stringResource(
                if (slotSuffix == "_a") R.string.slot_a
                else if (slotSuffix == "_b") R.string.slot_b
                else R.string.slot
            ),
            viewModel = slotViewModel,
            navController = navController,
            isSlotScreen = true,
            showDlkm = false,
        )
        Spacer(Modifier.height(16.dp))

        if (backupsViewModel.currentBackup != null && backupsViewModel.backups.containsKey(backupsViewModel.currentBackup)) {
            val currentBackup = backupsViewModel.backups.getValue(backupsViewModel.currentBackup!!)
            DataCard(
                title = backupsViewModel.currentBackup!!,
                icon = Icons.Rounded.Backup
            ) {
                val cardWidth = remember { mutableIntStateOf(0) }
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
            AnimatedVisibility(!slotViewModel.isRefreshing) {
                Column {
                    Spacer(Modifier.height(12.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        if (slotViewModel.isActive) {
                            if (currentBackup.type == "raw") {
                                Button(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp),
                                    shape = RoundedCornerShape(24.dp),
                                    onClick = {
                                        navController.navigate("slot$slotSuffix/backups/${backupsViewModel.currentBackup!!}/restore")
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Restore,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = stringResource(R.string.restore),
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                }
                            } else if (currentBackup.type == "ak3") {
                                Button(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp),
                                    shape = RoundedCornerShape(24.dp),
                                    onClick = {
                                        slotViewModel.flashAk3(context, backupsViewModel.currentBackup!!, currentBackup.filename!!)
                                        navController.navigate("slot$slotSuffix/backups/${backupsViewModel.currentBackup!!}/flash/ak3") {
                                            popUpTo("slot$slotSuffix")
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Bolt,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = stringResource(R.string.flash),
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                }
                            }
                        }
                        FilledTonalButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                contentColor = MaterialTheme.colorScheme.onErrorContainer
                            ),
                            onClick = { backupsViewModel.delete(context) { navController.popBackStack() } }
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
        } else {
            DataCard(
                title = stringResource(R.string.backups),
                icon = Icons.Rounded.Backup
            )
            val backups = backupsViewModel.backups.filter { it.value.bootSha1.equals(slotViewModel.sha1) || it.value.type == "ak3" }
            if (backups.isNotEmpty()) {
                for (id in backups.keys.sortedByDescending { it }) {
                    Spacer(Modifier.height(14.dp))
                    DataCard(
                        title = id,
                        icon = Icons.Rounded.Backup,
                        button = {
                            AnimatedVisibility(!slotViewModel.isRefreshing) {
                                ViewButton(onClick = {
                                    navController.navigate("slot$slotSuffix/backups/$id")
                                })
                            }
                        }
                    ) {
                        DataRow(stringResource(R.string.kernel_version), backups[id]!!.kernelVersion, clickable = true)
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
    } else if (currentRoute.endsWith("/backups/{backupId}/restore")) {
        DataCard(
            title = stringResource(R.string.restore),
            icon = Icons.Rounded.Restore
        ) {
            val currentBackup = backupsViewModel.backups.getValue(backupsViewModel.currentBackup!!)
            if (currentBackup.hashes != null) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (partitionName in PartitionUtil.PartitionNames) {
                        val hash = currentBackup.hashes.get(partitionName)
                        if (hash != null) {
                            val isSelected = backupsViewModel.backupPartitions[partitionName] == true
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        backupsViewModel.backupPartitions[partitionName] = !isSelected
                                    },
                                shape = RoundedCornerShape(16.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainer,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                                    else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = isSelected,
                                        onCheckedChange = { checked ->
                                            backupsViewModel.backupPartitions[partitionName] = checked
                                        },
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = MaterialTheme.colorScheme.primary
                                        )
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = partitionName,
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                        ),
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                Text(
                    text = stringResource(R.string.partition_selection_unavailable),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(12.dp))
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(24.dp),
                onClick = {
                    backupsViewModel.restore(context, slotSuffix)
                    navController.navigate("slot$slotSuffix/backups/${backupsViewModel.currentBackup!!}/restore/restore") {
                        popUpTo("slot$slotSuffix")
                    }
                },
                enabled = currentBackup.hashes == null || (PartitionUtil.PartitionNames.none { currentBackup.hashes.get(it) != null && backupsViewModel.backupPartitions[it] == null } && backupsViewModel.backupPartitions.filter { it.value }.isNotEmpty())
            ) {
                Icon(
                    imageVector = Icons.Rounded.Restore,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.restore),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    } else {
        FlashList(
            cardTitle = stringResource(R.string.restore),
            output = backupsViewModel.restoreOutput
        ) {
            AnimatedVisibility(!backupsViewModel.isRefreshing && backupsViewModel.wasRestored != null) {
                Column {
                    if (backupsViewModel.wasRestored != false) {
                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            onClick = { navController.navigate("reboot") }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.PowerSettingsNew,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.reboot),
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }
        }
    }
}

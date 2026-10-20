package com.github.capntrips.kernelflasher.ui.screens.slot

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Archive
import androidx.compose.material.icons.rounded.Backup
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.material.icons.rounded.FolderZip
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.PowerSettingsNew
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.github.capntrips.kernelflasher.MainActivity
import com.github.capntrips.kernelflasher.R
import com.github.capntrips.kernelflasher.common.PartitionUtil
import com.github.capntrips.kernelflasher.ui.components.DataCard
import com.github.capntrips.kernelflasher.ui.components.FlashButton
import com.github.capntrips.kernelflasher.ui.components.FlashList
import com.github.capntrips.kernelflasher.ui.components.SlotCard
import com.github.capntrips.kernelflasher.ui.components.bouncyClickable
import kotlinx.serialization.ExperimentalSerializationApi

@OptIn(ExperimentalLayoutApi::class)
@ExperimentalAnimationApi
@ExperimentalMaterialApi
@ExperimentalMaterial3Api
@ExperimentalSerializationApi
@ExperimentalUnitApi
@Composable
fun ColumnScope.SlotFlashContent(
    viewModel: SlotViewModel,
    slotSuffix: String,
    navController: NavController
) {
    val context = LocalContext.current
    val mainActivity = context as MainActivity
    val currentRoute = navController.currentDestination?.route ?: ""

    val ak3Result = remember { mutableStateOf<Uri?>(null) }
    val ak3Launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        ak3Result.value = uri
        if (uri == null) {
            mainActivity.isAwaitingResult = false
        }
    }
    ak3Result.value?.let { uri ->
        if (mainActivity.isAwaitingResult) {
            navController.navigate("slot$slotSuffix/flash/ak3") {
                popUpTo("slot$slotSuffix")
            }
            viewModel.flashAk3(context, uri)
        }
        mainActivity.isAwaitingResult = false
    }

    if (!listOf("/flash/ak3", "/flash/image/flash", "/backup/backup").any { currentRoute.endsWith(it) }) {
        SlotCard(
            title = stringResource(
                if (slotSuffix == "_a") R.string.slot_a
                else if (slotSuffix == "_b") R.string.slot_b
                else R.string.slot
            ),
            viewModel = viewModel,
            navController = navController,
            isSlotScreen = true,
            showDlkm = false
        )
        Spacer(Modifier.height(16.dp))

        if (currentRoute.endsWith("/flash")) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Hero Flash AK3 Zip Banner
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .bouncyClickable {
                            mainActivity.isAwaitingResult = true
                            ak3Launcher.launch("*/*")
                        },
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Rounded.FolderZip,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = stringResource(R.string.flash_ak3_zip),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "Select any .zip kernel installer",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.Rounded.FolderOpen,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Partition Image Banner
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .bouncyClickable { navController.navigate("slot$slotSuffix/flash/image") },
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Rounded.Layers,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSecondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = stringResource(R.string.flash_partition_image),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Text(
                                    text = "Flash raw partition images (.img)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        } else if (currentRoute.endsWith("/flash/image")) {
            DataCard(
                title = stringResource(R.string.flash_partition_image),
                icon = Icons.Rounded.Layers
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    for (partitionName in PartitionUtil.AvailablePartitions) {
                        FlashButton(
                            buttonText = partitionName,
                            callback = { uri ->
                                navController.navigate("slot$slotSuffix/flash/image/flash") {
                                    popUpTo("slot$slotSuffix")
                                }
                                viewModel.flashImage(context, uri, partitionName)
                            }
                        )
                    }
                }
            }
        } else if (currentRoute.endsWith("/backup")) {
            DataCard(
                title = stringResource(R.string.backup),
                icon = Icons.Rounded.Backup
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Select partitions to backup",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = "${viewModel.backupPartitions.count { it.value }} selected",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (partitionName in PartitionUtil.AvailablePartitions) {
                            val isSelected = viewModel.backupPartitions[partitionName] == true
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    viewModel.backupPartitions[partitionName] = !isSelected
                                },
                                label = { Text(partitionName) },
                                leadingIcon = if (isSelected) {
                                    {
                                        Icon(
                                            imageVector = Icons.Rounded.Check,
                                            contentDescription = null,
                                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                                        )
                                    }
                                } else null
                            )
                        }
                    }

                    Spacer(Modifier.height(4.dp))
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        onClick = {
                            viewModel.backup(context)
                            navController.navigate("slot$slotSuffix/backup/backup") {
                                popUpTo("slot$slotSuffix")
                            }
                        },
                        enabled = viewModel.backupPartitions.filter { it.value }.isNotEmpty()
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Backup,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.backup),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    } else {
        val isRunning = viewModel.isRefreshing || viewModel.wasFlashSuccess == null
        val isSuccess = viewModel.wasFlashSuccess
        FlashList(
            cardTitle = stringResource(
                if (currentRoute.endsWith("/backup/backup")) R.string.backup else R.string.flash
            ),
            output = if (currentRoute.contains("ak3")) viewModel.uiPrintedOutput else viewModel.flashOutput,
            isRunning = isRunning,
            isSuccess = isSuccess
        ) {
            AnimatedVisibility(
                visible = !isRunning && isSuccess != null,
                enter = fadeIn(tween(220)) + expandVertically(tween(240)),
                exit = fadeOut(tween(180)) + shrinkVertically(tween(200))
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (currentRoute.contains("ak3")) {
                        FilledTonalButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(16.dp),
                            onClick = { viewModel.saveLog(context) }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Save,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.save_ak3_log),
                                style = MaterialTheme.typography.labelLarge
                            )
                        }

                        AnimatedVisibility(
                            visible = !currentRoute.endsWith("/backups/{backupId}/flash/ak3") && viewModel.wasFlashSuccess != false
                        ) {
                            FilledTonalButton(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                ),
                                onClick = {
                                    viewModel.backupZip(context) {
                                        navController.navigate("slot$slotSuffix/backups") {
                                            popUpTo("slot$slotSuffix")
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Archive,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = stringResource(R.string.save_ak3_zip_as_backup),
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }
                    }

                    if (viewModel.wasFlashSuccess != false && currentRoute.endsWith("/backup/backup")) {
                        FilledTonalButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(16.dp),
                            onClick = { navController.popBackStack() }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.back),
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    } else {
                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(16.dp),
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

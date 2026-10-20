package com.github.capntrips.kernelflasher.ui.screens.main

import android.os.Build
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
import androidx.compose.material.icons.automirrored.rounded.Article
import androidx.compose.material.icons.rounded.Backup
import androidx.compose.material.icons.rounded.BugReport
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.PowerSettingsNew
import androidx.compose.material.icons.rounded.Smartphone
import androidx.compose.material.icons.rounded.SystemUpdate
import androidx.compose.material.icons.rounded.Widgets
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.github.capntrips.kernelflasher.R
import com.github.capntrips.kernelflasher.ui.components.DataCard
import com.github.capntrips.kernelflasher.ui.components.DataRow
import com.github.capntrips.kernelflasher.ui.components.SlotCard
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalMaterial3Api
@ExperimentalSerializationApi
@Composable
fun ColumnScope.MainContent(
    viewModel: MainViewModel,
    navController: NavController
) {
    val context = LocalContext.current

    // Device Information Card
    DataCard(
        title = stringResource(R.string.device),
        icon = Icons.Rounded.Smartphone
    ) {
        val cardWidth = remember { mutableIntStateOf(0) }
        DataRow(stringResource(R.string.model), "${Build.MODEL} (${Build.DEVICE})", mutableMaxWidth = cardWidth)
        DataRow(stringResource(R.string.build_number), Build.ID, mutableMaxWidth = cardWidth)
        DataRow(stringResource(R.string.kernel_version), viewModel.kernelVersion, mutableMaxWidth = cardWidth, clickable = true)
        if (viewModel.isAb) {
            DataRow(stringResource(R.string.slot_suffix), viewModel.slotSuffix, mutableMaxWidth = cardWidth)
        }
    }

    Spacer(Modifier.height(16.dp))

    // Slot A Card
    SlotCard(
        title = stringResource(if (viewModel.isAb) R.string.slot_a else R.string.slot),
        viewModel = viewModel.slotA,
        navController = navController
    )

    // Slot B Card (if A/B device)
    if (viewModel.isAb) {
        Spacer(Modifier.height(16.dp))
        SlotCard(
            title = stringResource(R.string.slot_b),
            viewModel = viewModel.slotB!!,
            navController = navController
        )
    }

    Spacer(Modifier.height(16.dp))

    // Actions Card with colorful Material You buttons
    AnimatedVisibility(!viewModel.isRefreshing) {
        Column {
            DataCard(
                title = stringResource(R.string.actions),
                icon = Icons.Rounded.Widgets
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    FilledTonalButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        ),
                        onClick = { navController.navigate("backups") }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Backup,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = stringResource(R.string.backups),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }

                    FilledTonalButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        ),
                        onClick = { navController.navigate("updates") }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.SystemUpdate,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = stringResource(R.string.updates),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }

                    FilledTonalButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                        ),
                        onClick = { navController.navigate("reboot") }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.PowerSettingsNew,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = stringResource(R.string.reboot),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }

    // Diagnostics Card
    DataCard(
        title = stringResource(R.string.diagnostics),
        icon = Icons.Rounded.BugReport
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            if (viewModel.hasRamoops) {
                FilledTonalButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    onClick = { viewModel.saveRamoops(context) }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Memory,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.save_ramoops),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            FilledTonalButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                onClick = { viewModel.saveDmesg(context) }
            ) {
                Icon(
                    imageVector = Icons.Rounded.Description,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.save_dmesg),
                    style = MaterialTheme.typography.labelLarge
                )
            }

            FilledTonalButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                onClick = { viewModel.saveLogcat(context) }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.Article,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.save_logcat),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

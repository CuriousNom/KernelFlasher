package com.github.capntrips.kernelflasher.ui.screens.main

import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Article
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Backup
import androidx.compose.material.icons.rounded.BugReport
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.PowerSettingsNew
import androidx.compose.material.icons.rounded.Smartphone
import androidx.compose.material.icons.rounded.SystemUpdate
import androidx.compose.material.icons.rounded.Widgets
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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

    // Actions Card
    AnimatedVisibility(!viewModel.isRefreshing) {
        Column {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Column {
                    ActionListItem(
                        title = stringResource(R.string.backups),
                        icon = Icons.Rounded.Backup,
                        iconTint = MaterialTheme.colorScheme.primary,
                        trailingIcon = Icons.AutoMirrored.Rounded.ArrowForward,
                        onClick = { navController.navigate("backups") }
                    )
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    ActionListItem(
                        title = stringResource(R.string.updates),
                        icon = Icons.Rounded.SystemUpdate,
                        iconTint = MaterialTheme.colorScheme.secondary,
                        trailingIcon = Icons.AutoMirrored.Rounded.ArrowForward,
                        onClick = { navController.navigate("updates") }
                    )
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    ActionListItem(
                        title = stringResource(R.string.reboot),
                        icon = Icons.Rounded.PowerSettingsNew,
                        iconTint = MaterialTheme.colorScheme.tertiary,
                        trailingIcon = Icons.AutoMirrored.Rounded.ArrowForward,
                        onClick = { navController.navigate("reboot") }
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }

    // Diagnostics Card
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Column {
            if (viewModel.hasRamoops) {
                ActionListItem(
                    title = stringResource(R.string.save_ramoops),
                    icon = Icons.Rounded.Memory,
                    iconTint = MaterialTheme.colorScheme.secondary,
                    trailingIcon = Icons.Rounded.Download,
                    onClick = { viewModel.saveRamoops(context) }
                )
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            ActionListItem(
                title = stringResource(R.string.save_dmesg),
                icon = Icons.Rounded.Description,
                iconTint = MaterialTheme.colorScheme.secondary,
                trailingIcon = Icons.Rounded.Download,
                onClick = { viewModel.saveDmesg(context) }
            )
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            ActionListItem(
                title = stringResource(R.string.save_logcat),
                icon = Icons.AutoMirrored.Rounded.Article,
                iconTint = MaterialTheme.colorScheme.secondary,
                trailingIcon = Icons.Rounded.Download,
                onClick = { viewModel.saveLogcat(context) }
            )
        }
    }
}

@Composable
private fun ActionListItem(
    title: String,
    icon: ImageVector,
    iconTint: Color,
    trailingIcon: ImageVector,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        leadingContent = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )
        },
        trailingContent = {
            Icon(
                imageVector = trailingIcon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(18.dp)
            )
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        modifier = Modifier.clickable(onClick = onClick)
    )
}

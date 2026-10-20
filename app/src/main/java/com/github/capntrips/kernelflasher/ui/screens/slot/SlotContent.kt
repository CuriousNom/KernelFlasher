package com.github.capntrips.kernelflasher.ui.screens.slot

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
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
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.HelpOutline
import androidx.compose.material.icons.rounded.Backup
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.Restore
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.github.capntrips.kernelflasher.R
import com.github.capntrips.kernelflasher.ui.components.SlotCard

@ExperimentalAnimationApi
@ExperimentalMaterial3Api
@ExperimentalUnitApi
@Composable
fun ColumnScope.SlotContent(
    viewModel: SlotViewModel,
    slotSuffix: String,
    navController: NavController
) {
    val context = LocalContext.current
    val title = stringResource(
        if (slotSuffix == "_a") R.string.slot_a
        else if (slotSuffix == "_b") R.string.slot_b
        else R.string.slot
    )

    SlotCard(
        title = title,
        viewModel = viewModel,
        navController = navController,
        isSlotScreen = true
    )

    AnimatedVisibility(!viewModel.isRefreshing) {
        Column {
            Spacer(Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Column {
                    if (viewModel.isActive) {
                        SlotActionListItem(
                            title = stringResource(R.string.flash),
                            icon = Icons.Rounded.Bolt,
                            iconTint = MaterialTheme.colorScheme.primary,
                            onClick = { navController.navigate("slot$slotSuffix/flash") }
                        )
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }

                    SlotActionListItem(
                        title = stringResource(R.string.backup),
                        icon = Icons.Rounded.Backup,
                        iconTint = MaterialTheme.colorScheme.secondary,
                        onClick = {
                            viewModel.clearFlash(context)
                            navController.navigate("slot$slotSuffix/backup")
                        }
                    )

                    if (viewModel.isActive) {
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        SlotActionListItem(
                            title = stringResource(R.string.restore),
                            icon = Icons.Rounded.Restore,
                            iconTint = MaterialTheme.colorScheme.secondary,
                            onClick = { navController.navigate("slot$slotSuffix/backups") }
                        )
                    }

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    SlotActionListItem(
                        title = stringResource(R.string.check_kernel_version),
                        icon = Icons.AutoMirrored.Rounded.HelpOutline,
                        iconTint = MaterialTheme.colorScheme.onSurfaceVariant,
                        onClick = { if (!viewModel.isRefreshing) viewModel.getKernel(context) }
                    )
                }
            }

            if (viewModel.hasVendorDlkm) {
                Spacer(Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                ) {
                    Column {
                        AnimatedVisibility(viewModel.isVendorDlkmMounted) {
                            SlotActionListItem(
                                title = stringResource(R.string.unmount_vendor_dlkm),
                                icon = Icons.Rounded.Layers,
                                iconTint = MaterialTheme.colorScheme.secondary,
                                onClick = { viewModel.unmountVendorDlkm(context) }
                            )
                        }
                        AnimatedVisibility(!viewModel.isVendorDlkmMounted && viewModel.isVendorDlkmMapped) {
                            Column {
                                SlotActionListItem(
                                    title = stringResource(R.string.mount_vendor_dlkm),
                                    icon = Icons.Rounded.Layers,
                                    iconTint = MaterialTheme.colorScheme.secondary,
                                    onClick = { viewModel.mountVendorDlkm(context) }
                                )
                                HorizontalDivider(
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f),
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                                SlotActionListItem(
                                    title = stringResource(R.string.unmap_vendor_dlkm),
                                    icon = Icons.Rounded.Layers,
                                    iconTint = MaterialTheme.colorScheme.secondary,
                                    onClick = { viewModel.unmapVendorDlkm(context) }
                                )
                            }
                        }
                        AnimatedVisibility(!viewModel.isVendorDlkmMounted && !viewModel.isVendorDlkmMapped) {
                            SlotActionListItem(
                                title = stringResource(R.string.map_vendor_dlkm),
                                icon = Icons.Rounded.Layers,
                                iconTint = MaterialTheme.colorScheme.secondary,
                                onClick = { viewModel.mapVendorDlkm(context) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SlotActionListItem(
    title: String,
    icon: ImageVector,
    iconTint: Color,
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
                imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(18.dp)
            )
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        modifier = Modifier.clickable(onClick = onClick)
    )
}

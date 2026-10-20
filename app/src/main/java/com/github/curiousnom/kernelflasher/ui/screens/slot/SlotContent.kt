package com.github.curiousnom.kernelflasher.ui.screens.slot

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.HelpOutline
import androidx.compose.material.icons.rounded.Backup
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.Restore
import androidx.compose.material.icons.rounded.Widgets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.github.curiousnom.kernelflasher.R
import com.github.curiousnom.kernelflasher.ui.components.DataCard
import com.github.curiousnom.kernelflasher.ui.components.SlotCard
import com.github.curiousnom.kernelflasher.ui.components.pressScale

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
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val sectionSpacing = if (isLandscape) 12.dp else 18.dp
    val buttonHeight = if (isLandscape) 44.dp else 48.dp
    val checkBtnHeight = if (isLandscape) 42.dp else 46.dp

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
            Spacer(Modifier.height(sectionSpacing))

            DataCard(
                title = stringResource(R.string.actions),
                icon = Icons.Rounded.Widgets
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val flashSource = remember { MutableInteractionSource() }
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(buttonHeight)
                            .pressScale(flashSource, scaleDown = 0.96f),
                        interactionSource = flashSource,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        onClick = { navController.navigate("slot$slotSuffix/flash") }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Bolt,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.flash),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val backupSource = remember { MutableInteractionSource() }
                        FilledTonalButton(
                            modifier = Modifier
                                .weight(1f)
                                .height(buttonHeight)
                                .pressScale(backupSource, scaleDown = 0.95f),
                            interactionSource = backupSource,
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            ),
                            onClick = {
                                viewModel.clearFlash(context)
                                navController.navigate("slot$slotSuffix/backup")
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Backup,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = stringResource(R.string.backup),
                                style = MaterialTheme.typography.labelLarge
                            )
                        }

                        val restoreSource = remember { MutableInteractionSource() }
                        FilledTonalButton(
                            modifier = Modifier
                                .weight(1f)
                                .height(buttonHeight)
                                .pressScale(restoreSource, scaleDown = 0.95f),
                            interactionSource = restoreSource,
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            ),
                            onClick = { navController.navigate("slot$slotSuffix/backups") }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Restore,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = stringResource(R.string.restore),
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }

                    val checkSource = remember { MutableInteractionSource() }
                    FilledTonalButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(checkBtnHeight)
                            .pressScale(checkSource, scaleDown = 0.96f),
                        interactionSource = checkSource,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        onClick = { if (!viewModel.isRefreshing) viewModel.getKernel(context) }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.HelpOutline,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.check_kernel_version),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }

            if (viewModel.hasVendorDlkm) {
                Spacer(Modifier.height(sectionSpacing))
                DataCard(
                    title = stringResource(R.string.vendor_dlkm),
                    icon = Icons.Rounded.Layers
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        AnimatedVisibility(viewModel.isVendorDlkmMounted) {
                            FilledTonalButton(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(42.dp),
                                shape = RoundedCornerShape(14.dp),
                                onClick = { viewModel.unmountVendorDlkm(context) }
                            ) {
                                Text(
                                    text = stringResource(R.string.unmount_vendor_dlkm),
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }
                        AnimatedVisibility(!viewModel.isVendorDlkmMounted && viewModel.isVendorDlkmMapped) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                FilledTonalButton(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(42.dp),
                                    shape = RoundedCornerShape(14.dp),
                                    onClick = { viewModel.mountVendorDlkm(context) }
                                ) {
                                    Text(
                                        text = stringResource(R.string.mount_vendor_dlkm),
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                }
                                FilledTonalButton(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(42.dp),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.filledTonalButtonColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                        contentColor = MaterialTheme.colorScheme.onSurface
                                    ),
                                    onClick = { viewModel.unmapVendorDlkm(context) }
                                ) {
                                    Text(
                                        text = stringResource(R.string.unmap_vendor_dlkm),
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                }
                            }
                        }
                        AnimatedVisibility(!viewModel.isVendorDlkmMounted && !viewModel.isVendorDlkmMapped) {
                            FilledTonalButton(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(42.dp),
                                shape = RoundedCornerShape(14.dp),
                                onClick = { viewModel.mapVendorDlkm(context) }
                            ) {
                                Text(
                                    text = stringResource(R.string.map_vendor_dlkm),
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

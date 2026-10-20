package com.github.curiousnom.kernelflasher.ui.screens.reboot

import android.os.Build
import android.os.PowerManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Cached
import androidx.compose.material.icons.rounded.DeveloperMode
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.FlashOn
import androidx.compose.material.icons.rounded.PowerSettingsNew
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material.icons.rounded.SettingsBackupRestore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.github.curiousnom.kernelflasher.R
import com.github.curiousnom.kernelflasher.ui.components.DataCard
import com.github.curiousnom.kernelflasher.ui.components.bouncyClickable

@ExperimentalMaterial3Api
@Suppress("UnusedReceiverParameter")
@Composable
fun ColumnScope.RebootContent(
    viewModel: RebootViewModel,
    @Suppress("UNUSED_PARAMETER") ignoredNavController: NavController
) {
    val context = LocalContext.current
    @Suppress("DEPRECATION")
    val isUserspaceSupported = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && context.getSystemService(PowerManager::class.java)?.isRebootingUserspaceSupported == true

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Column {
            RebootColorfulItem(
                title = stringResource(R.string.reboot),
                icon = Icons.Rounded.RestartAlt,
                badgeColor = MaterialTheme.colorScheme.primaryContainer,
                iconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                onClick = { viewModel.rebootSystem() }
            )

            if (isUserspaceSupported) {
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                RebootColorfulItem(
                    title = stringResource(R.string.reboot_userspace),
                    icon = Icons.Rounded.Cached,
                    badgeColor = MaterialTheme.colorScheme.secondaryContainer,
                    iconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    onClick = { viewModel.rebootUserspace() }
                )
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            RebootColorfulItem(
                title = stringResource(R.string.reboot_recovery),
                icon = Icons.Rounded.SettingsBackupRestore,
                badgeColor = MaterialTheme.colorScheme.tertiaryContainer,
                iconColor = MaterialTheme.colorScheme.onTertiaryContainer,
                onClick = { viewModel.rebootRecovery() }
            )

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            RebootColorfulItem(
                title = stringResource(R.string.reboot_bootloader),
                icon = Icons.Rounded.DeveloperMode,
                badgeColor = MaterialTheme.colorScheme.secondaryContainer,
                iconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                onClick = { viewModel.rebootBootloader() }
            )

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            RebootColorfulItem(
                title = stringResource(R.string.reboot_download),
                icon = Icons.Rounded.Download,
                badgeColor = MaterialTheme.colorScheme.secondaryContainer,
                iconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                onClick = { viewModel.rebootDownload() }
            )

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            RebootColorfulItem(
                title = stringResource(R.string.reboot_edl),
                icon = Icons.Rounded.FlashOn,
                badgeColor = MaterialTheme.colorScheme.errorContainer,
                iconColor = MaterialTheme.colorScheme.onErrorContainer,
                onClick = { viewModel.rebootEdl() }
            )
        }
    }
}

@Composable
private fun RebootColorfulItem(
    title: String,
    icon: ImageVector,
    badgeColor: Color,
    iconColor: Color,
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
            Surface(
                shape = CircleShape,
                color = badgeColor,
                contentColor = iconColor,
                modifier = Modifier.size(34.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        },
        trailingContent = {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(16.dp)
            )
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        modifier = Modifier.bouncyClickable(scaleDown = 0.98f, onClick = onClick)
    )
}

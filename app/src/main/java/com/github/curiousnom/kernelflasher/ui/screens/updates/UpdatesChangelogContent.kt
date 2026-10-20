package com.github.curiousnom.kernelflasher.ui.screens.updates

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.github.curiousnom.kernelflasher.ui.components.DataCard
import kotlinx.serialization.ExperimentalSerializationApi

@Suppress("UnusedReceiverParameter")
@ExperimentalMaterial3Api
@ExperimentalSerializationApi
@ExperimentalUnitApi
@Composable
fun ColumnScope.UpdatesChangelogContent(
    viewModel: UpdatesViewModel,
    @Suppress("UNUSED_PARAMETER") ignoredNavController: NavController
) {
    viewModel.currentUpdate?.let { currentUpdate ->
        DataCard(
            title = currentUpdate.kernelName,
            icon = Icons.Rounded.Description
        )
        Spacer(Modifier.height(14.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHigh
        ) {
            Text(
                text = viewModel.changelog ?: "",
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                lineHeight = 18.sp,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

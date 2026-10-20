package com.github.capntrips.kernelflasher.ui.screens.updates

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.github.capntrips.kernelflasher.ui.components.DataCard
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
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF0F1117),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF262933))
        ) {
            Text(
                text = viewModel.changelog ?: "",
                color = Color(0xFFE2E2E9),
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                lineHeight = 18.sp,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

package com.github.capntrips.kernelflasher.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.capntrips.kernelflasher.R

@Composable
fun ViewButton(
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    FilledTonalButton(
        shape = CircleShape,
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
        interactionSource = interactionSource,
        modifier = Modifier
            .height(34.dp)
            .pressScale(interactionSource, scaleDown = 0.94f),
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ),
        onClick = onClick
    ) {
        Text(
            text = stringResource(R.string.view),
            style = MaterialTheme.typography.labelSmall
        )
        Spacer(Modifier.width(4.dp))
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
            contentDescription = null,
            modifier = Modifier.size(13.dp)
        )
    }
}

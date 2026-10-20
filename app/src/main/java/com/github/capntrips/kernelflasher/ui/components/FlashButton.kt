package com.github.capntrips.kernelflasher.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.unit.ExperimentalUnitApi
import kotlinx.serialization.ExperimentalSerializationApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FileOpen
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.github.capntrips.kernelflasher.MainActivity

@ExperimentalAnimationApi
@ExperimentalMaterialApi
@ExperimentalMaterial3Api
@ExperimentalSerializationApi
@ExperimentalUnitApi
@Composable
fun FlashButton(
    buttonText: String,
    callback: (uri: Uri) -> Unit
) {
    val mainActivity = LocalContext.current as MainActivity
    val result = remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        result.value = it
        if (it == null) {
            mainActivity.isAwaitingResult = false
        }
    }
    FilledTonalButton(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ),
        onClick = {
            mainActivity.isAwaitingResult = true
            launcher.launch("*/*")
        }
    ) {
        Icon(
            imageVector = Icons.Rounded.FileOpen,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = buttonText,
            style = MaterialTheme.typography.labelLarge
        )
    }
    result.value?.let { uri ->
        if (mainActivity.isAwaitingResult) {
            callback.invoke(uri)
        }
        mainActivity.isAwaitingResult = false
    }
}

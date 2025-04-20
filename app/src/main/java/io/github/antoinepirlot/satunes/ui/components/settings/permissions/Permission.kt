package io.github.antoinepirlot.satunes.ui.components.settings.permissions

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.antoinepirlot.jetpack_libs.components.texts.NormalText
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.icons.SatunesIcons

private val spacerSize = 16.dp

@Composable
fun Permission(
    modifier: Modifier = Modifier,
    isGranted: Boolean,
    icon: SatunesIcons,
    title: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon.imageVector,
            contentDescription = icon.description
        )
        Spacer(modifier = Modifier.size(16.dp))
        NormalText(text = title)
        Spacer(modifier = Modifier.size(spacerSize))
        val isGrantedIcon: SatunesIcons =
            if (isGranted) SatunesIcons.PERMISSION_GRANTED
            else SatunesIcons.PERMISSION_NOT_GRANTED

        Icon(
            imageVector = isGrantedIcon.imageVector,
            contentDescription = isGrantedIcon.description,
            tint = if (isGranted) Color.Green else Color.Red
        )
        if (!isGranted) {
            Spacer(modifier = Modifier.size(spacerSize))
            Button(onClick = onClick) {
                NormalText(text = stringResource(id = R.string.ask_permission))
            }
        }
    }
}

@Preview
@Composable
fun PermissionPreview(modifier: Modifier = Modifier) {
    Permission(isGranted = true, icon = SatunesIcons.FOLDER, title = "Hello", onClick = {})
}
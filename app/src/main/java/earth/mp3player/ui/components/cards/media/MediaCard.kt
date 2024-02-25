/*
 *  This file is part of MP3 Player.
 *
 *  MP3 Player is free software: you can redistribute it and/or modify it under
 *  the terms of the GNU General Public License as published by the Free Software Foundation,
 *  either version 3 of the License, or (at your option) any later version.
 *
 *  MP3 Player is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *   without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with MP3 Player.
 *  If not, see <https://www.gnu.org/licenses/>.
 */

package earth.mp3player.ui.components.cards.media

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import earth.mp3player.models.Music

@Composable
fun MediaCard(
    modifier: Modifier = Modifier,
    text: String,
    imageVector: ImageVector,
    contentDescription: String? = null,
    onClick: () -> Unit
) {
    Box(modifier = modifier) {
        TextButton(onClick = onClick) {
            ListItem(
                headlineContent = {
                    Text(text = text)
                },
                leadingContent = {
                    Icon(
                        imageVector = imageVector,
                        contentDescription = contentDescription
                    )
                }
            )
        }
    }
    Divider(modifier = modifier)
}

@Composable
@Preview
fun CardPreview() {
    val music = Music(1, "Il avait les mots", 2, 2, Uri.EMPTY, "relative path")
    MediaCard(
        modifier = Modifier.fillMaxSize(),
        text = music.name,
        imageVector = Icons.Filled.PlayArrow,
        onClick = {})
}
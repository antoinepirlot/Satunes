package earth.mp3.ui.components.cards.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun CardMenu(
    modifier: Modifier,
    menuTitle: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Tab(selected = selected, onClick = onClick) {
        Column(
            modifier
                .padding(10.dp)
                .height(50.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier
                    .size(10.dp)
                    .align(Alignment.CenterHorizontally)
                    .background(
                        color = if (selected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.background
                    )
            )
            Text(
                text = menuTitle,
                style = MaterialTheme.typography.bodyLarge,
                modifier = modifier.align(Alignment.CenterHorizontally)
            )

        }
    }
}

@Composable
@Preview
fun CardMenuPreview() {
    CardMenu(
        modifier = Modifier.fillMaxSize(),
        menuTitle = "Folder",
        selected = false,
        onClick = {})
}
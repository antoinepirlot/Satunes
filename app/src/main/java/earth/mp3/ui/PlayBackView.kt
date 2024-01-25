package earth.mp3.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun PlayBackView(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Text(text = "Hello World! Welcome to PlayBackView.")
    }
}

@Composable
@Preview
fun PlayBackViewPreview() {
    PlayBackView()
}
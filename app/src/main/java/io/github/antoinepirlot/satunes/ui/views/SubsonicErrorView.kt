package io.github.antoinepirlot.satunes.ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import io.github.antoinepirlot.jetpack_libs.components.texts.NormalText
import io.github.antoinepirlot.jetpack_libs.components.texts.Title
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.internet.subsonic.models.responses.Error
import io.github.antoinepirlot.satunes.ui.components.buttons.spacerSize

/**
 * @author Antoine Pirlot 17/12/2025
 */

@Composable
fun SubsonicErrorView(
    modifier: Modifier = Modifier,
    error: Error
) {

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Title(text = stringResource(id = R.string.error_while_fetching_title))
        NormalText(
            text = stringResource(
                id = R.string.error_code_text,
                formatArgs = arrayOf(error.code)
            )
        )
        Spacer(modifier.size(size = spacerSize))
        NormalText(
            text = stringResource(
                id = R.string.error_message_text,
                formatArgs = arrayOf(error.message)
            )
        )
    }
}

@Preview
@Composable
private fun SubsonicErrorViewPreview() {
    SubsonicErrorView(error = Error(code = 10, message = "Error message"))
}
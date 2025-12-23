package io.github.antoinepirlot.satunes.internet.subsonic.models.callbacks

import io.github.antoinepirlot.satunes.internet.subsonic.SubsonicApiRequester
import io.github.antoinepirlot.satunes.internet.subsonic.models.responses.Error
import okhttp3.Call
import okhttp3.Response
import java.io.InputStream

/**
 * @author Antoine Pirlot 23/12/2025
 */
internal class DownloadCallback(
    subsonicApiRequester: SubsonicApiRequester,
    onDataRetrieved: (InputStream) -> Unit,
    onSucceed: (() -> Unit)? = null,
    onFinished: (() -> Unit)? = null,
    onError: ((Error?) -> Unit)? = null,
) : SubsonicCallback<InputStream>(
    subsonicApiRequester = subsonicApiRequester,
    onDataRetrieved = onDataRetrieved,
    onSucceed = onSucceed,
    onFinished = onFinished,
    onError = onError
) {
    override fun onResponse(call: Call, response: Response) {
        super.onResponse(call, response)
        onDataRetrieved(response.body.byteStream())
    }

    override fun isReceivingJsonData(): Boolean = false
}
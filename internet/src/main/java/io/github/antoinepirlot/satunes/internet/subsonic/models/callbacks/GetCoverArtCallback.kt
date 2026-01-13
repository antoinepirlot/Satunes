package io.github.antoinepirlot.satunes.internet.subsonic.models.callbacks

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import io.github.antoinepirlot.satunes.internet.subsonic.SubsonicApiRequester
import io.github.antoinepirlot.satunes.internet.subsonic.models.responses.Error
import okhttp3.Call
import okhttp3.Response

/**
 * @author Antoine Pirlot 20/12/2025
 */
internal class GetCoverArtCallback(
    subsonicApiRequester: SubsonicApiRequester,
    onDataRetrieved: (Bitmap) -> Unit,
    onSucceed: (() -> Unit)? = null,
    onFinished: (() -> Unit)? = null,
    onError: ((Error?) -> Unit)? = null,
) : SubsonicCallback<Bitmap>(
    subsonicApiRequester = subsonicApiRequester,
    onDataRetrieved = onDataRetrieved,
    onSucceed = onSucceed,
    onFinished = onFinished,
    onError = onError
) {
    override fun onResponse(call: Call, response: Response) {
        super.onResponse(call, response)
        val bitmap: Bitmap? = BitmapFactory.decodeStream(response.body.byteStream())
        if (bitmap != null) {
            this.onDataRetrieved(bitmap)
            this.onSucceed?.invoke()
        }
        this.onFinished?.invoke()
    }

    override fun isReceivingJsonData(): Boolean = false
}
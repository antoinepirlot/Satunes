package io.github.antoinepirlot.satunes.internet.subsonic.models.callbacks

import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicMusic
import io.github.antoinepirlot.satunes.internet.subsonic.SubsonicApiRequester
import io.github.antoinepirlot.satunes.internet.subsonic.models.responses.Error
import okhttp3.Call
import okhttp3.Response

/**
 * @author Antoine Pirlot 24/12/2025
 */
internal class GetSongCallback(
    subsonicApiRequester: SubsonicApiRequester,
    onDataRetrieved: (SubsonicMusic) -> Unit,
    onSucceed: (() -> Unit)? = null,
    onFinished: (() -> Unit)? = null,
    onError: ((Error?) -> Unit)? = null,
) : SubsonicCallback<SubsonicMusic>(
    subsonicApiRequester = subsonicApiRequester,
    onDataRetrieved = onDataRetrieved,
    onSucceed = onSucceed,
    onFinished = onFinished,
    onError = onError
) {
    override fun onResponse(call: Call, response: Response) {
        super.onResponse(call, response)
        this.processData { this.onSucceed?.invoke() }
        this.onFinished?.invoke()
    }

    override fun processData(block: () -> Unit) {
        super.processData {
            this.onDataRetrieved(
                this.subsonicResponse!!.song!!.toSubsonicMedia(
                    subsonicApiRequester = subsonicApiRequester
                )
            )
            block()
        }
    }
}
package io.github.antoinepirlot.satunes.internet.subsonic.models.callbacks

import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicAlbum
import io.github.antoinepirlot.satunes.internet.subsonic.SubsonicApiRequester
import io.github.antoinepirlot.satunes.internet.subsonic.models.responses.Error
import okhttp3.Call
import okhttp3.Response

/**
 * @author Antoine Pirlot on 15/04/2026
 */
internal class GetAlbumsCallback(
    subsonicApiRequester: SubsonicApiRequester,
    onDataRetrieved: (Collection<SubsonicAlbum>) -> Unit,
    onSucceed: (() -> Unit)? = null,
    onFinished: (() -> Unit)? = null,
    onError: ((Error?) -> Unit)? = null,
) : SubsonicCallback<Collection<SubsonicAlbum>>(
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
                this.subsonicResponse!!.albums!!
                    .toSubsonicResponse(subsonicApiRequester = subsonicApiRequester)
            )
            block()
        }
    }
}
package io.github.antoinepirlot.satunes.internet.subsonic.models.callbacks

import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicPlaylist
import io.github.antoinepirlot.satunes.internet.subsonic.SubsonicApiRequester
import io.github.antoinepirlot.satunes.internet.subsonic.models.responses.Error
import okhttp3.Call
import okhttp3.Response

/**
 * @author Antoine Pirlot 26/12/2025
 */
internal class CreatePlaylistCallback(
    subsonicApiRequester: SubsonicApiRequester,
    onDataRetrieved: (SubsonicPlaylist) -> Unit,
    onSucceed: (() -> Unit)? = null,
    onFinished: (() -> Unit)? = null,
    onError: ((Error?) -> Unit)? = null,
) : SubsonicCallback<SubsonicPlaylist>(
    subsonicApiRequester = subsonicApiRequester,
    onDataRetrieved = onDataRetrieved,
    onSucceed = onSucceed,
    onFinished = onFinished,
    onError = onError
) {
    override fun onResponse(call: Call, response: Response) {
        super.onResponse(call, response)
        if (this.processData())
            this.onSucceed?.invoke()
        this.onFinished?.invoke()
    }

    override fun processData(): Boolean {
        return if (super.processData()) {
            this.onDataRetrieved(
                this.subsonicResponse!!.playlist!!.toSubsonicMedia(
                    subsonicApiRequester = subsonicApiRequester
                )
            )
            true
        } else false
    }
}
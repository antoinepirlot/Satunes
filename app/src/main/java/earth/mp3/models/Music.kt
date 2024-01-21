package earth.mp3.models

import android.net.Uri

class Music {
    private val id: Long
        get() {
            return this.id
        }
    private var name: String
        set(name: String) {
            this.name = name
        }
    private var duration: Int
        set(duration: Int) {
            this.duration = duration
        }
    private var size: Int
        set(size: Int) {
            this.size = size
        }
    private var uri: Uri?
        get() {
            return this.uri
        }
        set(uri: Uri?) {
            if (uri == null) {
                return
            }
            this.uri = uri
        }
    private var relativePath: String
        set(name: String) {
            this.name = name
        }
}
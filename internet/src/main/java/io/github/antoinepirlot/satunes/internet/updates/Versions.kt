/*
 * This file is part of Satunes.
 *
 * Satunes is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 * Satunes is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with Satunes.
 * If not, see <https://www.gnu.org/licenses/>.
 *
 * *** INFORMATION ABOUT THE AUTHOR *****
 * The author of this file is Antoine Pirlot, the owner of this project.
 * You find this original project on Codeberg.
 *
 * My Codeberg link is: https://codeberg.org/antoinepirlot
 * This current project's link is: https://codeberg.org/antoinepirlot/Satunes
 */

package io.github.antoinepirlot.satunes.internet.updates

import android.os.Build
import androidx.annotation.RequiresApi

/**
 * @author Antoine Pirlot on 14/04/2024
 */

@RequiresApi(Build.VERSION_CODES.M)
object Versions {
    internal const val ALPHA: String = "alpha"
    internal const val BETA: String = "beta"
    internal const val PREVIEW: String = "preview"

    internal val ALPHA_REGEX: Regex =
        Regex("\"/antoinepirlot/.+/releases/tag/v[0-9]+\\.[0-9]+\\.[0-9]+((-$PREVIEW|-$BETA|-$ALPHA)-[0-9]*)?\"")

    internal val BETA_REGEX: Regex =
        Regex("\"/antoinepirlot/.+/releases/tag/v[0-9]+\\.[0-9]+\\.[0-9]+((-$PREVIEW|-$BETA)-[0-9]*)?\"")

    internal val PREVIEW_REGEX: Regex =
        Regex("\"/antoinepirlot/.+/releases/tag/v[0-9]+\\.[0-9]+\\.[0-9]+(-$PREVIEW-[0-9]*)?\"")

    internal val RELEASE_REGEX: Regex =
        Regex("\"/antoinepirlot/.+/releases/tag/v[0-9]+\\.[0-9]+\\.[0-9]+\"")

    private const val RELEASES_URL = "https://codeberg.org/antoinepirlot/Satunes/releases"
    const val LATEST_RELEASE_URL = "$RELEASES_URL/latest"
    const val TAG_RELEASE_URL = "$RELEASES_URL/tag"

    internal var versionType: String = "" //Alpha, Beta, Preview or "" for Stable version
}

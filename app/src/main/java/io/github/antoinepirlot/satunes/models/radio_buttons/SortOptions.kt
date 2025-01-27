/*
 * This file is part of Satunes.
 *
 *  Satunes is free software: you can redistribute it and/or modify it under
 *  the terms of the GNU General Public License as published by the Free Software Foundation,
 *  either version 3 of the License, or (at your option) any later version.
 *
 *  Satunes is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with Satunes.
 *  If not, see <https://www.gnu.org/licenses/>.
 *
 *  **** INFORMATIONS ABOUT THE AUTHOR *****
 *  The author of this file is Antoine Pirlot, the owner of this project.
 *  You find this original project on github.
 *
 *  My github link is: https://github.com/antoinepirlot
 *  This current project's link is: https://github.com/antoinepirlot/Satunes
 *
 *  You can contact me via my email: pirlot.antoine@outlook.com
 *  PS: I don't answer quickly.
 */

package io.github.antoinepirlot.satunes.models.radio_buttons

import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.database.models.MediaImpl
import io.github.antoinepirlot.satunes.database.models.comparators.SortByAlbumComparator
import io.github.antoinepirlot.satunes.database.models.comparators.SortByArtistComparator
import io.github.antoinepirlot.satunes.database.models.comparators.SortByGenreComparator
import io.github.antoinepirlot.satunes.database.models.comparators.SortByTitleComparator
import io.github.antoinepirlot.satunes.icons.SatunesIcons
import io.github.antoinepirlot.satunes.database.R as RDb

/**
 * @author Antoine Pirlot on 30/11/2024
 */
enum class SortOptions(
    val icon: SatunesIcons,
    val stringId: Int,
    val comparator: Comparator<MediaImpl>
) {
    ARTIST(icon = SatunesIcons.ARTIST, stringId = RDb.string.artists, SortByArtistComparator),
    ALBUM(
        icon = SatunesIcons.ALBUM,
        stringId = RDb.string.albums,
        comparator = SortByAlbumComparator
    ),
    TITLE(icon = SatunesIcons.TITLE, stringId = R.string.title, comparator = SortByTitleComparator),
    GENRE(
        icon = SatunesIcons.GENRES,
        stringId = RDb.string.genres,
        comparator = SortByGenreComparator
    )
}
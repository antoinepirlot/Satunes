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

package io.github.antoinepirlot.satunes.models.radio_buttons

import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.database.models.MediaImpl
import io.github.antoinepirlot.satunes.database.models.comparators.MediaComparator
import io.github.antoinepirlot.satunes.database.models.comparators.SortByAddedDateComparator
import io.github.antoinepirlot.satunes.database.models.comparators.SortByAlbumComparator
import io.github.antoinepirlot.satunes.database.models.comparators.SortByArtistComparator
import io.github.antoinepirlot.satunes.database.models.comparators.SortByGenreComparator
import io.github.antoinepirlot.satunes.database.models.comparators.SortByTitleComparator
import io.github.antoinepirlot.satunes.database.models.comparators.SortByYearsComparator
import io.github.antoinepirlot.satunes.icons.SatunesIcons
import io.github.antoinepirlot.satunes.database.R as RDb

/**
 * @author Antoine Pirlot on 30/11/2024
 */
enum class SortOptions(
    val icon: SatunesIcons,
    val stringId: Int,
    val comparator: MediaComparator<MediaImpl>?
) {
    ALBUM(
        icon = SatunesIcons.ALBUM,
        stringId = RDb.string.albums,
        comparator = SortByAlbumComparator
    ),
    ARTIST(
        icon = SatunesIcons.ARTIST,
        stringId = RDb.string.artists,
        comparator = SortByArtistComparator
    ),

    DATE_ADDED(
        icon = SatunesIcons.ADDED_DATE,
        stringId = RDb.string.added_date,
        comparator = SortByAddedDateComparator
    ),
    GENRE(
        icon = SatunesIcons.GENRES,
        stringId = RDb.string.genres,
        comparator = SortByGenreComparator
    ),
    PLAYLIST_ADDED_DATE(
        icon = SatunesIcons.ADDED_DATE,
        stringId = RDb.string.playlist_added_date,
        comparator = null
    ),
    TITLE(icon = SatunesIcons.TITLE, stringId = R.string.title, comparator = SortByTitleComparator),
    YEAR(icon = SatunesIcons.YEARS, stringId = RDb.string.years, comparator = SortByYearsComparator)
}
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

package io.github.antoinepirlot.satunes.data

import io.github.antoinepirlot.satunes.models.Destination
import io.github.antoinepirlot.satunes.models.radio_buttons.SortOptions

/**
 * @author Antoine Pirlot on 30/11/2024
 */

internal val defaultSortingOptions: SortOptions = SortOptions.TITLE

private val albumsSortOptions: List<SortOptions> = listOf(
    SortOptions.TITLE,
    SortOptions.ARTIST,
    SortOptions.YEAR
)

private val artistsSortOptions: List<SortOptions> = listOf(
    SortOptions.TITLE,
)

private val foldersSortOptions: List<SortOptions> = listOf(
    SortOptions.TITLE,
    SortOptions.DATE_ADDED
)

private val genresSortOptions: List<SortOptions> = listOf(
    SortOptions.TITLE,
)

private val musicsSortOptions: List<SortOptions> = listOf(
    SortOptions.TITLE,
    SortOptions.ALBUM,
    SortOptions.ARTIST,
    SortOptions.GENRE,
    SortOptions.YEAR,
    SortOptions.DATE_ADDED
)

private val playlistsSortOptions: List<SortOptions> = listOf(
    SortOptions.TITLE
)

private val singlePlaylistSortOptions: List<SortOptions> = musicsSortOptions

/**
 * Get the sortOptions list for the specified [Destination].
 * If there's no sort options list matching the [Destination] then returns null.
 *
 * @param destination a [Destination] to get the matching [SortOptions] list.
 *
 * @return the [SortOptions] list or an empty list if there's no [SortOptions] list matching [Destination].
 */
internal fun getSortOptions(destination: Destination): List<SortOptions> {
    return when (destination) {
        Destination.MUSICS, Destination.ALBUM, Destination.ARTIST, Destination.GENRE -> musicsSortOptions
        Destination.ALBUMS -> albumsSortOptions
        Destination.ARTISTS -> artistsSortOptions
        Destination.FOLDERS, Destination.FOLDER -> foldersSortOptions
        Destination.GENRES -> genresSortOptions
        Destination.PLAYLISTS -> playlistsSortOptions
        Destination.PLAYLIST -> singlePlaylistSortOptions
        else -> listOf()
    }
}
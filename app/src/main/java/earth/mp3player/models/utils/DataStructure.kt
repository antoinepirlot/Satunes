/*
 *  This file is part of MP3 Player.
 *
 *  MP3 Player is free software: you can redistribute it and/or modify it under
 *  the terms of the GNU General Public License as published by the Free Software Foundation,
 *  either version 3 of the License, or (at your option) any later version.
 *
 *  MP3 Player is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *   without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with MP3 Player.
 *  If not, see <https://www.gnu.org/licenses/>.
 */

package earth.mp3player.models.utils

fun <T> loadObjectsTo(toList: MutableList<T>, fromList: List<T>) {
    toList.clear()
    toList.addAll(fromList)
}

fun <T, U> loadObjectsToMap(toMap: MutableMap<T, U>, fromMap: Map<T, U>) {
    toMap.clear()
    toMap.putAll(fromMap)
}
/*
 * This file is part of MP3 Player.
 *
 * MP3 Player is free software: you can redistribute it and/or modify it under
 *  the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * MP3 Player is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with MP3 Player.
 * If not, see <https://www.gnu.org/licenses/>.
 *
 * **** INFORMATIONS ABOUT THE AUTHOR *****
 * The author of this file is Antoine Pirlot, the owner of this project.
 * You find this original project on github.
 *
 * My github link is: https://github.com/antoinepirlot
 * This current project's link is: https://github.com/antoinepirlot/MP3-Player
 *
 * You can contact me via my email: pirlot.antoine@outlook.com
 * PS: I don't answer quickly.
 */

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
 *
 *  ***** INFORMATIONS ABOUT THE AUTHOR *****
 *  The author of this file is Antoine Pirlot, the owner of this project.
 *  You find this original project on github.
 *
 *  My github link is: https://github.com/antoinepirlot
 *  This current project's link is: https://github.com/antoinepirlot/MP3-Player
 *
 *  You can contact me via my email: pirlot.antoine@outlook.com
 *  PS: I don't answer quickly.
 */

package earth.mp3player.car.playback

import earth.mp3player.car.pages.ScreenPages

/**
 * @author Antoine Pirlot on 17/03/2024
 */
class RouteDeque {
    private val routeDeque: ArrayDeque<String>

    init {
        this.routeDeque = ArrayDeque(initialCapacity = 4)
        this.routeDeque.addFirst(ScreenPages.ROOT.id)
    }

    fun isEmpty(): Boolean {
        return this.routeDeque.isEmpty() || this.routeDeque.size == 1
    }

    fun resetRouteDeque() {
        this.routeDeque.clear()
        this.routeDeque.addFirst(ScreenPages.ROOT.id)
    }

    fun addLast(route: String) {
        if (!this.routeDeque.contains(route)) {
            this.routeDeque.addLast(route)
        }
    }

    fun last(): String {
        return this.routeDeque.last()
    }

    fun oneBeforeLast(): String {
        return this.get(index = this.routeDeque.lastIndex - 1)
    }


    fun get(index: Int): String {
        if (index !in this.routeDeque.indices) {
            throw ArrayIndexOutOfBoundsException("The index is $index and is out of bound")
        }
        return this.routeDeque[index]
    }
}
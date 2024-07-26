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

package io.github.antoinepirlot.satunes.database.models

import io.github.antoinepirlot.satunes.database.R

/**
 * @author Antoine Pirlot on 26/07/2024
 */

enum class BarSpeed(val stringId: Int, val speed: Float) {
    REAL_TIME(stringId = R.string.real_time_speed, speed = 0.1F),
    VERY_FAST(stringId = R.string.very_fast_speed, speed = 0.35F),
    FAST(stringId = R.string.fast_speed, speed = 0.5F),
    NORMAL(stringId = R.string.normal_speed, speed = 1.0F),
    BIT_SLOW(stringId = R.string.bit_slow_speed, speed = 1.35F),
    SLOW(stringId = R.string.slow_speed, speed = 1.6F),
    VERY_SLOW(stringId = R.string.very_slow_speed, speed = 2.0F)
}
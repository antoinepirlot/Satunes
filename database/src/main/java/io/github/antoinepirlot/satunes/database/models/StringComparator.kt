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

import java.text.Normalizer

/**
 * @author Antoine Pirlot on 24/04/2024
 */
object StringComparator : Comparator<String> {
    override fun compare(o1: String, o2: String): Int {
        val o1Normalized: String =
            Normalizer.normalize(o1, Normalizer.Form.NFD)
        val o2Normalized: String =
            Normalizer.normalize(o2, Normalizer.Form.NFD)

        for (i: Int in o1Normalized.indices) {
            if (i == o2Normalized.lastIndex + 1) {
                break
            }
            val c1: Char = o1Normalized[i]
            val c2: Char = o2Normalized[i]
            if (!c1.isLetter() || !c2.isLetter()) {
                return o1Normalized[i].compareTo(o2Normalized[i])
            } else {
                // c1 & c2 are both letters
                if (c1 == c2) {
                    // "A" == "A" or "a" == "a" it's trivial then continue as the verification is
                    // not finished.
                    continue
                }
                if (c1.lowercaseChar() == c2.lowercaseChar()) {
                    // it's "A" == to "a" or "a" == "A". "A" > "a"
                    return if (c1 > c2) { // ASCII Table Upper is less than Lower
                        1
                    } else {
                        -1
                    }
                }
                // Here it's something like "B" == "a", "a" > "B"
                return if (c1.lowercaseChar() > c2.lowercaseChar()) {
                    1
                } else {
                    -1
                }
            }
        }
        // At this stage, it's the same value but length has not been checked
        return if (o1Normalized.length == o2Normalized.length) {
            0
        } else if (o1Normalized.length > o2Normalized.length) {
            1
        } else {
            -1
        }
    }
}
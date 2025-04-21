/*
 * This file is part of Satunes.
 * Satunes is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *  Satunes is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License along with Satunes.
 *  If not, see <https://www.gnu.org/licenses/>.
 *
 * **** INFORMATION ABOUT THE AUTHOR *****
 * The author of this file is Antoine Pirlot, the owner of this project.
 * You find this original project on Codeberg.
 *
 * My Codeberg link is: https://codeberg.org/antoinepirlot
 * This current project's link is: https://codeberg.org/antoinepirlot/Satunes
 */

package io.github.antoinepirlot.satunes.utils.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF

/**
 * @author Antoine Pirlot 20/04/2025
 */

/**
 * Used to replace modifier.clip from Modifier, make the artwork circle
 * @author ChatGPT
 */
fun Bitmap.toCircularBitmap(): Bitmap {
    val size: Int = minOf(width, height)
    val output: Bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(output)

    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val rect = RectF(0f, 0f, size.toFloat(), size.toFloat())

    val path = Path().apply {
        addOval(rect, Path.Direction.CCW)
    }

    canvas.clipPath(path)
    val srcRect =
        Rect((width - size) / 2, (height - size) / 2, (width + size) / 2, (height + size) / 2)
    canvas.drawBitmap(this, srcRect, rect, paint)

    return output
}
package com.example.navigationsample.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.core.graphics.ColorUtils
import kotlin.random.Random

val Int.dp: Int get() = (this * Resources.getSystem().displayMetrics.density).toInt()

val Int.sp: Int get() = (this * Resources.getSystem().displayMetrics.scaledDensity).toInt()

fun Context.getThemeAttr(@AttrRes attr: Int) =
    TypedValue().also { theme.resolveAttribute(attr, it, true) }.resourceId

fun generateColor(number: Int): Int {
    val random = Random(number + 1)
    return Color.rgb(
        random.nextInt(180, 240),
        random.nextInt(180, 240),
        random.nextInt(180, 240)
    )
}

fun generateDarkColor(number: Int) = darkerColor(generateColor(number))

fun darkerColor(color: Int): Int {
    val hsl = floatArrayOf(0f, 0f, 0f)
    ColorUtils.colorToHSL(color, hsl)
    hsl[2] -= 0.2f
    return ColorUtils.HSLToColor(hsl)
}

fun lighterColor(color: Int) = ColorUtils.blendARGB(color, Color.WHITE, 0.5f)
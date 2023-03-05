package com.example.android.clock

import android.graphics.Color

enum class ClockArrows(val rads: Float, val coefLength: Float,
                       val coefStrokeWidth: Float) {
    HOURS((Math.PI / 6).toFloat(), 0.5f, 0.05f),
    MINUTES((Math.PI / 30).toFloat(), 0.6f, 0.03f),
    SECONDS((Math.PI / 30).toFloat(), 0.75f, 0.01f);

    var color: Int = Color.BLACK

    fun next() = when (this) {
        HOURS -> MINUTES
        MINUTES -> SECONDS
        SECONDS -> HOURS
    }
}
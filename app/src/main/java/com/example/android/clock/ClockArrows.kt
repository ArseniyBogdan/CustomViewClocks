package com.example.android.clock

enum class ClockArrows(val rads: Float, var coefLength: Float,
                       var coefStrokeWidth: Float) {
    HOURS((Math.PI / 6).toFloat(), 0.5f, 0.05f),
    MINUTES((Math.PI / 30).toFloat(), 0.6f, 0.03f),
    SECONDS((Math.PI / 30).toFloat(), 0.75f, 0.01f);

    fun next() = when (this) {
        SECONDS -> MINUTES
        MINUTES -> HOURS
        HOURS -> SECONDS
    }

    val length: Float
        get() = coefLength * radius
    val strokeWidth: Float
        get() = coefStrokeWidth * coefStrokeWidth

    companion object{
        var radius: Float = 0f
    }
}
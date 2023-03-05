package com.example.android.clock

import android.icu.util.TimeZone
import android.util.Log
import java.time.LocalTime
import kotlin.math.cos
import kotlin.math.sin

data class ClockArrowCoordinates(
    var xHead: Float = 0f,
    var yHead: Float = 0f,
    var xTail: Float = 0f,
    var yTail: Float = 0f,
    var width: Float = 0f,
    var height: Float = 0f
){
    fun computeXYForArrow(arrowType: ClockArrows, timeZone: TimeZones) {
        // Angles are in radians.
        val timeZoneOffset = TimeZone.getDefault().rawOffset / (60 * 60 * 1000)

        val localTime = when(arrowType){
            ClockArrows.HOURS -> ((LocalTime.now().hour - timeZoneOffset + timeZone.zone) % 12)
                .toFloat() + LocalTime.now().minute / 60f
            ClockArrows.MINUTES -> LocalTime.now().minute.toFloat() + LocalTime.now().second / 60f
            ClockArrows.SECONDS -> LocalTime.now().second.toFloat() + LocalTime.now().nano / 1_000_000_000f
        }
        val lineLength = arrowType.length

        val startAngle = (Math.PI * (3/ 2.0)).toFloat()
        val angle = startAngle + localTime * arrowType.rads
        xHead = (lineLength * cos(angle)) + width / 2
        yHead = (lineLength * sin(angle)) + height / 2
        xTail = (lineLength * cos(angle + Math.PI) * 0.3f ).toFloat() + width / 2
        yTail = (lineLength * sin(angle + Math.PI) * 0.3f ).toFloat() + height / 2
    }
}
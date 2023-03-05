package com.example.android.clock

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.icu.util.TimeZone
import com.example.android.clock.extensions.drawClockArrow

class ClockArrowPainter(private val widthOfView: Float,
                        private val heightOfView: Float,
                        private val timeZone: TimeZones): Paintable {

    private val clockArrowCoordinates = ClockArrowCoordinates(width = widthOfView, height = heightOfView)
    private val line = Line()
    private val lineBuilder = LineBuilder()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.BLACK
    }

    override fun paint(canvas: Canvas) {
        var arrowType = ClockArrows.HOURS

        /** отрисовываем минутную и часовую стрелку */
        while(arrowType != ClockArrows.SECONDS){
            paint.color = arrowType.color
            clockArrowCoordinates.computeXYForArrow(arrowType, timeZone)
            lineBuilder.configureBuilder(clockArrowCoordinates)
            canvas.drawClockArrow(arrowType.strokeWidth, lineBuilder.build(line), paint)
            arrowType = arrowType.next()
        }

        /** отрисовываем секундную стрелку */
        paint.color = arrowType.color
        clockArrowCoordinates.computeXYForArrow(arrowType, timeZone)
        lineBuilder.apply {
            configureBuilder(clockArrowCoordinates)
            startXY(widthOfView / 2f, heightOfView / 2f)
            canvas.drawClockArrow(ClockArrows.SECONDS.strokeWidth, build(line), paint)
            configureBuilder(clockArrowCoordinates)
            stopXY(widthOfView / 2f, heightOfView / 2f)
            canvas.drawClockArrow(ClockArrows.MINUTES.strokeWidth, build(line), paint)
        }
    }
}
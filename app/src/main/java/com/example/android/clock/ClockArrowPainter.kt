package com.example.android.clock

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.example.android.clock.extensions.drawClockArrow

class ClockArrowPainter(private val widthOfView: Float,
                        private val heightOfView: Float,
                        private val timeZone: TimeZones,
                        private val radius: Float): Paintable {

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
            clockArrowCoordinates.computeXYForArrow(arrowType, radius, timeZone)
            lineBuilder.configureBuilder(clockArrowCoordinates)
            canvas.drawClockArrow(arrowType.coefStrokeWidth * radius,
                lineBuilder.build(line), paint)
            arrowType = arrowType.next()
        }

        /** отрисовываем секундную стрелку */
        paint.color = arrowType.color
        clockArrowCoordinates.computeXYForArrow(arrowType, radius,timeZone)
        lineBuilder.apply {
            configureBuilder(clockArrowCoordinates)
            startXY(widthOfView / 2f, heightOfView / 2f)
            canvas.drawClockArrow(ClockArrows.SECONDS.coefStrokeWidth * radius, build(line), paint)
            configureBuilder(clockArrowCoordinates)
            stopXY(widthOfView / 2f, heightOfView / 2f)
            canvas.drawClockArrow(ClockArrows.MINUTES.coefStrokeWidth * radius, build(line), paint)
        }
    }
}
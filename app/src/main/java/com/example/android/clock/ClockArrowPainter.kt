package com.example.android.clock

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.example.android.clock.extensions.drawClockArrow

class ClockArrowPainter(private val widthOfView: Float, private val heightOfView: Float): Paintable {

    private val clockArrowCoordinates = ClockArrowCoordinates(width = widthOfView, height = heightOfView)
    private val line = Line()
    private val lineBuilder = LineBuilder()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.BLACK
    }

    override fun paint(canvas: Canvas) {
        var arrowType = ClockArrows.SECONDS
        /** отрисовываем секундную стрелку */
        clockArrowCoordinates.computeXYForArrow(arrowType)
        lineBuilder.apply {
            configureBuilder(clockArrowCoordinates)
            startXY(widthOfView / 2f, heightOfView / 2f)
            canvas.drawClockArrow(ClockArrows.SECONDS.strokeWidth, build(line), paint)
            configureBuilder(clockArrowCoordinates)
            stopXY(widthOfView / 2f, heightOfView / 2f)
            canvas.drawClockArrow(ClockArrows.MINUTES.strokeWidth, build(line), paint)
        }

        /** отрисовываем минутную и часовую стрелку */
        while(arrowType != ClockArrows.HOURS){
            arrowType = arrowType.next()
            clockArrowCoordinates.computeXYForArrow(arrowType)
            lineBuilder.configureBuilder(clockArrowCoordinates)
            canvas.drawClockArrow(arrowType.strokeWidth, lineBuilder.build(line), paint)
        }
    }
}
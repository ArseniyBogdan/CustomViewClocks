package com.example.android.clock

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.example.android.clock.extensions.drawClockArrow
import kotlin.math.min

class ClockArrowPainter(private val widthOfView: Float, private val heightOfView: Float): Paintable {

    private val radius = min(widthOfView, heightOfView) / 2 * 0.9f
    private val clockArrowCoordinates = ClockArrowCoordinates(width = widthOfView, height = heightOfView)
    private val line = Line()
    private val lineBuilder = LineBuilder()

    override fun paint(canvas: Canvas, paint: Paint) {
        paint.color = Color.BLACK
        paint.maskFilter = null
        clockArrowCoordinates.computeXYForArrow(arrowType)
        arrowType = arrowType.next()
        lineBuilder.apply {
            configureBuilder(clockArrowCoordinates)
            startXY(widthOfView / 2f, heightOfView / 2f)
            canvas.drawClockArrow(radius*0.01f, build(line), paint)
            configureBuilder(clockArrowCoordinates)
            stopXY(widthOfView / 2f, heightOfView / 2f)
            canvas.drawClockArrow(radius*0.03f, lineBuilder.build(line), paint)
        }

        clockArrowCoordinates.computeXYForArrow(arrowType)
        arrowType = arrowType.next()
        lineBuilder.configureBuilder(clockArrowCoordinates)
        canvas.drawClockArrow(radius*0.03f, lineBuilder.build(line), paint)

        clockArrowCoordinates.computeXYForArrow(arrowType)
        arrowType = arrowType.next()
        lineBuilder.configureBuilder(clockArrowCoordinates)
        canvas.drawClockArrow(radius*0.05f, lineBuilder.build(line), paint)

        for(arrow in ClockArrows.values()){
            clockArrowCoordinates.computeXYForArrow(arrow)
            lineBuilder.configureBuilder(clockArrowCoordinates)
            canvas.drawClockArrow(arrow.strokeWidth, lineBuilder.build(line), paint)
        }
    }

    companion object{
        private var arrowType = ClockArrows.SECONDS
    }
}
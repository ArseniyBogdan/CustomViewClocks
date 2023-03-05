package com.example.android.clock

import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.example.android.clock.extensions.drawClockArrow
import kotlin.math.min

class ShadowPainter(private val widthOfView: Float,
                    private val heightOfView: Float,
                    private val timeZone: TimeZones): Paintable {
    private val radius = min(widthOfView, heightOfView) / 2 * 0.9f

    private val line = Line()
    private val lineBuilder = LineBuilder()
    private val clockArrowCoordinates = ClockArrowCoordinates(width = widthOfView, height = heightOfView)
    private val blurMaskS = BlurMaskFilter(
        ClockArrows.SECONDS.coefStrokeWidth * radius, BlurMaskFilter.Blur.NORMAL)
    private val blurMaskM = BlurMaskFilter(
        ClockArrows.MINUTES.coefStrokeWidth * radius, BlurMaskFilter.Blur.NORMAL)
    private val blurMaskH = BlurMaskFilter(
        ClockArrows.HOURS.coefStrokeWidth * radius, BlurMaskFilter.Blur.NORMAL)
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.GRAY
    }

    override fun paint(canvas: Canvas) {
        val offsetX = radius*0.05f
        val offsetY = radius*0.02f
        var arrowType = ClockArrows.HOURS

        /** отрисовка тени для минутной и часовой стрелки */
        while(arrowType != ClockArrows.SECONDS){
            paint.maskFilter = when(arrowType){
                ClockArrows.MINUTES -> blurMaskM
                ClockArrows.HOURS -> blurMaskH
                else -> {null}
            }
            clockArrowCoordinates.computeXYForArrow(arrowType, radius, timeZone)
            lineBuilder.configureBuilder(clockArrowCoordinates).offsetXY(offsetX, offsetY)
            canvas.drawClockArrow(arrowType.coefStrokeWidth * radius, lineBuilder.build(line), paint)
            arrowType = arrowType.next()
        }

        /** отрисовка тени для секундной стрелки */
        paint.maskFilter = blurMaskS
        clockArrowCoordinates.computeXYForArrow(arrowType, radius, timeZone)
        lineBuilder.apply {
            configureBuilder(clockArrowCoordinates)
            offsetXY(offsetX, offsetY)
            startXY(widthOfView / 2f, heightOfView / 2f)
            canvas.drawClockArrow(ClockArrows.SECONDS.coefStrokeWidth * radius, build(line), paint)
            configureBuilder(clockArrowCoordinates)
            offsetXY(offsetX, offsetY)
            stopXY(widthOfView / 2f, heightOfView / 2f)
            canvas.drawClockArrow(ClockArrows.MINUTES.coefStrokeWidth * radius, build(line), paint)
        }
    }
}
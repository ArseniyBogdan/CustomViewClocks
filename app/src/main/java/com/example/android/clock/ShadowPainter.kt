package com.example.android.clock

import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.example.android.clock.extensions.drawClockArrow
import kotlin.math.min

class ShadowPainter(private val widthOfView: Float, private val heightOfView: Float): Paintable {
    private val radius = min(widthOfView, heightOfView) / 2 * 0.9f

    private val line = Line()
    private val lineBuilder = LineBuilder()
    private val clockArrowCoordinates = ClockArrowCoordinates(width = widthOfView, height = heightOfView)
    private val blurMaskS = BlurMaskFilter(
        ClockArrows.SECONDS.strokeWidth, BlurMaskFilter.Blur.NORMAL)
    private val blurMaskM = BlurMaskFilter(
        ClockArrows.MINUTES.strokeWidth, BlurMaskFilter.Blur.NORMAL)
    private val blurMaskH = BlurMaskFilter(
        ClockArrows.HOURS.strokeWidth, BlurMaskFilter.Blur.NORMAL)
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.GRAY
    }

    override fun paint(canvas: Canvas) {
        /** отрисовка тени для секундной стрелки */
        var arrowType = ClockArrows.SECONDS
        val offsetX = radius*0.05f
        val offsetY = radius*0.02f
        paint.maskFilter = blurMaskS
        clockArrowCoordinates.computeXYForArrow(arrowType)
        lineBuilder.apply {
            configureBuilder(clockArrowCoordinates)
            offsetXY(offsetX, offsetY)
            startXY(widthOfView / 2f, heightOfView / 2f)
            canvas.drawClockArrow(ClockArrows.SECONDS.strokeWidth, build(line), paint)
            configureBuilder(clockArrowCoordinates)
            offsetXY(offsetX, offsetY)
            stopXY(widthOfView / 2f, heightOfView / 2f)
            canvas.drawClockArrow(ClockArrows.MINUTES.strokeWidth, build(line), paint)
        }

        /** отрисовка тени для минутной и часовой стрелки */
        while(arrowType != ClockArrows.HOURS){
            arrowType = arrowType.next()
            paint.maskFilter = when(arrowType){
                ClockArrows.MINUTES -> blurMaskM
                ClockArrows.HOURS -> blurMaskH
                else -> {null}
            }
            clockArrowCoordinates.computeXYForArrow(arrowType)
            lineBuilder.configureBuilder(clockArrowCoordinates).offsetXY(offsetX, offsetY)
            canvas.drawClockArrow(arrowType.strokeWidth, lineBuilder.build(line), paint)
        }
    }
}
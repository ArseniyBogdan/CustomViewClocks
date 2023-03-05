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
    private val blurMaskS = BlurMaskFilter(radius*0.01f, BlurMaskFilter.Blur.NORMAL)
    private val blurMaskM = BlurMaskFilter(radius*0.03f, BlurMaskFilter.Blur.NORMAL)
    private val blurMaskH = BlurMaskFilter(radius*0.05f, BlurMaskFilter.Blur.NORMAL)

    override fun paint(canvas: Canvas, paint: Paint) {
        /** отрисовка тени стрелок */
        val offsetX = radius*0.05f
        val offsetY = radius*0.02f
        paint.color = Color.GRAY
        paint.maskFilter = blurMaskS
        clockArrowCoordinates.computeXYForArrow(arrowType)
        arrowType = arrowType.next()
        lineBuilder.apply {
            configureBuilder(clockArrowCoordinates)
            offsetXY(offsetX, offsetY)
            startXY(widthOfView / 2f, heightOfView / 2f)
            canvas.drawClockArrow(radius*0.01f, build(line), paint)
            configureBuilder(clockArrowCoordinates)
            offsetXY(offsetX, offsetY)
            stopXY(widthOfView / 2f, heightOfView / 2f)
            canvas.drawClockArrow(radius*0.03f, build(line), paint)
        }


        paint.maskFilter = blurMaskM
        clockArrowCoordinates.computeXYForArrow(arrowType)
        arrowType = arrowType.next()
        lineBuilder.configureBuilder(clockArrowCoordinates).offsetXY(offsetX, offsetY)
        canvas.drawClockArrow(radius*0.03f, lineBuilder.build(line), paint)

        paint.maskFilter = blurMaskH
        clockArrowCoordinates.computeXYForArrow(arrowType)
        arrowType = arrowType.next()
        lineBuilder.configureBuilder(clockArrowCoordinates).offsetXY(offsetX, offsetY)
        canvas.drawClockArrow(radius*0.05f, lineBuilder.build(line), paint)
    }

    companion object{
        private var arrowType = ClockArrows.SECONDS
    }
}
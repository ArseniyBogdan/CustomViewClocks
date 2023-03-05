package com.example.android.clock

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import com.example.android.clock.extensions.drawClockArrow
import com.example.clocks.R
import java.time.LocalTime
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class CustomClock @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): View(context, attrs, defStyleAttr) {

    // numeric numbers to denote the hours
    private val mClockHours = (1 .. 12).map{it}

    private var radius = 0.0f
    private val radiusOfInnerCircle
        get() = radius * 0.91f
    private val radiusOfDashedCircle
        get() = radius * 0.84f
    private val radiusForHoursCircle
        get() = radius * 0.67f

    // объекты
    private val mRect = Rect()
    private val pointPosition = PointF(0.0f, 0.0f)
    lateinit var shadowPainter: Paintable
    lateinit var arrowsPainter: Paintable
    lateinit var radialShader: Paintable

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        typeface = resources.getFont(R.font.poppinsregular)
    }

    private val creamColor = Color.argb(255, 255, 251, 251)

    private fun PointF.computeXYForHours(hour: Int, radius: Float) {
        // Angles are in radians.
        val startAngle = Math.PI * (3/ 2.0)
        val angle = startAngle + hour * (Math.PI / 6)
        x = (radius * cos(angle)).toFloat() + width / 2
        y = (radius * sin(angle)).toFloat() + mRect.height()/2 + height / 2
    }

    private fun PointF.computeXYForDots(index: Int, radius: Float) {
        val angle = index * (Math.PI / 30)
        x = (radius * cos(angle)).toFloat() + width / 2
        y = (radius * sin(angle)).toFloat() + height / 2
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        radius = (min(width, height) / 2.0 * 0.9).toFloat()
        shadowPainter = ShadowPainter(widthOfView = width.toFloat(),
            heightOfView = width.toFloat())
        arrowsPainter = ClockArrowPainter(widthOfView = width.toFloat(),
            heightOfView = width.toFloat())
        radialShader = RadialShader(widthOfView = width.toFloat(),
            heightOfView = width.toFloat())
        ClockArrows.radius = radius
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // drawing the edging
        paint.color = Color.BLACK
        canvas.drawCircle((width/2).toFloat(), (height/2).toFloat(), radius, paint)

        // drawing a dial
        paint.color = creamColor
        canvas.drawCircle((width/2).toFloat(), (height/2).toFloat(), radiusOfInnerCircle, paint)

        paint.color = Color.BLACK
        for(minutes in 1..60){
            pointPosition.computeXYForDots(minutes, radiusOfDashedCircle)
            val sizeOfDot = if(minutes % 5 == 0) radius/80 else radius/100
            canvas.drawCircle(pointPosition.x, pointPosition.y, sizeOfDot, paint)
        }

        /** border of hours */
        val fontSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, radius*0.1f, resources.displayMetrics)
        paint.textSize = fontSize
        for(hour in mClockHours) {
            val tmp = hour.toString()
            paint.getTextBounds(tmp, 0, tmp.length, mRect)

            pointPosition.computeXYForHours(hour, radiusForHoursCircle)
            canvas.drawText(tmp, pointPosition.x, pointPosition.y, paint)
        }


        /** отрисовка тени стрелок */
        shadowPainter.paint(canvas, paint)

        /** отрисовка стрелок */
        arrowsPainter.paint(canvas, paint)

        /** Отрисовка тени */
        radialShader.paint(canvas, paint)

        /** отложение перерисовки представления */
        postInvalidateDelayed(50)
        invalidate()
    }


}

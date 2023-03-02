package com.example.android.clock

import android.R.attr.shadowColor
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
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
        get() = radius * 0.93f
    private val radiusOfDashedCircle
        get() = radius * 0.88f
    private val lengthOfHours
        get() = radius * 0.5f
    private val lengthOfMinutes
        get() = radius * 0.6f
    private val lengthOfSeconds
        get() = radius * 0.75f

    private val mRect = Rect()

    // для других полей
    private val pointPosition = PointF(0.0f, 0.0f)
    private val clockArrowCoordinates = ClockArrowCoordinates()

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create( "", Typeface.NORMAL)
    }

    fun ClockArrowCoordinates.computeXYForSeconds() {
        // Angles are in radians.
        val localTime = LocalTime.now().second.toFloat() + LocalTime.now().nano / 1_000_000_000f
        val startAngle = Math.PI * (3/ 2.0)
        val angle = startAngle + localTime * (Math.PI / 30)
        xForHead = (lengthOfSeconds * cos(angle)).toFloat() + width / 2
        yForHead = (lengthOfSeconds * sin(angle)).toFloat() + height / 2
        xForTail = (lengthOfSeconds * cos(angle + Math.PI) * 0.3 ).toFloat() + width / 2
        yForTail = (lengthOfSeconds * sin(angle + Math.PI) * 0.3 ).toFloat() + height / 2
    }

    fun ClockArrowCoordinates.computeXYForMinutes() {
        // Angles are in radians.
        val localTime = LocalTime.now().minute.toFloat() + LocalTime.now().second / 60f
        val startAngle = Math.PI * (3/ 2.0)
        val angle = startAngle + localTime * (Math.PI / 30)
        xForHead = (lengthOfMinutes * cos(angle)).toFloat() + width / 2
        yForHead = (lengthOfMinutes * sin(angle)).toFloat() + height / 2
        xForTail = (lengthOfMinutes * cos(angle + Math.PI) * 0.3 ).toFloat() + width / 2
        yForTail = (lengthOfMinutes * sin(angle + Math.PI) * 0.3 ).toFloat() + height / 2
    }

    fun ClockArrowCoordinates.computeXYForHours() {
        // Angles are in radians.
        val localTime = (LocalTime.now().hour%12).toFloat() + LocalTime.now().minute / 60f
        val startAngle = Math.PI * (3/ 2.0)
        val angle = startAngle + localTime * (Math.PI / 6)
        xForHead = (lengthOfHours * cos(angle)).toFloat() + width / 2
        yForHead = (lengthOfHours * sin(angle)).toFloat() + height / 2
        xForTail = (lengthOfHours * cos(angle + Math.PI) * 0.3 ).toFloat() + width / 2
        yForTail = (lengthOfHours * sin(angle + Math.PI) * 0.3 ).toFloat() + height / 2
    }

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
        radius = (min(width, height) / 2.0 * 0.8).toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // drawing the edging
        paint.color = Color.BLACK
        canvas.drawCircle((width/2).toFloat(), (height/2).toFloat(), radius, paint)

        // drawing a dial
        paint.color = Color.WHITE
        canvas.drawCircle((width/2).toFloat(), (height/2).toFloat(), radiusOfInnerCircle, paint)

        paint.color = Color.BLACK

        for(minutes in 1..60){
            pointPosition.computeXYForDots(minutes, radiusOfDashedCircle)
            val sizeOfDot = if(minutes % 5 == 0) radius/80 else radius/100
            canvas.drawCircle(pointPosition.x, pointPosition.y, sizeOfDot, paint)
        }


        /** border of hours */
        val fontSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14f, resources.displayMetrics)
        paint.textSize = fontSize
        val radiusForHoursCircle = radius*0.75f
        for(hour in mClockHours) {
            val tmp = hour.toString()
            paint.getTextBounds(tmp, 0, tmp.length, mRect)

            pointPosition.computeXYForHours(hour, radiusForHoursCircle)
            canvas.drawText(tmp, pointPosition.x, pointPosition.y, paint)
        }


        //paint.setShadowLayer(2f, radius*0.05f, radius*0.05f, Color.GRAY)

        paint.strokeWidth = radius*0.01f
        clockArrowCoordinates.computeXYForSeconds()
        canvas.drawLine(
            width / 2f,
            height / 2f,
            clockArrowCoordinates.xForHead,
            clockArrowCoordinates.yForHead,
            paint)

        paint.strokeWidth = radius*0.03f
        clockArrowCoordinates.computeXYForMinutes()
        canvas.drawLine(
            clockArrowCoordinates.xForTail,
            clockArrowCoordinates.yForTail,
            clockArrowCoordinates.xForHead,
            clockArrowCoordinates.yForHead,
            paint)

        paint.strokeWidth = radius*0.05f
        clockArrowCoordinates.computeXYForHours()
        canvas.drawLine(
            clockArrowCoordinates.xForTail,
            clockArrowCoordinates.yForTail,
            clockArrowCoordinates.xForHead,
            clockArrowCoordinates.yForHead,
            paint)

        //paint.clearShadowLayer()

        postInvalidateDelayed(50)
        invalidate()
    }

    class ClockArrowCoordinates{
        var xForHead: Float = 0f
        var yForHead: Float = 0f
        var xForTail: Float = 0f
        var yForTail: Float = 0f
    }
}

//class HandsOfTheClock @JvmOverloads constructor(
//    context: Context,
//    attrs: AttributeSet? = null,
//    defStyleAttr: Int = 0
//): View(context, attrs, defStyleAttr) {
//
//    private val startAngle = Math.PI * (3/ 2.0)
//    private var mWidth = 0.0f
//    private var mHeight = 0.0f
//
//    private var radius = 0.0f
//    private val lengthOfHours
//        get() = radius * 0.93f
//    private val lengthOfMinutes
//        get() = radius * 0.88f
//    private val lengthOfSeconds
//        get() = radius * 0.88f
//
//    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
//        style = Paint.Style.FILL
//    }
//
//    private val pointPosition: PointF = PointF(0.0f, 0.0f)
//
//    private fun PointF.computeXYForSeconds(radius: Float) {
//        // Angles are in radians.
//        val localTime = LocalTime.now().second.toFloat() + LocalTime.now().nano / 1_000_000_000f
//        val startAngle = Math.PI * (3/ 2.0)
//        val angle = startAngle + localTime * (Math.PI / 60)
//        x = (radius * cos(angle)).toFloat() + width / 2
//        y = (radius * sin(angle)).toFloat() +  + height / 2
//    }
//
//    init {
//        ZonedDateTime.now().hour
//        Log.d("MyLog", ZonedDateTime.now().hour.toString())
//        Log.d("MyLog", ZonedDateTime.now().minute.toString())
//        Log.d("MyLog", ZonedDateTime.now().second.toString())
//        Log.d("MyLog", (ZonedDateTime.now().nano/1000000).toString())
//    }
//
//    override fun onDraw(canvas: Canvas) {
//        super.onDraw(canvas)
//
//        pointPosition.computeXYForSeconds(radius)
//        canvas.drawLine(width / 2f, height / 2f,
//            pointPosition.x, pointPosition.y, paint)
//    }
//
//    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
//        mWidth = MeasureSpec.getSize(widthMeasureSpec).toFloat()
//        mHeight = MeasureSpec.getSize(heightMeasureSpec).toFloat()
//        radius = mWidth / 2
//    }
//
//}
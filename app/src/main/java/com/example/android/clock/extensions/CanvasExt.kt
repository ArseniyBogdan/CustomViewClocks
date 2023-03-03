package com.example.android.clock.extensions

import android.graphics.Canvas
import android.graphics.Paint
import com.example.android.clock.Line

fun Canvas.drawClockArrow(strokeWidth: Float, line: Line, paint: Paint){
    paint.strokeWidth = strokeWidth
    drawLine(
        line.startX + line.offsetX,
        line.startY + line.offsetY,
        line.stopX + line.offsetX,
        line.stopY + line.offsetY,
        paint)
}
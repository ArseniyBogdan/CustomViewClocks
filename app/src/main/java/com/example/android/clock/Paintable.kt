package com.example.android.clock

import android.graphics.Canvas
import android.graphics.Paint

interface Paintable {
    fun paint(canvas: Canvas, paint: Paint)
}
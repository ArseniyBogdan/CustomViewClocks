package com.example.android.clock


/** класс отвечающий за конфигурацию линий перед отрисовкой */
class LineBuilder{
    private var startX: Float = 0f
    private var startY: Float = 0f
    private var stopX: Float = 0f
    private var stopY: Float = 0f
    private var offsetX: Float = 0f
    private var offsetY: Float = 0f

    fun startXY(x: Float, y: Float): LineBuilder{
        startX = x
        startY = y
        return this
    }

    fun stopXY(x: Float, y: Float): LineBuilder{
        stopX = x
        stopY = y
        return this
    }

    fun offsetXY(x: Float, y: Float): LineBuilder{
        offsetX = x
        offsetY = y
        return this
    }

    fun build(line: Line): Line {
        line.startX = startX
        line.startY = startY
        line.stopX = stopX
        line.stopY = stopY
        line.offsetX = offsetX
        line.offsetY = offsetY
        return line
    }

    fun configureBuilder(clockArrowCoordinates: ClockArrowCoordinates): LineBuilder{
        startX = clockArrowCoordinates.xTail
        startY = clockArrowCoordinates.yTail
        stopX = clockArrowCoordinates.xHead
        stopY = clockArrowCoordinates.yHead
        offsetX = 0f
        offsetY = 0f
        return this
    }
}
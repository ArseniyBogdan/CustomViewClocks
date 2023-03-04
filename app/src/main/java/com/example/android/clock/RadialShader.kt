package com.example.android.clock

import android.graphics.*
import android.graphics.Bitmap.createScaledBitmap
import android.graphics.BlurMaskFilter.Blur
import android.graphics.Matrix.ScaleToFit
import android.graphics.drawable.GradientDrawable
import androidx.core.graphics.drawable.toBitmap
import kotlin.math.min


class RadialShader(private val widthOfView: Float, private val heightOfView: Float): Paintable {
    private val radius = min(widthOfView, heightOfView) / 2
    private val radiusOfInnerCircle
        get() = radius * 0.97f
    private val bitmapSRC = Bitmap.createBitmap(widthOfView.toInt(), heightOfView.toInt(), Bitmap.Config.ARGB_8888)
    private val shaderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        xfermode = null
        color = Color.BLACK
        style = Paint.Style.FILL
        xfermode = xfermodeSrcIn
    }
    private val bitmapDST = shadowShaderCreate()
    private val canvasShadow = Canvas(bitmapDST)
    private val canvasCircles = Canvas(bitmapSRC)
    private val xfermodeSrcIn = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    private val xfermodeSrcOut = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
    private val modeClear = PorterDuff.Mode.CLEAR
//    private val shadow = addShadow()

    private val bitmapShadow = Bitmap.createBitmap(widthOfView.toInt(), heightOfView.toInt(), Bitmap.Config.ARGB_8888)
    private val canvasMask = Canvas(bitmapShadow)

    override fun paint(canvas: Canvas, paint: Paint) {
        /** cutting circle*/
        canvasCircles.drawCircle(widthOfView/2, heightOfView/2, radiusOfInnerCircle, shaderPaint)
        shaderPaint.xfermode = xfermodeSrcIn
        canvasShadow.drawBitmap(bitmapSRC, 0.0f, 0.0f, shaderPaint)

        /** clearing bitmap*/
        shaderPaint.xfermode = null
        canvasCircles.apply{
            drawColor(Color.TRANSPARENT, modeClear)
            drawCircle(widthOfView/2+radius*0.2f, heightOfView/2+radius*0.07f,
                radiusOfInnerCircle, shaderPaint)
        }

        /** cutting month*/
        shaderPaint.xfermode = xfermodeSrcOut
        canvasShadow.drawBitmap(bitmapSRC, 0.0f, 0.0f, shaderPaint)


        /** adding addition shadow */
        shaderPaint.apply {
            xfermode = null
            strokeWidth=30f
            style = Paint.Style.STROKE
        }
        canvasMask.drawCircle(widthOfView/2, heightOfView/2, radiusOfInnerCircle, shaderPaint)
        val shadowLayer = blurrableShadowCreate(bitmapShadow, widthOfView.toInt(), heightOfView.toInt(),
            Color.BLACK, 30, 15f, 3f)
        val canvasShadowLayer = Canvas(shadowLayer)
        shaderPaint.apply {
            reset()
            color = Color.BLACK
            style = Paint.Style.FILL
        }

        /** clearing bitmap*/
        canvasCircles.drawColor(Color.TRANSPARENT, modeClear)

        /** cutting Arc*/
        val oval = RectF()
        val center_x = widthOfView/2;
        val center_y = heightOfView/2;
        oval.set(center_x - radius, center_y - radius, center_x + radius, center_y + radius);
        canvasCircles.drawArc(oval, 90f, 200f, true, shaderPaint)
        shaderPaint.xfermode = xfermodeSrcIn
        canvasShadowLayer.drawBitmap(bitmapSRC, 0.0f, 0.0f, shaderPaint)

        /** painting shadows*/
        canvas.drawBitmap(shadowLayer, 0.0f, 0.0f, paint)
        canvas.drawBitmap(bitmapDST, 0.0f, 0.0f, paint)
    }

    fun prepareBlurrableShadow(){

    }

    fun prepareShadowShader(){
        /** cutting circle*/
        canvasCircles.drawCircle(widthOfView/2, heightOfView/2, radiusOfInnerCircle, shaderPaint)
        shaderPaint.xfermode = xfermodeSrcIn
        canvasShadow.drawBitmap(bitmapSRC, 0.0f, 0.0f, shaderPaint)

        /** clearing bitmap*/
        shaderPaint.xfermode = null
        canvasCircles.apply{
            drawColor(Color.TRANSPARENT, modeClear)
            drawCircle(widthOfView/2+radius*0.2f, heightOfView/2+radius*0.07f,
                radiusOfInnerCircle, shaderPaint)
        }

        /** cutting month*/
        shaderPaint.xfermode = xfermodeSrcOut
        canvasShadow.drawBitmap(bitmapSRC, 0.0f, 0.0f, shaderPaint)
    }

    private fun shadowShaderCreate(): Bitmap{
        val drawable = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(
                Color.argb(20, 0, 0, 0),
                Color.argb(0, 107, 107, 107),
            ))

        drawable.setSize(widthOfView.toInt(), heightOfView.toInt())
        return drawable.toBitmap(config = Bitmap.Config.ARGB_8888)
    }

    private fun blurrableShadowCreate(bm: Bitmap, dstWidth: Int, dstHeight: Int,
        color: Int, size: Int, dx: Float, dy: Float): Bitmap {
        val mask = Bitmap.createBitmap(dstWidth, dstHeight, Bitmap.Config.ALPHA_8)

        val scaleToFit = Matrix()
        val src = RectF(0f, 0f, bm.width.toFloat(), bm.height.toFloat())
        val dst = RectF(0f, 0f, dstWidth - dx, dstHeight - dy)
        scaleToFit.setRectToRect(src, dst, ScaleToFit.CENTER)

        val dropShadow = Matrix(scaleToFit)
        dropShadow.postTranslate(dx, dy)

        val maskCanvas = Canvas(mask)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        maskCanvas.drawBitmap(bm, scaleToFit, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OUT)
        maskCanvas.drawBitmap(bm, dropShadow, paint)

        val filter = BlurMaskFilter(size.toFloat(), Blur.NORMAL)
        paint.reset()
        paint.isAntiAlias = true
        paint.color = color
        paint.maskFilter = filter
        paint.isFilterBitmap = true

        val ret = Bitmap.createBitmap(dstWidth, dstHeight, Bitmap.Config.ARGB_8888)
        val retCanvas = Canvas(ret)
        retCanvas.drawBitmap(mask, 0f, 0f, paint)
        mask.recycle()
        return ret
    }

}
package com.example.android.clock

import android.graphics.*
import android.graphics.BlurMaskFilter.Blur
import android.graphics.Matrix.ScaleToFit
import android.graphics.drawable.GradientDrawable
import androidx.core.graphics.drawable.toBitmap
import kotlin.math.min


class RadialShader(private val widthOfView: Float,
                   private val heightOfView: Float,
                   private val radius: Float): Paintable {
    private val radiusOfShadowShader
        get() = radius * 0.93f
    private val radiusOfBlurredShadow
        get() = radius * 0.93f

    private val shadowShader: Bitmap = prepareShadowShader()
    private val shadowLayer: Bitmap = prepareBlurredShadow()
    private val shineLayer: Bitmap = createShine()

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.BLACK
    }

    override fun paint(canvas: Canvas) {
        /** painting shadows*/
        canvas.drawBitmap(shadowLayer, 0.0f, 0.0f, paint)
        canvas.drawBitmap(shadowShader, 0.0f, 0.0f, paint)

        /** отрисовка блеска */
        canvas.drawBitmap(shineLayer, 0.0f, 0.0f, paint)
    }

    private fun createShine(): Bitmap {
        val bitmapShine =
            Bitmap.createBitmap(widthOfView.toInt(), heightOfView.toInt(), Bitmap.Config.ARGB_8888)
        val canvasMask = Canvas(bitmapShine)
        val shaderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            strokeWidth = radius*0.02f
            style = Paint.Style.STROKE
        }

        canvasMask.drawCircle(widthOfView / 2, heightOfView / 2, radius, shaderPaint)

        return blurredShadowCreate(
            bitmapShine, widthOfView.toInt(), heightOfView.toInt(),
            Color.WHITE, 1, (radius*0.01f).toInt().toFloat(),
            (radius*0.005f).toInt().toFloat()
        )
    }

    private fun prepareBlurredShadow(): Bitmap{
        /** adding addition shadow */
        val bitmapShadow = Bitmap.createBitmap(widthOfView.toInt(), heightOfView.toInt(), Bitmap.Config.ARGB_8888)
        val canvasMask = Canvas(bitmapShadow)
        val shaderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            strokeWidth = 30f
            style = Paint.Style.STROKE
        }

        canvasMask.drawCircle(widthOfView/2, heightOfView/2, radiusOfBlurredShadow, shaderPaint)
        val shadowLayer = blurredShadowCreate(bitmapShadow, widthOfView.toInt(), heightOfView.toInt(),
            Color.BLACK, 30, 15f, 3f)
        cutArc(shadowLayer, radiusOfBlurredShadow, 90f, 200f, 0f,0f,
            PorterDuffXfermode(PorterDuff.Mode.SRC_IN))

        return shadowLayer
    }

    private fun prepareShadowShader(): Bitmap{
        val bitmapDST = shadowShaderCreate()
        /** cutting circles*/
        cutArc(bitmapDST, radiusOfShadowShader,0f, 360f, 0f,0f,
            PorterDuffXfermode(PorterDuff.Mode.SRC_IN))
        cutArc(bitmapDST, radiusOfShadowShader,0f, 360f, radius*0.2f,radius*0.07f,
            PorterDuffXfermode(PorterDuff.Mode.DST_OUT))

        return bitmapDST
    }

    private fun cutArc(Layer: Bitmap, radiusOfInnerCircle: Float ,
                       startAngle: Float, sweepAngle: Float,
                       offsetX: Float, offsetY: Float, xFerMode: Xfermode){
        val bitmapSRC = Bitmap.createBitmap(widthOfView.toInt(), heightOfView.toInt(), Bitmap.Config.ARGB_8888)
        val canvasCircles = Canvas(bitmapSRC)
        val canvasShadowLayer = Canvas(Layer)
        val shaderPaint = Paint().apply {
            reset()
            color = Color.BLACK
            style = Paint.Style.FILL
        }

        /** cutting Arc*/
        val oval = RectF()
        val centerX = widthOfView/2
        val centerY = heightOfView/2
        oval.set(centerX - radiusOfInnerCircle + offsetX,
            centerY - radiusOfInnerCircle + offsetY,
            centerX + radiusOfInnerCircle + offsetX,
            centerY + radiusOfInnerCircle + offsetY)

        canvasCircles.drawArc(oval, startAngle, sweepAngle, false, shaderPaint)
        shaderPaint.xfermode = xFerMode
        canvasShadowLayer.drawBitmap(bitmapSRC, 0.0f, 0.0f, shaderPaint)
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

    private fun blurredShadowCreate(bm: Bitmap, dstWidth: Int, dstHeight: Int,
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
        paint.apply {
            reset()
            isAntiAlias = true
            this.color = color
            maskFilter = filter
            isFilterBitmap = true
        }

        val ret = Bitmap.createBitmap(dstWidth, dstHeight, Bitmap.Config.ARGB_8888)
        val retCanvas = Canvas(ret)
        retCanvas.drawBitmap(mask, 0f, 0f, paint)
        mask.recycle()
        return ret
    }

}
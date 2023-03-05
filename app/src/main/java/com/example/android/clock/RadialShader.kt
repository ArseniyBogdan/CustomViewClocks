package com.example.android.clock

import android.graphics.*
import android.graphics.BlurMaskFilter.Blur
import android.graphics.Matrix.ScaleToFit
import android.graphics.drawable.GradientDrawable
import androidx.core.graphics.drawable.toBitmap
import kotlin.math.min


class RadialShader(private val widthOfView: Float, private val heightOfView: Float): Paintable {
    private val radius = min(widthOfView, heightOfView) / 2 * 0.9f
    private val radiusOfInnerCircle
        get() = radius * 0.97f

    private val shadowShader: Bitmap
    private val shadowLayer: Bitmap
    private val xfermodeSrcIn = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    private val xfermodeSrcOut = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
    private val modeClear = PorterDuff.Mode.CLEAR

    init{
        shadowShader = prepareShadowShader()
        shadowLayer = prepareBlurrableShadow()
    }

    override fun paint(canvas: Canvas, paint: Paint) {
        /** painting shadows*/
        canvas.drawBitmap(shadowLayer, 0.0f, 0.0f, paint)
        canvas.drawBitmap(shadowShader, 0.0f, 0.0f, paint)


        /** отрисовка блеска */
        val shapeShine = createShapeShine()
        canvas.drawBitmap(shapeShine, 0.0f, 0.0f, paint)
    }

    fun createShapeShine(): Bitmap{
        val bitmapSRC = Bitmap.createBitmap(widthOfView.toInt(), heightOfView.toInt(), Bitmap.Config.ARGB_8888)
        val bitmapShine = Bitmap.createBitmap(widthOfView.toInt(), heightOfView.toInt(), Bitmap.Config.ARGB_8888)
        val canvasMask = Canvas(bitmapShine)
        val canvasCircles = Canvas(bitmapSRC)
        val shaderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            strokeWidth = 3f
            style = Paint.Style.STROKE
            xfermode = null
        }

        val oval = RectF()
        val center_x = widthOfView/2;
        val center_y = heightOfView/2;
        oval.set(center_x - radius, center_y - radius,
            center_x + radius, center_y + radius);

        canvasMask.drawArc(oval, 120f,160f, false, shaderPaint)
        val shadowLayer = blurrableShadowCreate(bitmapShine, widthOfView.toInt(), heightOfView.toInt(),
            Color.WHITE, 2, 2f, 1f)
        val canvasShadowLayer = Canvas(shadowLayer)
        shaderPaint.apply {
            reset()
            color = Color.WHITE
            style = Paint.Style.FILL
        }

        /** clearing bitmap*/
        canvasCircles.drawColor(Color.TRANSPARENT, modeClear)

        /** cutting Arc*/
        canvasCircles.drawArc(oval, 100f, 190f, true, shaderPaint)
        shaderPaint.xfermode = xfermodeSrcIn
        canvasShadowLayer.drawBitmap(bitmapSRC, 0.0f, 0.0f, shaderPaint)

        return shadowLayer
    }

    fun prepareBlurrableShadow(): Bitmap{
        /** adding addition shadow */
        val bitmapSRC = Bitmap.createBitmap(widthOfView.toInt(), heightOfView.toInt(), Bitmap.Config.ARGB_8888)
        val bitmapShadow = Bitmap.createBitmap(widthOfView.toInt(), heightOfView.toInt(), Bitmap.Config.ARGB_8888)
        val canvasMask = Canvas(bitmapShadow)
        val canvasCircles = Canvas(bitmapSRC)
        val shaderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            strokeWidth = 30f
            style = Paint.Style.STROKE
            xfermode = null
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
        oval.set(center_x - radiusOfInnerCircle, center_y - radiusOfInnerCircle,
            center_x + radiusOfInnerCircle, center_y + radiusOfInnerCircle);
        canvasCircles.drawArc(oval, 90f, 200f, true, shaderPaint)
        shaderPaint.xfermode = xfermodeSrcIn
        canvasShadowLayer.drawBitmap(bitmapSRC, 0.0f, 0.0f, shaderPaint)
        return shadowLayer
    }

    private fun prepareShadowShader(): Bitmap{
        /** cutting circle*/
        val bitmapSRC = Bitmap.createBitmap(widthOfView.toInt(), heightOfView.toInt(), Bitmap.Config.ARGB_8888)
        val bitmapDST = shadowShaderCreate()
        val shaderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            xfermode = null
            color = Color.BLACK
            style = Paint.Style.FILL
        }
        val canvasShadow = Canvas(bitmapDST)
        val canvasCircles = Canvas(bitmapSRC)
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
        return bitmapDST
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
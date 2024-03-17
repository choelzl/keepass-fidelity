package net.helcel.fidelity.activity.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.util.AttributeSet
import android.view.View

class ScannerView : View {

    private val overlayPaint = Paint().apply {
        color = Color.parseColor("#80000000") // Semi-transparent black
        style = Paint.Style.FILL
    }

    private val clearPaint = Paint().apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), overlayPaint)

        val centerX = width / 2f
        val centerY = height / 2f
        val squareSize = 0.75f * width.coerceAtMost(height)
        canvas.drawRect(
            centerX - squareSize / 2, centerY - squareSize / 2,
            centerX + squareSize / 2, centerY + squareSize / 2, clearPaint
        )
    }
}


package com.example.in2000_team32.ui.home

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.example.in2000_team32.R

class UvForecastGraphView(context: Context?, attrs: AttributeSet?): View(context, attrs) {
    private val DATALENGTH = 25

    private var paint = Paint()
    private var canvasWidth: Float = 0f
    private var canvasHeight: Float = 400f

    private var data = MutableList(25) { 0f } // Init list with all zeros
    private var startTime: Int = 12 // Default start time is 12

    init {
        paint.color = Color.rgb(200, 10, 10)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        canvasWidth = w.toFloat()
        canvasHeight = h.toFloat()
    }

    /**
     * @param d: UV data as a list of Floats. Must be of length DATALENGTH (25)
     * @param t: The hour of the first forecast in d.
     */
    fun addData(d: List<Double>, t: Int) {
        for (i in 0 until DATALENGTH) {
            data[i] = d[i].toFloat()
        }
        startTime = t

        invalidate() // Called to make sure draw updates
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        val numberOfBars = DATALENGTH

        var timeXaxis = startTime

        val margin = 5f
        val leftMargin = 30f
        val boxWidth = ((canvasWidth - margin - leftMargin) / numberOfBars)

        // User specified
        var barHeight = 0f

        val scale = 10
        val blockHeight = (canvasHeight / scale) - 10
        val bottomMargin = 30

        // Y axis
        val yP: Paint = Paint()
        yP.setColor(Color.GRAY)
        yP.strokeWidth = 1.0f
        yP.textSize = 20f

        var y: Float
        var text: String
        for (i in 0 until 12) {
            barHeight = i * blockHeight
            y = ((canvasHeight - bottomMargin) - barHeight)

            if (i % 2 == 0) {
                text = if (i < 10) i.toString().padStart(3) else i.toString()
                canvas?.drawLine(0f + leftMargin, y, canvasWidth, y, yP)
                canvas?.drawText(text, 0f, y + 7f, yP)
            }
        }

        // Data bars
        for (i in 0 until DATALENGTH) {

            barHeight = data[i] * blockHeight

            paint.color = context.getColor(R.color.graph_purple).toInt()
            if (data[i] < 10) paint.color = context.getColor(R.color.graph_purple).toInt()
            if (data[i] < 8) paint.color = context.getColor(R.color.graph_red).toInt()
            if (data[i] < 6) paint.color = context.getColor(R.color.graph_orange).toInt()
            if (data[i] < 4) paint.color = context.getColor(R.color.graph_yellow).toInt()
            if (data[i] < 2) paint.color = context.getColor(R.color.graph_green).toInt()

            //paint.shader = LinearGradient(0f, 0f, 0f, 100f, 0xFF1F9928.toInt(), 0xFF184F1E.toInt(), Shader.TileMode.MIRROR)

            // Math to place bars correctly
            val left = (i * boxWidth) + margin + leftMargin
            val right = (left + boxWidth) - margin
            val top = ((canvasHeight - bottomMargin) - barHeight)
            val bottom = canvasHeight - bottomMargin
            canvas?.drawRoundRect(RectF(left, top, right, bottom), 6f, 6f, paint)

            // Write text to indicate time on x axis
            val p: Paint = Paint()
            val timeInterval = 6
            if (i % timeInterval == 0) {
                p.setColor(Color.GRAY)
                p.setTextSize(28f)
                val timeString: String = "%02d".format(timeXaxis)
                canvas?.drawText(timeString, (i * boxWidth) + (margin / 2) + leftMargin, height - 0f, p)
                timeXaxis += timeInterval
                timeXaxis %= 24 // Roll over on 24 hours
            }
        }
    }
}
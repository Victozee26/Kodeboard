package com.victozee.kodeboard.layout.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.inputmethodservice.KeyboardView
import android.view.MotionEvent
import android.view.View
import android.view.ViewOutlineProvider
import com.victozee.kodeboard.layout.Box
import com.victozee.kodeboard.layout.Key
import com.victozee.kodeboard.theme.UiTheme
import java.util.Timer
import java.util.TimerTask

class KeyboardButtonView(
    context: Context,
    private val key: Key,
    private val inputService: KeyboardView.OnKeyboardActionListener,
    private val uiTheme: UiTheme
) : View(context) {

    private var timer: Timer? = null
    private var currentLabel: String? = null
    private var isPressed: Boolean = false

    init {
        currentLabel = key.info.label
        outlineProvider = ViewOutlineProvider.BOUNDS
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        when (e.action) {
            MotionEvent.ACTION_DOWN -> onPress()
            MotionEvent.ACTION_UP -> onRelease()
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_OUTSIDE -> releaseIfPressed()
        }
        return true
    }

    override fun layout(l: Int, t: Int, r: Int, b: Int) {
        val box: Box = key.box
        val w = r - l
        val h = b - t
        val left = (l + w * box.getLeft()).toInt()
        val right = (l + w * box.getRight()).toInt()
        val top = (t + h * box.getTop()).toInt()
        val bottom = (t + h * box.getBottom()).toInt()
        super.layout(left, top, right, bottom)
    }

    override fun draw(canvas: Canvas) {
        drawButtonBody(canvas)
        drawButtonContent(canvas)
        super.draw(canvas)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        autoReleaseIfPressed()
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        autoReleaseIfPressed()
    }

    private fun drawButtonContent(canvas: Canvas) {
        val fg = uiTheme.getForegroundForKey(key.info.code)
        // use a copy so we don't mutate shared paint
        val textPaint = Paint(uiTheme.foregroundPaint).apply {
            color = fg
            // Shift glyph needs to be bigger to match GBoard arrow
            if (key.info.code == 16) {
                textSize = uiTheme.fontHeight * 1.6f
            }
        }
        currentLabel?.let { label ->
            if (label.isNotEmpty()) {
                val x = width / 2f
                // center using font metrics, lift comma/dot slightly
                val yOffset = when (label) {
                    ",", "." -> uiTheme.fontHeight / 4f
                    else -> uiTheme.fontHeight / 3f
                }
                val xOffset = when (label) {
                    "?123" -> -2f
                    else -> 0f
                }
                val y = height / 2f + yOffset
                canvas.drawText(label, x + xOffset, y, textPaint)
            }
        }

        val icon: Drawable? = key.info.icon
        if (icon != null) {
            val d: Drawable = icon
            d.setTint(fg)
            // GBoard: icon ~45% of key height, centered, never touches edges
            val iconH = height * 0.45f
            val iconW = (width * 0.6f).coerceAtMost(iconH * 1.4f)
            val halfW = iconW / 2f
            val halfH = iconH / 2f
            val cx = width / 2f
            val cy = height / 2f
            val left = (cx - halfW).toInt()
            val top = (cy - halfH).toInt()
            val right = (cx + halfW).toInt()
            val bottom = (cy + halfH).toInt()
            if (right > left && bottom > top) {
                d.setBounds(left, top, right, bottom)
                d.draw(canvas)
            }
        }
    }

    private fun drawButtonBody(canvas: Canvas) {
        val padH = uiTheme.buttonBodyPadding
        val padV = uiTheme.buttonBodyPaddingVertical
        val left = padH
        val top = padV
        val right = width - padH
        val bottom = height - padV
        // GBoard: letters rounded-rect, wide keys full pill
        val isWide = width > height * 1.5f
        val rx: Float
        val ry: Float
        if (isWide) {
            // full pill for space/?123/enter
            rx = (bottom - top) / 2f * 0.9f
            ry = rx
        } else {
            rx = uiTheme.buttonBodyBorderRadius
            ry = rx
        }
        val paint = Paint(uiTheme.buttonBodyPaint).apply {
            color = uiTheme.getKeyColor(key.info.code, key.info.isModifier)
            isAntiAlias = true
        }
        if (right > left && bottom > top) {
            canvas.drawRoundRect(left, top, right, bottom, rx, ry, paint)
        }
    }

    private fun onPress() {
        isPressed = true
        inputService.onPress(key.info.code)
        if (key.info.isRepeatable) {
            startRepeating()
        }
        submitKeyEvent()
        animatePress()
    }

    private fun onRelease() {
        releaseIfPressed()
    }

    private fun submitKeyEvent() {
        if (key.info.code != 0) {
            inputService.onKey(key.info.code, null)
        }
        val outputText = key.info.outputText
        if (outputText != null) {
            inputService.onText(outputText)
        }
    }

    private fun autoReleaseIfPressed() {
        releaseIfPressed()
    }

    fun releaseIfPressed() {
        val wasPressed = isPressed
        isPressed = false
        try {
            timer?.cancel()
        } catch (_: Exception) {
        }
        timer = null
        // Reset visuals unconditionally so a stuck key can never stay dim/lifted.
        alpha = 1.0f
        translationY = 0.0f
        scaleX = 1.0f
        scaleY = 1.0f
        elevation = 0.0f
        if (wasPressed && key.info.code != 0) {
            try {
                inputService.onRelease(key.info.code)
            } catch (_: Exception) {
            }
        }
    }

    private fun stopRepeating() {
        val t = timer ?: return
        t.cancel()
        timer = null
    }

    private fun startRepeating() {
        if (timer != null) {
            stopRepeating()
            return
        }
        timer = Timer()
        timer!!.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                submitKeyEvent()
            }
        }, 400, 50)
    }

    private fun animatePress() {
        if (uiTheme.enablePreview) {
            translationY = -200.0f
            scaleX = 1.2f
            scaleY = 1.2f
            elevation = 21.0f
        } else {
            alpha = 0.1f
        }
    }

    private fun animateRelease() {
        if (uiTheme.enablePreview) {
            translationY = 0.0f
            scaleX = 1.0f
            scaleY = 1.0f
            elevation = 0.0f
        } else {
            // reset immediately to avoid stuck translucent state in screenshots
            alpha = 1.0f
        }
    }

    fun applyShiftModifier(shiftPressed: Boolean) {
        val onShiftLabel = key.info.onShiftLabel
        if (onShiftLabel != null) {
            val nextLabel = if (shiftPressed) onShiftLabel else key.info.label
            setCurrentLabel(nextLabel)
        }
    }

    fun applyCtrlModifier(ctrlPressed: Boolean) {
        val onCtrlLabel = key.info.onCtrlLabel
        if (onCtrlLabel != null) {
            val nextLabel = if (ctrlPressed) onCtrlLabel else key.info.label
            setCurrentLabel(nextLabel)
        }
    }

    private fun setCurrentLabel(nextLabel: String?) {
        if (nextLabel != currentLabel) {
            currentLabel = nextLabel
            invalidate()
        }
    }
}

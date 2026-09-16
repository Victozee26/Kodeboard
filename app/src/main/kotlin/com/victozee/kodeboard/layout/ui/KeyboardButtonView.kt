package com.victozee.kodeboard.layout.ui

import android.content.Context
import android.graphics.Canvas
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
        currentLabel?.let { label ->
            val x = width / 2f
            val y = height / 2f + uiTheme.fontHeight / 3f
            canvas.drawText(label, x, y, uiTheme.foregroundPaint)
        }

        val icon: Drawable? = key.info.icon
        if (icon != null) {
            val d: Drawable = icon
            d.setTint(uiTheme.foregroundPaint.color)
            val padding = uiTheme.buttonBodyPadding.toInt() * 2
            val top: Int
            val left: Int
            val squareSize: Int
            if (width > height) {
                top = 2 * padding
                squareSize = height / 2 - top
                left = width / 2 - squareSize
            } else {
                left = 2 * padding
                squareSize = width / 2 - left
                top = height / 2 - squareSize
            }
            val right = left + squareSize * 2
            val bottom = top + squareSize * 2
            d.setBounds(left, top, right, bottom)
            d.draw(canvas)
        }
    }

    private fun drawButtonBody(canvas: Canvas) {
        val left = uiTheme.buttonBodyPadding
        val top = uiTheme.buttonBodyPadding
        val right = width - uiTheme.buttonBodyPadding
        val bottom = height - uiTheme.buttonBodyPadding
        val rx = uiTheme.buttonBodyBorderRadius
        val ry = uiTheme.buttonBodyBorderRadius
        canvas.drawRoundRect(left, top, right, bottom, rx, ry, uiTheme.buttonBodyPaint)
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
        isPressed = false
        if (key.info.code != 0) {
            inputService.onRelease(key.info.code)
        }
        if (key.info.isRepeatable) {
            stopRepeating()
        }
        animateRelease()
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
        if (isPressed) {
            onRelease()
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
            animate().alpha(1.0f).setDuration(400)
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

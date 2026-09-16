package com.victozee.kodeboard.layout.ui

import android.content.Context
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import com.victozee.kodeboard.theme.UiTheme
import java.util.ArrayList

class KeyboardLayoutView(context: Context, private val uiTheme: UiTheme) : ViewGroup(context) {

    var onSwipeLeft: (() -> Unit)? = null
    var onSwipeRight: (() -> Unit)? = null

    private var downX = 0f
    private var downY = 0f
    private var isScrolling = false

    init {
        setBackgroundColor(uiTheme.backgroundColor)
    }

    override fun onInterceptTouchEvent(ev: android.view.MotionEvent): Boolean {
        when (ev.action) {
            android.view.MotionEvent.ACTION_DOWN -> {
                downX = ev.x
                downY = ev.y
                isScrolling = false
            }
            android.view.MotionEvent.ACTION_MOVE -> {
                val dx = ev.x - downX
                val dy = ev.y - downY
                if (!isScrolling && kotlin.math.abs(dx) > 80 && kotlin.math.abs(dx) > kotlin.math.abs(dy) * 1.5f) {
                    isScrolling = true
                    return true
                }
            }
            android.view.MotionEvent.ACTION_UP, android.view.MotionEvent.ACTION_CANCEL -> {
                if (isScrolling) {
                    val dx = ev.x - downX
                    if (kotlin.math.abs(dx) > 120) {
                        if (dx < 0) onSwipeLeft?.invoke() else onSwipeRight?.invoke()
                    }
                    isScrolling = false
                    return true
                }
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: android.view.MotionEvent): Boolean {
        if (isScrolling) {
            if (event.action == android.view.MotionEvent.ACTION_UP || event.action == android.view.MotionEvent.ACTION_CANCEL) {
                val dx = event.x - downX
                if (kotlin.math.abs(dx) > 120) {
                    if (dx < 0) onSwipeLeft?.invoke() else onSwipeRight?.invoke()
                }
                isScrolling = false
            }
            return true
        }
        return super.onTouchEvent(event)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val metrics: DisplayMetrics = context.resources.displayMetrics
        val availableHeight = metrics.heightPixels
        val availableWidth = metrics.widthPixels
        val keyboardSize = if (availableHeight > availableWidth) uiTheme.portraitSize else uiTheme.landscapeSize
        setMeasuredDimension(availableWidth, (availableHeight * keyboardSize).toInt())
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val count = childCount
        for (i in 0 until count) {
            val child: View = getChildAt(i)
            child.layout(l, t, r, b)
        }
    }

    fun applyShiftModifier(shiftPressed: Boolean) {
        for (button in getKeyboardButtons()) {
            button.applyShiftModifier(shiftPressed)
        }
    }

    fun applyCtrlModifier(ctrlPressed: Boolean) {
        for (button in getKeyboardButtons()) {
            button.applyCtrlModifier(ctrlPressed)
        }
    }

    private fun getKeyboardButtons(): Collection<KeyboardButtonView> {
        val childCount = childCount
        val list = ArrayList<KeyboardButtonView>(childCount)
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child is KeyboardButtonView) {
                list.add(child)
            }
        }
        return list
    }
}

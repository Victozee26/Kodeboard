package com.victozee.kodeboard.layout.ui

import android.content.Context
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import com.victozee.kodeboard.theme.UiTheme
import java.util.ArrayList

class KeyboardLayoutView(context: Context, private val uiTheme: UiTheme) : ViewGroup(context) {

    init {
        setBackgroundColor(uiTheme.backgroundColor)
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

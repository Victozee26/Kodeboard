package com.victozee.kodeboard.layout.ui

import android.content.Context
import android.inputmethodservice.KeyboardView
import android.view.View
import android.widget.RelativeLayout
import com.victozee.kodeboard.layout.Box
import com.victozee.kodeboard.layout.Key
import com.victozee.kodeboard.theme.ThemeDefinitions
import com.victozee.kodeboard.theme.ThemeInfo
import com.victozee.kodeboard.theme.UiTheme

class KeyboardUiFactory(private val inputService: KeyboardView.OnKeyboardActionListener) {

    var theme: ThemeInfo = ThemeDefinitions.Default()

    fun createKeyboardView(context: Context, keys: List<Key>): KeyboardLayoutView {
        val uiTheme = UiTheme.buildFromInfo(theme)
        val layout = createKeyGroupView(context, uiTheme)
        for (key in keys) {
            val params = getKeyLayoutParams(key)
            val view: View = createKeyView(context, key, uiTheme)
            layout.addView(view, params)
        }
        return layout
    }

    private fun createKeyGroupView(context: Context, uiTheme: UiTheme): KeyboardLayoutView {
        return KeyboardLayoutView(context, uiTheme)
    }

    private fun createKeyView(context: Context, key: Key, uiTheme: UiTheme): KeyboardButtonView {
        val view = KeyboardButtonView(context, key, inputService, uiTheme)
        val box: Box = key.box
        view.layout(box.getLeft().toInt(), box.getTop().toInt(), box.getRight().toInt(), box.getBottom().toInt())
        return view
    }

    private fun getKeyLayoutParams(key: Key): RelativeLayout.LayoutParams {
        val width = key.box.width.toInt()
        val height = key.box.height.toInt()
        val params = RelativeLayout.LayoutParams(width, height)
        params.leftMargin = key.box.x.toInt()
        params.topMargin = key.box.y.toInt()
        return params
    }
}

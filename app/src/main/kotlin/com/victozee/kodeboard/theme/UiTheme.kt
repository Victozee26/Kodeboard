package com.victozee.kodeboard.theme

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import androidx.core.graphics.ColorUtils

class UiTheme private constructor() {

    var foregroundPaint: Paint
    var backgroundColor: Int
    var fontHeight: Float = 0f

    var buttonBodyPadding: Float = 7f
    var buttonBodyPaddingVertical: Float = 16f
    var buttonBodyPaint: Paint
    var buttonBodyBorderRadius: Float = 14.0f
    var enablePreview: Boolean = false
    var portraitSize: Float = 0f
    var landscapeSize: Float = 0f

    // GBoard-like palette
    var keyNormalColor: Int = 0xFF3C4043.toInt()
    var keyFunctionalColor: Int = 0xFF2D2E30.toInt()
    var keyEnterColor: Int = 0xFF8AB4F8.toInt()
    var keyEnterForeground: Int = 0xFF202124.toInt()
    var keyActiveColor: Int = 0xFF80868B.toInt()

    // Codes of armed modifiers (shift/ctrl/alt/fn) shown brighter while active
    var activeCodes: MutableSet<Int> = mutableSetOf()

    init {
        foregroundPaint = Paint()
        buttonBodyPaint = Paint()
        backgroundColor = 0xFF000000.toInt()
    }

    fun getKeyColor(code: Int, isModifier: Boolean): Int {
        // Armed modifiers glow brighter while active
        if (code != -4 && activeCodes.contains(code)) {
            return keyActiveColor
        }
        return when (code) {
            -4 -> keyEnterColor // Enter blue
            16, 17, -30, -31, 9, -2, -1 -> keyFunctionalColor // Shift, Ctrl, Alt(-30), Fn(-31), Tab, Esc, SYM (?123)
            -5 -> keyFunctionalColor // Backspace
            44, 46 -> keyFunctionalColor // , . GBoard dark like ?123
            53737, 53738, 53739, 53740, 53741, 53742 -> keyFunctionalColor
            else -> {
                // F-keys, arrows, navigation are functional
                if (code in -23..-6 || code in 5000..5003) keyFunctionalColor
                else if (isModifier) keyFunctionalColor
                else keyNormalColor
            }
        }
    }

    fun getForegroundForKey(code: Int): Int {
        return if (code == -4) keyEnterForeground else foregroundPaint.color
    }

    companion object {
        @JvmStatic
        fun buildFromInfo(info: ThemeInfo): UiTheme {
            val theme = UiTheme()
            theme.portraitSize = info.size
            theme.landscapeSize = info.sizeLandscape
            theme.enablePreview = info.enablePreview
            if (info.enableBorder) {
                theme.backgroundColor = ColorUtils.blendARGB(info.backgroundColor, Color.BLACK, 0.2f)
            } else {
                theme.backgroundColor = info.backgroundColor
            }
            // GBoard-like: background pure black or dark, keys slightly lighter
            // Keep background as theme background, but derive key colors
            val isDark = ColorUtils.calculateLuminance(info.backgroundColor) < 0.5
            if (isDark) {
                theme.keyNormalColor = 0xFF3C4043.toInt()
                theme.keyFunctionalColor = 0xFF202124.toInt()
                theme.keyEnterColor = 0xFF8AB4F8.toInt()
                theme.keyEnterForeground = 0xFFFFFFFF.toInt()
                theme.keyActiveColor = 0xFF80868B.toInt()
                // GBoard dark background is pure black
                theme.backgroundColor = 0xFF000000.toInt()
            } else {
                theme.keyNormalColor = 0xFFFFFFFF.toInt()
                theme.keyFunctionalColor = 0xFFE8EAED.toInt()
                theme.keyEnterColor = 0xFF1A73E8.toInt()
                theme.keyEnterForeground = 0xFFFFFFFF.toInt()
                theme.keyActiveColor = 0xFFBDC1C6.toInt()
            }
            theme.buttonBodyPaint.color = theme.keyNormalColor
            theme.foregroundPaint.color = info.foregroundColor
            theme.fontHeight = info.fontSize * 0.95f
            theme.foregroundPaint.textSize = theme.fontHeight
            theme.foregroundPaint.textAlign = Paint.Align.CENTER
            theme.foregroundPaint.isAntiAlias = true
            theme.foregroundPaint.typeface = Typeface.DEFAULT
            // GBoard sizing - short wide keys: small gutters for 26% height board
            theme.buttonBodyPadding = 5f
            theme.buttonBodyPaddingVertical = 6f
            theme.buttonBodyBorderRadius = 10.0f
            return theme
        }
    }
}

package com.victozee.kodeboard.theme

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import androidx.core.graphics.ColorUtils

class UiTheme private constructor() {

    var foregroundPaint: Paint
    var backgroundColor: Int
    var fontHeight: Float = 0f

    var buttonBodyPadding: Float = 5.0f
    var buttonBodyPaint: Paint
    var buttonBodyBorderRadius: Float = 8.0f
    var enablePreview: Boolean = false
    var enableBorder: Boolean = false
    var portraitSize: Float = 0f
    var landscapeSize: Float = 0f

    init {
        foregroundPaint = Paint()
        buttonBodyPaint = Paint()
        backgroundColor = 0xFF000000.toInt()
    }

    companion object {
        @JvmStatic
        fun buildFromInfo(info: ThemeInfo): UiTheme {
            val theme = UiTheme()
            theme.portraitSize = info.size
            theme.landscapeSize = info.sizeLandscape
            theme.enablePreview = info.enablePreview
            theme.enableBorder = info.enableBorder
            if (info.enableBorder) {
                theme.backgroundColor = ColorUtils.blendARGB(info.backgroundColor, Color.BLACK, 0.2f)
            } else {
                theme.backgroundColor = info.backgroundColor
            }
            theme.buttonBodyPaint.color = info.backgroundColor
            theme.foregroundPaint.color = info.foregroundColor
            theme.fontHeight = info.fontSize
            theme.foregroundPaint.textSize = theme.fontHeight
            theme.foregroundPaint.textAlign = Paint.Align.CENTER
            theme.foregroundPaint.isAntiAlias = true
            theme.foregroundPaint.typeface = Typeface.DEFAULT
            return theme
        }
    }
}

package com.victozee.kodeboard.theme

object ThemeDefinitions {

    private val whiteColor = 0xFFFFFFFF.toInt()
    private val blackColor = 0xFF000000.toInt()

    @JvmStatic
    fun Default(): ThemeInfo = MaterialDark()

    @JvmStatic
    fun MaterialDark(): ThemeInfo = ThemeInfo().apply {
        foregroundColor = whiteColor
        backgroundColor = 0xFF263238.toInt()
    }

    @JvmStatic
    fun MaterialWhite(): ThemeInfo = Default().apply {
        foregroundColor = blackColor
        backgroundColor = 0xFFECEFF1.toInt()
    }

    @JvmStatic
    fun PureBlack(): ThemeInfo = MaterialDark().apply {
        backgroundColor = blackColor
    }

    @JvmStatic
    fun White(): ThemeInfo = MaterialWhite().apply {
        backgroundColor = whiteColor
    }

    @JvmStatic
    fun Blue(): ThemeInfo = MaterialDark().apply {
        backgroundColor = 0xFF0D47A1.toInt()
    }

    @JvmStatic
    fun Purple(): ThemeInfo = MaterialDark().apply {
        backgroundColor = 0xFF4A148C.toInt()
    }
}

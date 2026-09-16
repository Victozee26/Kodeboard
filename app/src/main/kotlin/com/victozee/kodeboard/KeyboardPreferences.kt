package com.victozee.kodeboard

import android.content.ContextWrapper
import android.content.SharedPreferences
import android.content.res.Resources
import android.util.DisplayMetrics
import android.util.TypedValue
import androidx.preference.PreferenceManager

class KeyboardPreferences(contextWrapper: ContextWrapper) {
    private val preferences: SharedPreferences
    private val res: Resources

    init {
        res = contextWrapper.getResources()
        preferences = PreferenceManager.getDefaultSharedPreferences(contextWrapper)
    }

    fun isFirstStart(): Boolean {
        return read("FIRST_START", true)
    }

    fun setFirstStart(value: Boolean) {
        write("FIRST_START", value)
    }

    fun isSoundEnabled(): Boolean {
        return read("sound", res.getBoolean(R.bool.sound))
    }

    fun isVibrateEnabled(): Boolean {
        return try {
            read("vibrate", res.getBoolean(R.bool.vibrate))
        } catch (e: Exception) {
            true
        }
    }

    fun getVibrateLength(): Int {
        return try {
            Integer.parseInt(safeRead("vibrate_ms", res.getInteger(R.integer.vibrate_length).toString()))
        } catch (e: Exception) {
            1
        }
    }

    fun getBgColor(): Int {
        return Integer.parseInt(safeRead("bg_colour_picker", res.getInteger(R.integer.bg_color).toString()))
    }

    fun setBgColor(color: String) {
        write("bg_colour_picker", color)
    }

    fun getFgColor(): Int {
        return Integer.parseInt(safeRead("fg_colour_picker", res.getInteger(R.integer.fg_color).toString()))
    }

    fun setFgColor(color: String) {
        write("fg_colour_picker", color)
    }

    fun getPortraitSize(): Int {
        return try {
            Integer.parseInt(safeRead("size_portrait", res.getInteger(R.integer.size_portrait).toString()))
        } catch (e: Exception) {
            40
        }
    }

    fun getLandscapeSize(): Int {
        return try {
            Integer.parseInt(safeRead("size_landscape", res.getInteger(R.integer.size_landscape).toString()))
        } catch (e: Exception) {
            70
        }
    }

    fun getFontSizeAsSp(): Float {
        val fontSize = safeRead("font_size", res.getInteger(R.integer.font_size).toString())
        val dm: DisplayMetrics = res.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, fontSize.toFloat(), dm)
    }

    fun isPreviewEnabled(): Boolean {
        return read("preview", res.getBoolean(R.bool.preview))
    }

    fun isBorderEnabled(): Boolean {
        return read("borders", res.getBoolean(R.bool.borders))
    }

    fun getCustomSymbolsMain(): String {
        return read("input_symbols_main", res.getString(R.string.input_symbols_main)) ?: res.getString(R.string.input_symbols_main)
    }

    fun getCustomSymbolsMain2(): String {
        return read("input_symbols_main_2", res.getString(R.string.input_symbols_main_2)) ?: res.getString(R.string.input_symbols_main_2)
    }

    fun getCustomSymbolsMainBottom(): String {
        return read("input_symbols_main_bottom", res.getString(R.string.input_symbols_main_bottom)) ?: res.getString(R.string.input_symbols_main_bottom)
    }

    fun getNavBar(): Boolean {
        return read("navbar", res.getBoolean(R.bool.navbar))
    }

    fun getNavBarDark(): Boolean {
        return read("navbar_dark", res.getBoolean(R.bool.navbar_dark))
    }

    fun getLayoutIndex(): Int {
        return Integer.parseInt(safeRead("layout", "0"))
    }

    fun getThemeIndex(): Int {
        return Integer.parseInt(safeRead("theme", "0"))
    }

    fun getCustomTheme(): Boolean {
        return read("custom_theme", res.getBoolean(R.bool.custom_theme))
    }

    fun getPin1(): String {
        return read("pin1", res.getString(R.string.pin1)) ?: res.getString(R.string.pin1)
    }

    fun getPin2(): String {
        return read("pin2", res.getString(R.string.pin2)) ?: res.getString(R.string.pin2)
    }

    fun getPin3(): String {
        return read("pin3", res.getString(R.string.pin3)) ?: res.getString(R.string.pin3)
    }

    fun getPin4(): String {
        return read("pin4", res.getString(R.string.pin4)) ?: res.getString(R.string.pin4)
    }

    fun getPin5(): String {
        return read("pin5", res.getString(R.string.pin5)) ?: res.getString(R.string.pin5)
    }

    fun getPin6(): String {
        return read("pin6", res.getString(R.string.pin6)) ?: res.getString(R.string.pin6)
    }

    fun getPin7(): String {
        return read("pin7", res.getString(R.string.pin7)) ?: res.getString(R.string.pin7)
    }

    fun getNotification(): Boolean {
        return read("notification", res.getBoolean(R.bool.notification))
    }

    fun getTopRowActions(): Boolean {
        return read("top_row_actions", res.getBoolean(R.bool.top_row_actions))
    }

    private fun read(key: String, defaultValue: Boolean): Boolean {
        return preferences.getBoolean(key, defaultValue)
    }

    private fun write(key: String, value: Boolean) {
        val editor = preferences.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    private fun read(key: String, defaultValue: String): String? {
        return preferences.getString(key, defaultValue)
    }

    private fun safeRead(key: String, defaultValue: String): String {
        val s = read(key, defaultValue)
        if (s == null) {
            return "0"
        }
        return s
    }

    private fun write(key: String, value: String) {
        val editor = preferences.edit()
        editor.putString(key, value)
        editor.apply()
    }
}

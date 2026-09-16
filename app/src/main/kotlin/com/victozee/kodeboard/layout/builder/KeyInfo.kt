package com.victozee.kodeboard.layout.builder

import android.graphics.drawable.Drawable

/**
 * contains information on how to build up the real key
 */
class KeyInfo {
    /**
     * key press code sent when pressing the key
     */
    var code: Int = 0

    /**
     * label is shown on the keyboard
     */
    var label: String? = null

    /**
     * size relative to other keys in the same row
     */
    var size: Float = 1.0f

    /**
     * Key can be held to repeat
     */
    var isRepeatable: Boolean = false

    /**
     * This key is a modifier (Shift/Ctrl)
     */
    var isModifier: Boolean = false

    /**
     * When key is pressed output this text
     */
    var outputText: String? = null

    /**
     * When shift modifier is pressed, show this label instead
     */
    var onShiftLabel: String? = null

    /**
     * When control modifier is pressed, show this label instead
     */
    var onCtrlLabel: String? = null

    /**
     * Drawable is shown on the keyboard
     */
    var icon: Drawable? = null
}

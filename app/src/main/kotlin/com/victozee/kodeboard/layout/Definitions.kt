package com.victozee.kodeboard.layout

import android.content.Context
import com.victozee.kodeboard.R
import com.victozee.kodeboard.layout.builder.KeyboardLayoutBuilder

class Definitions(private val context: Context) {

    fun addArrowsRow(keyboard: KeyboardLayoutBuilder) {
        val CODE_ARROW_LEFT = 5000
        val CODE_ARROW_DOWN = 5001
        val CODE_ARROW_UP = 5002
        val CODE_ARROW_RIGHT = 5003
        keyboard.newRow()
            .addKey("Esc", CODE_ESCAPE)
            .addTabKey()
            .addKey(context.getDrawable(R.drawable.ic_keyboard_arrow_left_24dp), CODE_ARROW_LEFT).asRepeatable()
            .addKey(context.getDrawable(R.drawable.ic_keyboard_arrow_down_24dp), CODE_ARROW_DOWN).asRepeatable()
            .addKey(context.getDrawable(R.drawable.ic_keyboard_arrow_up_24dp), CODE_ARROW_UP).asRepeatable()
            .addKey(context.getDrawable(R.drawable.ic_keyboard_arrow_right_24dp), CODE_ARROW_RIGHT).asRepeatable()
            .addKey("SYM", CODE_SYMBOLS).onCtrlShow("CLIP")
    }

    fun addCopyPasteRow(keyboard: KeyboardLayoutBuilder) {
        keyboard.newRow()
            .addKey("Esc", CODE_ESCAPE)
            .addTabKey()
            .addKey(context.getDrawable(R.drawable.ic_select_all_24dp), 53737)
            .addKey(context.getDrawable(R.drawable.ic_cut_24dp), 53738)
            .addKey(context.getDrawable(R.drawable.ic_copy_24dp), 53739)
            .addKey(context.getDrawable(R.drawable.ic_paste_24dp), 53740)
            .addKey("SYM", CODE_SYMBOLS).onCtrlShow("CLIP")
    }

    fun addSymbolRows(keyboard: KeyboardLayoutBuilder) {
        keyboard.newRow()
            .addKey("Home", -18)
            .addKey("End", -19)
            .addKey("Del", -21)
            .addKey("PgUp", -22)
            .addKey("PgDn", -23)
            .newRow()
            .addShiftKey()
            .addKey("F1", -6)
            .addKey("F2", -7)
            .addKey("F3", -8)
            .addKey("F4", -9)
            .addKey("F5", -10)
            .addKey("F6", -11)
            .addKey("F7", -12)
            .addBackspaceKey()
            .newRow()
            .addKey("Ctrl", 17).asModifier().onCtrlShow("CTRL")
            .addKey("F8", -13)
            .addKey("F9", -14)
            .addKey("F10", -15)
            .addKey(context.getDrawable(R.drawable.ic_space_bar_24dp), 32).withSize(2f)
            .addKey("F11", -16)
            .addKey("F12", -17)
            .addEnterKey()
    }

    fun addClipboardActions(keyboard: KeyboardLayoutBuilder) {
        keyboard.newRow()
            .addKey(context.getDrawable(R.drawable.ic_select_all_24dp), 53737)
            .addKey(context.getDrawable(R.drawable.ic_cut_24dp), 53738)
            .addKey(context.getDrawable(R.drawable.ic_copy_24dp), 53739)
            .addKey(context.getDrawable(R.drawable.ic_paste_24dp), 53740)
            .addKey(context.getDrawable(R.drawable.ic_undo_24dp), 53741)
            .addKey(context.getDrawable(R.drawable.ic_redo_24dp), 53742)
    }

    fun addCustomSpaceRow(keyboard: KeyboardLayoutBuilder, symbols: String) {
        val chars = symbols.toCharArray()
        keyboard.newRow().addKey("Ctrl", 17).asModifier().onCtrlShow("CTRL")
        for (i in 0 until (chars.size + 1) / 2) {
            if (chars.isEmpty()) break
            keyboard.addKey(chars[i]).withSize(.7f)
        }
        keyboard.addKey(context.getDrawable(R.drawable.ic_space_bar_24dp), 32).withSize(2f)
        for (i in (chars.size + 1) / 2 until chars.size) {
            keyboard.addKey(chars[i]).withSize(.7f)
        }
        keyboard.addEnterKey()
    }

    fun addGboardBottomRow(keyboard: KeyboardLayoutBuilder) {
        // Exact GBoard bottom: ?123 , SPACE(blank) . ENTER - space dominates
        keyboard.newRow()
            .addKey("?123", -1).onCtrlShow("CLIP").withSize(1.5f)
            .addKey(",", ','.code).withSize(1f)
            .addKey("", 32).withSize(5f)
            .addKey(".", '.'.code).withSize(1f)
            .addKey(context.getDrawable(R.drawable.ic_keyboard_return_24dp), -4).withSize(1.8f)
    }

    companion object {
        private const val CODE_ESCAPE = -2
        private const val CODE_SYMBOLS = -1

        @JvmStatic
        fun addCustomRow(keyboard: KeyboardLayoutBuilder, symbols: String) {
            keyboard.newRow()
            val chars = symbols.toCharArray()
            for (aChar in chars) keyboard.addKey(aChar)
        }

        @JvmStatic
        fun addGboardNumbersRow(keyboard: KeyboardLayoutBuilder) {
            keyboard.newRow()
                .addKey('1').withSize(1f)
                .addKey('2').withSize(1f)
                .addKey('3').withSize(1f)
                .addKey('4').withSize(1f)
                .addKey('5').withSize(1f)
                .addKey('6').withSize(1f)
                .addKey('7').withSize(1f)
                .addKey('8').withSize(1f)
                .addKey('9').withSize(1f)
                .addKey('0').withSize(1f)
        }

        @JvmStatic
        fun addDevSpecialPage(keyboard: KeyboardLayoutBuilder, context: Context) {
            // Page 2 - shown on swipe left: only special / dev keys, no letters/numbers
            // Row 1: Esc Tab Home End PgUp Del  (navigation)
            keyboard.newRow()
                .addKey("Esc", -2).withSize(1f)
                .addKey("Tab", 9).withSize(1f)
                .addKey("Home", -18).withSize(1f)
                .addKey("End", -19).withSize(1f)
                .addKey("PgUp", -22).withSize(1f)
                .addKey(context.getDrawable(R.drawable.ic_backspace_24dp), -5).asRepeatable().withSize(1f)
            // Row 2: Ctrl Alt arrows PgDn
            keyboard.newRow()
                .addKey("Ctrl", 17).asModifier().onCtrlShow("CTRL").withSize(1f)
                .addKey("Alt", -30).asModifier().withSize(1f)
                .addKey(context.getDrawable(R.drawable.ic_keyboard_arrow_left_24dp), 5000).asRepeatable().withSize(1f)
                .addKey(context.getDrawable(R.drawable.ic_keyboard_arrow_down_24dp), 5001).asRepeatable().withSize(1f)
                .addKey(context.getDrawable(R.drawable.ic_keyboard_arrow_up_24dp), 5002).asRepeatable().withSize(1f)
                .addKey(context.getDrawable(R.drawable.ic_keyboard_arrow_right_24dp), 5003).asRepeatable().withSize(1f)
                .addKey("PgDn", -23).withSize(1f)
            // Row 3: Shift F1-F6
            keyboard.newRow()
                .addKey("Shft", 16).asModifier().onShiftShow("SHFT").withSize(1.2f)
                .addKey("F1", -6).withSize(1f)
                .addKey("F2", -7).withSize(1f)
                .addKey("F3", -8).withSize(1f)
                .addKey("F4", -9).withSize(1f)
                .addKey("F5", -10).withSize(1f)
                .addKey("F6", -11).withSize(1f)
                .addKey("Del", -21).withSize(1f)
            // Row 4: F7-F12 + Enter
            keyboard.newRow()
                .addKey("Ctrl", 17).asModifier().onCtrlShow("CTRL").withSize(1.2f)
                .addKey("F7", -12).withSize(1f)
                .addKey("F8", -13).withSize(1f)
                .addKey("F9", -14).withSize(1f)
                .addKey("F10", -15).withSize(1f)
                .addKey("F11", -16).withSize(1f)
                .addKey("F12", -17).withSize(1f)
                .addKey(context.getDrawable(R.drawable.ic_keyboard_return_24dp), -4).withSize(1.2f)
            // Row 5: bottom - keep space/enter for usability, but only specials
            keyboard.newRow()
                .addKey("SYM", -1).onCtrlShow("CLIP").withSize(1f)
                .addKey(",", ','.code).withSize(1f)
                .addKey(context.getDrawable(R.drawable.ic_space_bar_24dp), 32).withSize(3f)
                .addKey(".", '.'.code).withSize(1f)
                .addKey(context.getDrawable(R.drawable.ic_keyboard_return_24dp), -4).withSize(1f)
        }

        @JvmStatic
        fun addQwertyRows(keyboard: KeyboardLayoutBuilder) {
            keyboard.newRow()
                .addKey('q').onShiftUppercase()
                .addKey('w').onShiftUppercase()
                .addKey('e').onShiftUppercase()
                .addKey('r').onShiftUppercase()
                .addKey('t').onShiftUppercase()
                .addKey('y').onShiftUppercase()
                .addKey('u').onShiftUppercase()
                .addKey('i').onShiftUppercase()
                .addKey('o').onShiftUppercase()
                .addKey('p').onShiftUppercase()
                .newRow()
                .addKey('a').onShiftUppercase().withSize(1.5f)
                .addKey('s').onShiftUppercase()
                .addKey('d').onShiftUppercase()
                .addKey('f').onShiftUppercase()
                .addKey('g').onShiftUppercase()
                .addKey('h').onShiftUppercase()
                .addKey('j').onShiftUppercase()
                .addKey('k').onShiftUppercase()
                .addKey('l').onShiftUppercase().withSize(1.5f)
                .newRow()
                .addShiftKey()
                .addKey('z').onShiftUppercase()
                .addKey('x').onShiftUppercase()
                .addKey('c').onShiftUppercase()
                .addKey('v').onShiftUppercase()
                .addKey('b').onShiftUppercase()
                .addKey('n').onShiftUppercase()
                .addKey('m').onShiftUppercase()
                .addBackspaceKey()
        }

        @JvmStatic
        fun addQwertzRows(keyboard: KeyboardLayoutBuilder) {
            keyboard.newRow()
                .addKey('q').onShiftUppercase()
                .addKey('w').onShiftUppercase()
                .addKey('e').onShiftUppercase()
                .addKey('r').onShiftUppercase()
                .addKey('t').onShiftUppercase()
                .addKey('z').onShiftUppercase()
                .addKey('u').onShiftUppercase()
                .addKey('i').onShiftUppercase()
                .addKey('o').onShiftUppercase()
                .addKey('p').onShiftUppercase()
                .newRow()
                .addKey('a').onShiftUppercase().withSize(1.5f)
                .addKey('s').onShiftUppercase()
                .addKey('d').onShiftUppercase()
                .addKey('f').onShiftUppercase()
                .addKey('g').onShiftUppercase()
                .addKey('h').onShiftUppercase()
                .addKey('j').onShiftUppercase()
                .addKey('k').onShiftUppercase()
                .addKey('l').onShiftUppercase().withSize(1.5f)
                .newRow()
                .addShiftKey()
                .addKey('y').onShiftUppercase()
                .addKey('x').onShiftUppercase()
                .addKey('c').onShiftUppercase()
                .addKey('v').onShiftUppercase()
                .addKey('b').onShiftUppercase()
                .addKey('n').onShiftUppercase()
                .addKey('m').onShiftUppercase()
                .addBackspaceKey()
        }

        @JvmStatic
        fun addAzertyRows(keyboard: KeyboardLayoutBuilder) {
            keyboard.newRow()
                .addKey('a').onShiftUppercase()
                .addKey('z').onShiftUppercase()
                .addKey('e').onShiftUppercase()
                .addKey('r').onShiftUppercase()
                .addKey('t').onShiftUppercase()
                .addKey('y').onShiftUppercase()
                .addKey('u').onShiftUppercase()
                .addKey('i').onShiftUppercase()
                .addKey('o').onShiftUppercase()
                .addKey('p').onShiftUppercase()
                .newRow()
                .addKey('q').onShiftUppercase()
                .addKey('s').onShiftUppercase()
                .addKey('d').onShiftUppercase()
                .addKey('f').onShiftUppercase()
                .addKey('g').onShiftUppercase()
                .addKey('h').onShiftUppercase()
                .addKey('j').onShiftUppercase()
                .addKey('k').onShiftUppercase()
                .addKey('l').onShiftUppercase()
                .addKey('m').onShiftUppercase()
                .addBackspaceKey()
                .newRow()
                .addShiftKey()
                .addKey('w').onShiftUppercase()
                .addKey('x').onShiftUppercase()
                .addKey('c').onShiftUppercase()
                .addKey('v').onShiftUppercase()
                .addKey('b').onShiftUppercase()
                .addKey('n').onShiftUppercase()
                .addKey('!').withSize(.8f)
                .addKey('?').withSize(.8f)
                .addTabKey()
        }

        @JvmStatic
        fun addDvorakRows(keyboard: KeyboardLayoutBuilder) {
            keyboard.newRow()
                .addKey('!')
                .addKey('p').onShiftUppercase()
                .addKey('y').onShiftUppercase()
                .addKey('f').onShiftUppercase()
                .addKey('g').onShiftUppercase()
                .addKey('c').onShiftUppercase()
                .addKey('r').onShiftUppercase()
                .addKey('l').onShiftUppercase()
                .addEnterKey()
                .newRow()
                .addKey('a').onShiftUppercase()
                .addKey('o').onShiftUppercase()
                .addKey('e').onShiftUppercase()
                .addKey('u').onShiftUppercase()
                .addKey('i').onShiftUppercase()
                .addKey('d').onShiftUppercase()
                .addKey('h').onShiftUppercase()
                .addKey('t').onShiftUppercase()
                .addKey('n').onShiftUppercase()
                .addKey('s').onShiftUppercase()
                .addBackspaceKey()
                .newRow()
                .addShiftKey()
                .addKey('q').onShiftUppercase()
                .addKey('j').onShiftUppercase()
                .addKey('k').onShiftUppercase()
                .addKey('x').onShiftUppercase()
                .addKey('b').onShiftUppercase()
                .addKey('m').onShiftUppercase()
                .addKey('w').onShiftUppercase()
                .addKey('v').onShiftUppercase()
                .addKey('z').onShiftUppercase()
        }
    }
}

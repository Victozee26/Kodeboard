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

    fun addClipboardActions(keyboard: KeyboardLayoutBuilder) {
        keyboard.newRow()
            .addKey(context.getDrawable(R.drawable.ic_select_all_24dp), 53737)
            .addKey(context.getDrawable(R.drawable.ic_cut_24dp), 53738)
            .addKey(context.getDrawable(R.drawable.ic_copy_24dp), 53739)
            .addKey(context.getDrawable(R.drawable.ic_paste_24dp), 53740)
            .addKey(context.getDrawable(R.drawable.ic_undo_24dp), 53741)
            .addKey(context.getDrawable(R.drawable.ic_redo_24dp), 53742)
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
                .addKey('1').onFnShow("F1").withSize(1f)
                .addKey('2').onFnShow("F2").withSize(1f)
                .addKey('3').onFnShow("F3").withSize(1f)
                .addKey('4').onFnShow("F4").withSize(1f)
                .addKey('5').onFnShow("F5").withSize(1f)
                .addKey('6').onFnShow("F6").withSize(1f)
                .addKey('7').onFnShow("F7").withSize(1f)
                .addKey('8').onFnShow("F8").withSize(1f)
                .addKey('9').onFnShow("F9").withSize(1f)
                .addKey('0').onFnShow("F10").withSize(1f)
        }

        @JvmStatic
        fun addDevSpecialPage(keyboard: KeyboardLayoutBuilder, context: Context) {
            // Page 2 - shown on swipe left: only special / dev keys, no letters/numbers.
            // 4 rows (taller keys than the 5-row clean page) grouped by function,
            // no duplicates: one Esc/Tab/Ctrl/Alt/Shift, no clean bottom row, one Enter.
            // F1-F10 come from the Fn key: arm F, swipe right, tap a digit.
            // Row 1: Esc Tab Home End (wide, no more clipped labels)
            keyboard.newRow()
                .addKey("Esc", -2).withSize(1f)
                .addKey("Tab", 9).withSize(1f)
                .addKey("Home", -18).withSize(1f)
                .addKey("End", -19).withSize(1f)
            // Row 2: PgUp PgDn Del backspace (wide)
            keyboard.newRow()
                .addKey("PgUp", -22).withSize(1f)
                .addKey("PgDn", -23).withSize(1f)
                .addKey("Del", -21).withSize(1f)
                .addBackspaceKey()
            // Row 3: modifiers + Fn + Insert + Enter
            keyboard.newRow()
                .addKey("Ctrl", 17).asModifier().onCtrlShow("CTRL").withSize(1f)
                .addKey("Alt", -30).asModifier().withSize(1f)
                .addShiftKey()
                .addKey("F", -31).asModifier().withSize(1f)
                .addKey("Ins", -20).withSize(1f)
                .addEnterKey()
            // Row 4: arrow cluster, full-width cursor keys
            keyboard.newRow()
                .addKey(context.getDrawable(R.drawable.ic_keyboard_arrow_left_24dp), 5000).asRepeatable().withSize(1f)
                .addKey(context.getDrawable(R.drawable.ic_keyboard_arrow_up_24dp), 5002).asRepeatable().withSize(1f)
                .addKey(context.getDrawable(R.drawable.ic_keyboard_arrow_down_24dp), 5001).asRepeatable().withSize(1f)
                .addKey(context.getDrawable(R.drawable.ic_keyboard_arrow_right_24dp), 5003).asRepeatable().withSize(1f)
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

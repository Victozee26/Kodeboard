package com.victozee.kodeboard.layout.builder

import android.content.Context
import android.graphics.drawable.Drawable
import com.victozee.kodeboard.R
import com.victozee.kodeboard.layout.Box
import com.victozee.kodeboard.layout.Key
import java.util.ArrayList

class KeyboardLayoutBuilder(private val context: Context) {

    private var box: Box? = null
    private val rows = ArrayList<KeyboardLayoutRowBuilder>()
    private var currentRow: KeyboardLayoutRowBuilder? = null
    private var currentKey: KeyInfo? = null
    private var rowGap: Float = 0f
    private var keyGap: Float = 0f
    private var padding: Float = 0f

    fun newRow(): KeyboardLayoutBuilder {
        currentKey = null
        currentRow = KeyboardLayoutRowBuilder()
        rows.add(currentRow!!)
        return this
    }

    fun setBox(box: Box): KeyboardLayoutBuilder {
        this.box = box
        return this
    }

    fun setRowGap(size: Float): KeyboardLayoutBuilder {
        this.rowGap = size
        return this
    }

    fun setKeyGap(size: Float): KeyboardLayoutBuilder {
        this.keyGap = size
        return this
    }

    fun setPadding(size: Float): KeyboardLayoutBuilder {
        this.padding = size
        return this
    }

    fun addKey(label: String, code: Int): KeyboardLayoutBuilder {
        if (currentRow == null) {
            newRow()
        }
        currentKey = KeyInfo().apply {
            this.label = label
            this.code = code
            size = 1.0f
            isRepeatable = false
        }
        currentRow!!.addKey(currentKey!!)
        return this
    }

    fun addKey(icon: Drawable?, code: Int): KeyboardLayoutBuilder {
        return addKey("", code).withIcon(icon)
    }

    fun addKey(key: Char): KeyboardLayoutBuilder {
        return addKey("" + key, key.code)
    }

    fun addKey(relativeSize: Float): KeyboardLayoutBuilder {
        return addKey('?').withSize(relativeSize)
    }

    fun addKey(label: String): KeyboardLayoutBuilder {
        return addKey(label, 0).withOutputText(label)
    }

    fun asRepeatable(repeat: Boolean): KeyboardLayoutBuilder {
        currentKey!!.isRepeatable = repeat
        return this
    }

    fun asRepeatable(): KeyboardLayoutBuilder {
        return asRepeatable(true)
    }

    fun withSize(size: Float): KeyboardLayoutBuilder {
        currentKey!!.size = size
        return this
    }

    fun withCode(code: Int): KeyboardLayoutBuilder {
        currentKey!!.code = code
        return this
    }

    fun withIcon(icon: Drawable?): KeyboardLayoutBuilder {
        currentKey!!.icon = icon
        return this
    }

    @Throws(KeyboardLayoutException::class)
    fun build(): ArrayList<Key> {
        val nonNullBox = box!!
        val availableWidth = nonNullBox.width - 2 * padding
        val availableHeight = nonNullBox.height - (rows.size - 1) * rowGap - 2 * padding
        val cursorX = nonNullBox.x + padding
        var cursorY = nonNullBox.y + padding
        val result = ArrayList<Key>()
        for (rowBuilder in rows) {
            rowBuilder.setGap(keyGap)
            val width = availableWidth
            val height = availableHeight / rows.size
            val rowBox = Box.create(cursorX, cursorY, width, height)
            rowBuilder.setBox(rowBox)
            cursorY += rowBox.height
            cursorY += rowGap
            result.addAll(rowBuilder.build())
        }
        return result
    }

    fun asModifier(isModifier: Boolean): KeyboardLayoutBuilder {
        currentKey!!.isModifier = isModifier
        return this
    }

    fun asModifier(): KeyboardLayoutBuilder {
        return asModifier(true)
    }

    fun withOutputText(s: String): KeyboardLayoutBuilder {
        currentKey!!.outputText = s
        return this
    }

    fun onShiftShow(label: String): KeyboardLayoutBuilder {
        currentKey!!.onShiftLabel = label
        return this
    }

    fun onCtrlShow(label: String): KeyboardLayoutBuilder {
        currentKey!!.onCtrlLabel = label
        return this
    }

    fun onShiftUppercase(): KeyboardLayoutBuilder {
        return onShiftShow(currentKey!!.label!!.uppercase())
    }

    fun addTabKey(): KeyboardLayoutBuilder {
        return addKey("Tab", 9)
    }

    fun addShiftKey(): KeyboardLayoutBuilder {
        return addKey("Shft", 16).asModifier()
            .onShiftShow("SHFT").withSize(1.5f)
    }

    fun addBackspaceKey(): KeyboardLayoutBuilder {
        return addKey(context.getDrawable(R.drawable.ic_backspace_24dp), -5).asRepeatable()
    }

    fun addEnterKey(): KeyboardLayoutBuilder {
        return addKey(context.getDrawable(R.drawable.ic_keyboard_return_24dp), -4).withSize(1.5f)
    }
}

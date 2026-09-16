package com.victozee.kodeboard.layout.builder

import com.victozee.kodeboard.layout.Box
import com.victozee.kodeboard.layout.Key
import java.util.ArrayList

class KeyboardLayoutRowBuilder {

    private var box: Box? = null
    private val keys = mutableListOf<KeyInfo>()
    private var gap: Float = 0f

    @Throws(KeyboardLayoutException::class)
    fun build(): ArrayList<Key> {
        checkAndUpdateDefaults()
        if (keys.isEmpty()) {
            throw KeyboardLayoutException("Row cannot be built without any keys")
        }
        val nonNullBox = box!!
        val availableWidth = nonNullBox.width - gap * (keys.size - 1)
        val availableHeight = nonNullBox.height
        if (availableWidth <= 0) {
            throw KeyboardLayoutException("Not enough space to fit keys in row")
        }
        var totalRequestedSize = 0f
        for (info in keys) {
            totalRequestedSize += info.size
        }
        var cursorX = nonNullBox.x
        val cursorY = nonNullBox.y
        val result = ArrayList<Key>()
        for (info in keys) {
            val width = availableWidth / totalRequestedSize * info.size
            val height = availableHeight
            val keyBox = Box.create(cursorX, cursorY, width, height)
            cursorX += keyBox.width + gap
            val key = buildKeyFromBlueprint(info, keyBox)
            result.add(key)
        }
        return result
    }

    fun addKey(key: KeyInfo): KeyboardLayoutRowBuilder {
        keys.add(key)
        return this
    }

    fun setBox(size: Box): KeyboardLayoutRowBuilder {
        this.box = size
        return this
    }

    fun setGap(size: Float): KeyboardLayoutRowBuilder {
        this.gap = size
        return this
    }

    private fun checkAndUpdateDefaults() {
        if (box == null) {
            box = Box.create(0f, 0f, 0f, 0f)
        }
    }

    companion object {
        private fun buildKeyFromBlueprint(info: KeyInfo, box: Box): Key =
            Key().apply {
                this.box = box
                this.info = info
            }
    }
}

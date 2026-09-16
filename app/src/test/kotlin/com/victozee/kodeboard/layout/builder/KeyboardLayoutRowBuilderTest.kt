package com.victozee.kodeboard.layout.builder

import com.victozee.kodeboard.layout.Box
import com.victozee.kodeboard.layout.Key
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.ArrayList

class KeyboardLayoutRowBuilderTest {

    private val defaultBox = Box.create(0, 0, 100, 10)

    @Test
    fun setBox_doesNotThrow() {
        builder().setBox(Box.create(0, 600, 320, 32))
    }

    @Test
    fun addKey_canBeCalledMultipleTimes() {
        builder().setBox(defaultBox).addKey(KeyInfo()).addKey(KeyInfo()).addKey(KeyInfo())
    }

    @Test
    @Throws(KeyboardLayoutException::class)
    fun build_returnsCorrectNumberOfKeys() {
        assertEquals(2, builder().setBox(defaultBox).addKey(KeyInfo()).addKey(KeyInfo()).build().size)
    }

    @Test
    @Throws(KeyboardLayoutException::class)
    fun build_proportinallySplitsAvaiableWidth() {
        val result = buildTwoKeysRow()
        assertEquals(result[0].box.width, 25.0, 0.01)
        assertEquals(result[1].box.width, 75.0, 0.01)
    }

    @Test
    @Throws(KeyboardLayoutException::class)
    fun build_usesAllAvaiableHeight() {
        val result = buildTwoKeysRow()
        assertEquals(result[0].box.height, 10.0, 0.01)
    }

    @Test
    @Throws(KeyboardLayoutException::class)
    fun build_addsGapBetweenKeys() {
        val result = buildTwoKeysRow(20f)
        assertEquals(result[0].box.width, 20.0, 0.01)
        assertEquals(result[1].box.width, 60.0, 0.01)
    }

    private fun builder(): KeyboardLayoutRowBuilder {
        return KeyboardLayoutRowBuilder()
    }

    @Throws(KeyboardLayoutException::class)
    private fun buildTwoKeysRow(): ArrayList<Key> {
        return buildTwoKeysRow(0f)
    }

    @Throws(KeyboardLayoutException::class)
    private fun buildTwoKeysRow(gap: Float): ArrayList<Key> {
        val keyA = KeyInfo()
        keyA.size = 1
        val keyB = KeyInfo()
        keyB.size = 3
        return builder().setBox(defaultBox)
            .setGap(gap)
            .addKey(keyA).addKey(keyB).build()
    }
}

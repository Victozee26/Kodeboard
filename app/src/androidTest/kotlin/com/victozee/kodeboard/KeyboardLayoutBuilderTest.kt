package com.victozee.kodeboard

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.victozee.kodeboard.layout.Box
import com.victozee.kodeboard.layout.Key
import com.victozee.kodeboard.layout.builder.KeyboardLayoutBuilder
import com.victozee.kodeboard.layout.builder.KeyboardLayoutException
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.ArrayList

class KeyboardLayoutBuilderTest {

    @Test
    @Throws(KeyboardLayoutException::class)
    fun build_returnsCorrectNumberOfKeys() {
        val keyboard: ArrayList<Key> = builder().setBox(Box.create(0, 0, 100, 100))
            .newRow().addKey(1).addKey(1)
            .newRow().addKey(1).addKey(1).build()
        assertEquals(4, keyboard.size)
    }

    private fun builder(): KeyboardLayoutBuilder {
        val appContext = ApplicationProvider.getApplicationContext<Context>()
        return KeyboardLayoutBuilder(appContext)
    }
}

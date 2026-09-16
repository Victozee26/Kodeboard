package com.victozee.kodeboard

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.victozee.kodeboard.layout.Box
import com.victozee.kodeboard.layout.Definitions
import com.victozee.kodeboard.layout.Key
import com.victozee.kodeboard.layout.builder.KeyboardLayoutBuilder
import com.victozee.kodeboard.layout.builder.KeyboardLayoutException
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import org.junit.Test

class DefinitionsTest {

    @Test
    @Throws(KeyboardLayoutException::class)
    fun addArrowsRow_producesValidResult() {
        val appContext = ApplicationProvider.getApplicationContext<Context>()
        val definitions = Definitions(appContext)
        val builder = builder()
        definitions.addArrowsRow(builder)
        validate(builder)
    }

    @Test
    @Throws(KeyboardLayoutException::class)
    fun addCopyPasteRow() {
        val appContext = ApplicationProvider.getApplicationContext<Context>()
        val definitions = Definitions(appContext)
        val builder = builder()
        definitions.addCopyPasteRow(builder)
        validate(builder)
    }

    @Test
    @Throws(KeyboardLayoutException::class)
    fun addQwertyRows() {
        val appContext = ApplicationProvider.getApplicationContext<Context>()
        val definitions = Definitions(appContext)
        val builder = builder()
        definitions.addQwertyRows(builder)
        validate(builder)
    }

    @Test
    @Throws(KeyboardLayoutException::class)
    fun addQwertzRows() {
        val appContext = ApplicationProvider.getApplicationContext<Context>()
        val definitions = Definitions(appContext)
        val builder = builder()
        definitions.addQwertzRows(builder)
        validate(builder)
    }

    @Test
    @Throws(KeyboardLayoutException::class)
    fun addAzertyRows() {
        val appContext = ApplicationProvider.getApplicationContext<Context>()
        val definitions = Definitions(appContext)
        val builder = builder()
        definitions.addAzertyRows(builder)
        validate(builder)
    }

    @Test
    @Throws(KeyboardLayoutException::class)
    fun addClipboardRow() {
        val appContext = ApplicationProvider.getApplicationContext<Context>()
        val definitions = Definitions(appContext)
        val builder = builder()
        definitions.addClipboardActions(builder)
        validate(builder)
    }

    @Throws(KeyboardLayoutException::class)
    private fun validate(builder: KeyboardLayoutBuilder) {
        for (key in builder.build()) {
            assertNotNull(key.info)
            assertNotNull(key.box)
            // must have either code or outputText set
            assertTrue(key.info.code != 0 || key.info.outputText != null)
        }
    }

    private fun builder(): KeyboardLayoutBuilder {
        val appContext = ApplicationProvider.getApplicationContext<Context>()
        return KeyboardLayoutBuilder(appContext).setBox(Box.create(0, 0, 1, 1))
    }
}

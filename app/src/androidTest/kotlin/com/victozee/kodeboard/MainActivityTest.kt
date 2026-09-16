package com.victozee.kodeboard

import android.app.Activity
import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import junit.framework.TestCase.assertNotNull
import org.junit.Test

class MainActivityTest {

    @Test
    @Throws(Exception::class)
    fun mainActivity_canBeLaunched() {
        val appContext = ApplicationProvider.getApplicationContext<Context>()

        val mInstrumentation = InstrumentationRegistry.getInstrumentation()
        val monitor: Instrumentation.ActivityMonitor =
            mInstrumentation.addMonitor(MainActivity::class.java.name, null, false)

        val intent = Intent(Intent.ACTION_MAIN)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.setClassName(mInstrumentation.targetContext, MainActivity::class.java.name)
        mInstrumentation.startActivitySync(intent)

        val currentActivity: Activity? =
            InstrumentationRegistry.getInstrumentation().waitForMonitor(monitor)
        assertNotNull(currentActivity)

        mInstrumentation.removeMonitor(monitor)
    }
}

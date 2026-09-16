package com.victozee.kodeboard

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.graphics.Color
import android.inputmethodservice.InputMethodService
import android.inputmethodservice.KeyboardView
import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Vibrator
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputMethodManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.RemoteInput
import androidx.core.graphics.ColorUtils
import com.victozee.kodeboard.layout.Box
import com.victozee.kodeboard.layout.Definitions
import com.victozee.kodeboard.layout.Key
import com.victozee.kodeboard.layout.builder.KeyboardLayoutBuilder
import com.victozee.kodeboard.layout.builder.KeyboardLayoutException
import com.victozee.kodeboard.layout.ui.KeyboardLayoutView
import com.victozee.kodeboard.layout.ui.KeyboardUiFactory
import com.victozee.kodeboard.theme.ThemeDefinitions
import com.victozee.kodeboard.theme.ThemeInfo
import java.util.Objects
import java.util.Timer
import java.util.TimerTask
import android.content.ClipDescription.MIMETYPE_TEXT_PLAIN
import android.view.inputmethod.InputMethodManager.HIDE_IMPLICIT_ONLY

class CodeBoardIME : InputMethodService(), KeyboardView.OnKeyboardActionListener {

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "Codeboard"
        private const val NOTIFICATION_ONGOING_ID = 1001
    }

    var sEditorInfo: EditorInfo? = null
    private var vibratorOn = false
    private var vibrateLength = 0
    private var soundOn = false
    private var shiftLock = false
    private var ctrlLock = false
    private var shift = false
    private var ctrl = false
    private var mKeyboardState: Int = R.integer.keyboard_normal
    private var timerLongPress: Timer? = null
    private var mKeyboardUiFactory: KeyboardUiFactory? = null
    private var mCurrentKeyboardLayoutView: KeyboardLayoutView? = null
    private var longPressedSpaceButton = false
    var mToken: IBinder? = null
    private var mNotificationReceiver: NotificationReceiver? = null

    override fun onKey(primaryCode: Int, keyCodes: IntArray?) {
        //NOTE: Long press goes second, this is onDown
        val ic = currentInputConnection ?: return
        var code = primaryCode.toChar()
        Log.d(javaClass.simpleName, "onKey: $primaryCode")

        when (primaryCode) {
            //First handle cases that  don't use shift/ctrl meta modifiers
            53737 -> ic.performContextMenuAction(android.R.id.selectAll)
            53738 -> ic.performContextMenuAction(android.R.id.cut)
            53739 -> ic.performContextMenuAction(android.R.id.copy)
            53740 -> ic.performContextMenuAction(android.R.id.paste)
            53741 -> ic.performContextMenuAction(android.R.id.undo)
            53742 -> ic.performContextMenuAction(android.R.id.redo)
            -1 -> {
                //SYM
                if (mKeyboardState == R.integer.keyboard_normal && !ctrl) {
                    mKeyboardState = R.integer.keyboard_sym
                } else if (ctrl) {
                    mKeyboardState = R.integer.keyboard_clipboard
                } else {
                    mKeyboardState = R.integer.keyboard_normal
                }
                // regenerate view
                //Simple remove shift
                if (shift) {
                    shift = false
                    shiftLock = false
                    ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_SHIFT_LEFT))
                }
                if (ctrl) {
                    ctrl = false
                    ctrlLock = false
                    ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_CTRL_LEFT))
                }
                setInputView(onCreateInputView())
                controlKeyUpdateView()
                shiftKeyUpdateView()
            }
            17 -> {
                //KEYCODE_CTRL_LEFT:
                // emulates a press down of the ctrl key
                if (!ctrlLock && !ctrl) {
                    //Simple ctrl to true
                    ctrl = true
                    ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_CTRL_LEFT))
                } else if (!ctrlLock && ctrl) {
                    //Simple remove ctrl
                    ctrl = false
                    ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_CTRL_LEFT))
                }
                //else if (ctrl && ctrlLock) {
                //Stay ctrled if previously ctrled
                //}
                controlKeyUpdateView()
            }
            16 -> {
                //KEYCODE_SHIFT_LEFT
                // emulates press of shift key - this helps for selection with arrow keys
                if (!shiftLock && !shift) {
                    //Simple shift to true
                    shift = true
                    ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_SHIFT_LEFT))
                } else if (!shiftLock && shift) {
                    //Simple remove shift
                    shift = false
                    ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_SHIFT_LEFT))
                }
                //else if (shift && shiftLock) {
                //Stay shifted if previously shifted
                //}
                shiftKeyUpdateView()
            }
            else -> {
                var meta = 0
                if (shift) {
                    meta = KeyEvent.META_SHIFT_ON
                    code = Character.toUpperCase(code)
                    if (!shiftLock) {
                        shift = false
                        ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_SHIFT_LEFT))
                        shiftKeyUpdateView()
                    }
                }
                if (ctrl) {
                    meta = meta or KeyEvent.META_CTRL_ON
                    if (!ctrlLock) {
                        ctrl = false
                        ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_CTRL_LEFT))
                        controlKeyUpdateView()
                    }
                }
                //Now shift/ctrl metadata is set
                //Convert primaryCode to KeyEvent:
                //primaryCode is the char value, it doesn't correspond to the KeyEvent that we want to press
                var ke = 0
                when (primaryCode) {
                    9 -> ke = KeyEvent.KEYCODE_TAB
                    -2 -> ke = KeyEvent.KEYCODE_ESCAPE
                    32 -> ke = KeyEvent.KEYCODE_SPACE
                    -5 -> ke = KeyEvent.KEYCODE_DEL
                    -4 -> ke = KeyEvent.KEYCODE_ENTER
                    -6 -> ke = KeyEvent.KEYCODE_F1
                    -7 -> ke = KeyEvent.KEYCODE_F2
                    -8 -> ke = KeyEvent.KEYCODE_F3
                    -9 -> ke = KeyEvent.KEYCODE_F4
                    -10 -> ke = KeyEvent.KEYCODE_F5
                    -11 -> ke = KeyEvent.KEYCODE_F6
                    -12 -> ke = KeyEvent.KEYCODE_F7
                    -13 -> ke = KeyEvent.KEYCODE_F8
                    -14 -> ke = KeyEvent.KEYCODE_F9
                    -15 -> ke = KeyEvent.KEYCODE_F10
                    -16 -> ke = KeyEvent.KEYCODE_F11
                    -17 -> ke = KeyEvent.KEYCODE_F12
                    -18 -> ke = KeyEvent.KEYCODE_MOVE_HOME
                    -19 -> ke = KeyEvent.KEYCODE_MOVE_END
                    -20 -> ke = KeyEvent.KEYCODE_INSERT
                    -21 -> ke = KeyEvent.KEYCODE_FORWARD_DEL
                    -22 -> ke = KeyEvent.KEYCODE_PAGE_UP
                    -23 -> ke = KeyEvent.KEYCODE_PAGE_DOWN

                    //These are like a directional joystick - can jump outside the inputConnection
                    5000 -> ke = KeyEvent.KEYCODE_DPAD_LEFT
                    5001 -> ke = KeyEvent.KEYCODE_DPAD_DOWN
                    5002 -> ke = KeyEvent.KEYCODE_DPAD_UP
                    5003 -> ke = KeyEvent.KEYCODE_DPAD_RIGHT
                    else -> {
                        //(t key) code 116-> ke 48
                        if (Character.isLetter(code)) {
                            ke = KeyEvent.keyCodeFromString("KEYCODE_" + Character.toUpperCase(code))
                        }
                    }
                }
                if (ke != 0) {
                    Log.d(javaClass.simpleName, "onKey: keyEvent $ke")

                    /*
                     *   The if statement was added in order to prevent the space button
                     *   from having an action down event attached to it.
                     *   Reason being that we first want to check
                     *   whether the space button has been long pressed or not
                     *   and afterwards produce the right output to the screen.
                     *   TODO: Investigate whether KeyEvent.ACTION_UP is still required.
                     */
                    if (primaryCode != 32) {
                        ic.sendKeyEvent(KeyEvent(0, 0, KeyEvent.ACTION_DOWN, ke, 0, meta))
                    }

                    ic.sendKeyEvent(KeyEvent(0, 0, KeyEvent.ACTION_UP, ke, 0, meta))
                } else {
                    //All non-letter characters are handled here
                    // This doesn't use modifiers.
                    // For most users, this usage makes sense.
                    //eg. (0 key) code 48 -> ke 7
                    // If we handled '0' with a keyEvent, shift+0 would result in ')'
                    Log.i(javaClass.simpleName, "onKey: committext " + code.toString())
                    ic.commitText(code.toString(), 1)
                }
            }
        }
    }

    override fun onPress(primaryCode: Int) {
        if (soundOn) {
            val keypressSoundPlayer = MediaPlayer.create(this, R.raw.keypress_sound)
            keypressSoundPlayer?.start()
            keypressSoundPlayer?.setOnCompletionListener { mp -> mp.release() }
        }
        if (vibratorOn) {
            val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            vibrator?.vibrate(vibrateLength.toLong())
        }

        clearLongPressTimer()
        timerLongPress = Timer()
        timerLongPress!!.schedule(object : TimerTask() {
            override fun run() {
                try {
                    val uiHandler = Handler(Looper.getMainLooper())
                    val runnable = Runnable {
                        try {
                            this@CodeBoardIME.onKeyLongPress(primaryCode)
                        } catch (e: Exception) {
                            Log.e(javaClass.simpleName, "uiHandler.run: " + e.message, e)
                        }
                    }
                    uiHandler.post(runnable)
                } catch (e: Exception) {
                    Log.e(javaClass.simpleName, "Timer.run: " + e.message, e)
                }
            }
        }, ViewConfiguration.getLongPressTimeout().toLong())
    }

    override fun onExtractingInputChanged(ei: EditorInfo?) {
        Log.d(javaClass.simpleName, "onExtractingInputChanged: ")
    }

    override fun requestHideSelf(flags: Int) {
        var newFlag = HIDE_IMPLICIT_ONLY
        newFlag = flags
        Log.d(javaClass.simpleName, "requestHideSelf: $newFlag,$flags")
        // do nothing
        super.requestHideSelf(newFlag)
    }

    override fun onWindowHidden() {
        super.onWindowHidden()
        clearLongPressTimer()
    }

    override fun onViewClicked(focusChanged: Boolean) {
        super.onViewClicked(focusChanged)
        clearLongPressTimer()
    }

    override fun onRelease(primaryCode: Int) {
        /*
         *   After the space button is released,
         *   we check whether it was long pressed or not.
         *   If it was, we don't do anything,
         *   but If it wasn't, we print a "space" to the screen.
         */
        if (primaryCode == 32 && !longPressedSpaceButton) {
            val ic = currentInputConnection ?: return
            ic.commitText(primaryCode.toChar().toString(), 1)
        }

        longPressedSpaceButton = false

        clearLongPressTimer()
    }

    fun onKeyLongPress(keyCode: Int) {
        // Process long-click here
        // This is following an onKey()
        val ic = currentInputConnection ?: return
        if (keyCode == 16) {
            shiftLock = !shiftLock
            if (shiftLock) {
                shift = true
                ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_SHIFT_LEFT))
            } else {
                shift = false
                ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_SHIFT_LEFT))
            }
            shiftKeyUpdateView()
        }

        if (keyCode == 17) {
            ctrlLock = !ctrlLock
            if (ctrlLock) {
                ctrl = true
                ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_CTRL_LEFT))
            } else {
                ctrl = false
                ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_CTRL_LEFT))
            }
            controlKeyUpdateView()
        }

        if (keyCode == 32) {
            longPressedSpaceButton = true

            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.showInputMethodPicker()
        }

        if (vibratorOn) {
            val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            vibrator?.vibrate(vibrateLength.toLong())
        }
    }

    override fun onText(text: CharSequence?) {
        val ic = currentInputConnection ?: return
        ic.commitText(text, 1)
        clearLongPressTimer()
    }

    override fun swipeLeft() {}

    override fun swipeRight() {}

    override fun swipeDown() {}

    override fun swipeUp() {}

    @SuppressLint("MissingPermission")
    override fun onCreateInputView(): View? {
        if (mKeyboardUiFactory == null) {
            mKeyboardUiFactory = KeyboardUiFactory(this)
        }
        val sharedPreferences = KeyboardPreferences(this)
        setNotification(sharedPreferences.getNotification())
        if (sharedPreferences.getCustomTheme()) {
            mKeyboardUiFactory!!.theme = getDefaultThemeInfo()
            mKeyboardUiFactory!!.theme.foregroundColor = sharedPreferences.getFgColor()
            mKeyboardUiFactory!!.theme.backgroundColor = sharedPreferences.getBgColor()
        } else {
            mKeyboardUiFactory!!.theme = setThemeByIndex(sharedPreferences, sharedPreferences.getThemeIndex())
        }
        // Keyboard Features
        vibrateLength = sharedPreferences.getVibrateLength()
        vibratorOn = sharedPreferences.isVibrateEnabled()
        soundOn = sharedPreferences.isSoundEnabled()
        mKeyboardUiFactory!!.theme.enablePreview = sharedPreferences.isPreviewEnabled()
        mKeyboardUiFactory!!.theme.enableBorder = sharedPreferences.isBorderEnabled()
        mKeyboardUiFactory!!.theme.fontSize = sharedPreferences.getFontSizeAsSp()
        val mSize = sharedPreferences.getPortraitSize()
        val sizeLandscape = sharedPreferences.getLandscapeSize()
        mKeyboardUiFactory!!.theme.size = mSize / 100.0f
        mKeyboardUiFactory!!.theme.sizeLandscape = sizeLandscape / 100.0f
        if (sharedPreferences.getNavBarDark()) {
            Objects.requireNonNull(window!!.window)?.navigationBarColor =
                ColorUtils.blendARGB(mKeyboardUiFactory!!.theme.backgroundColor, Color.BLACK, 0.2f)
        } else if (sharedPreferences.getNavBar()) {
            Objects.requireNonNull(window!!.window)?.navigationBarColor = mKeyboardUiFactory!!.theme.backgroundColor
        }
        //Key Layout
        val mToprow = sharedPreferences.getTopRowActions()
        val mCustomSymbolsMain = sharedPreferences.getCustomSymbolsMain()
        val mCustomSymbolsMain2 = sharedPreferences.getCustomSymbolsMain2()
        val mCustomSymbolsSym = sharedPreferences.getCustomSymbolsSym()
        val mCustomSymbolsSym2 = sharedPreferences.getCustomSymbolsSym2()
        val mCustomSymbolsSym3 = sharedPreferences.getCustomSymbolsSym3()
        val mCustomSymbolsSym4 = sharedPreferences.getCustomSymbolsSym4()
        val mCustomSymbolsMainBottom = sharedPreferences.getCustomSymbolsMainBottom()
        val mLayout = sharedPreferences.getLayoutIndex()

        //Need this to get resources for drawables
        val definitions = Definitions(this)
        try {
            val builder = KeyboardLayoutBuilder(this)
            builder.setBox(Box.create(0f, 0f, 1f, 1f))

            if (mToprow) {
                definitions.addCopyPasteRow(builder)
            } else {
                definitions.addArrowsRow(builder)
            }

            if (mKeyboardState == R.integer.keyboard_sym) {
                if (mCustomSymbolsSym.isNotEmpty()) {
                    Definitions.addCustomRow(builder, mCustomSymbolsSym)
                }
                if (mCustomSymbolsSym2.isNotEmpty()) {
                    Definitions.addCustomRow(builder, mCustomSymbolsSym2)
                }
                if (mCustomSymbolsSym3.isNotEmpty()) {
                    Definitions.addCustomRow(builder, mCustomSymbolsSym3)
                }
                if (mCustomSymbolsSym4.isNotEmpty()) {
                    Definitions.addCustomRow(builder, mCustomSymbolsSym4)
                }
                if (mCustomSymbolsSym3.isEmpty() && mCustomSymbolsSym4.isEmpty()) {
                    definitions.addSymbolRows(builder)
                } else {
                    definitions.addCustomSpaceRow(builder, mCustomSymbolsMainBottom)
                }
            } else if (mKeyboardState == R.integer.keyboard_normal) {
                if (mCustomSymbolsMain.isNotEmpty()) {
                    Definitions.addCustomRow(builder, mCustomSymbolsMain)
                }
                if (mCustomSymbolsMain2.isNotEmpty()) {
                    Definitions.addCustomRow(builder, mCustomSymbolsMain2)
                }
                when (mLayout) {
                    1 -> Definitions.addAzertyRows(builder)
                    2 -> Definitions.addDvorakRows(builder)
                    3 -> Definitions.addQwertzRows(builder)
                    else -> Definitions.addQwertyRows(builder)
                }
                definitions.addCustomSpaceRow(builder, mCustomSymbolsMainBottom)
            } else if (mKeyboardState == R.integer.keyboard_clipboard) {
                definitions.addClipboardActions(builder)

                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                if (clipboard.hasPrimaryClip()
                    && clipboard.primaryClipDescription?.hasMimeType(MIMETYPE_TEXT_PLAIN) == true
                ) {
                    val pr: ClipData? = clipboard.primaryClip
                    //Android only allows one item in Clipboard
                    val s = pr?.getItemAt(0)?.text?.toString() ?: ""
                    builder.newRow().addKey(s)
                } else {
                    builder.newRow().addKey("Nothing copied").withOutputText("")
                }
                builder.addKey(sharedPreferences.getPin1())
                builder.newRow()
                    .addKey(sharedPreferences.getPin2())
                    .addKey(sharedPreferences.getPin3())
                builder.newRow()
                    .addKey(sharedPreferences.getPin4())
                    .addKey(sharedPreferences.getPin5())
                builder.newRow()
                    .addKey(sharedPreferences.getPin6())
                    .addKey(sharedPreferences.getPin7())
            }

            val keyboardLayout = builder.build()
            mCurrentKeyboardLayoutView = mKeyboardUiFactory!!.createKeyboardView(this, keyboardLayout)
            return mCurrentKeyboardLayoutView

        } catch (e: KeyboardLayoutException) {
            e.printStackTrace()
        }
        return null
    }

    override fun onUpdateExtractingVisibility(ei: EditorInfo?) {
        if (ei != null) {
            ei.imeOptions = ei.imeOptions or EditorInfo.IME_FLAG_NO_EXTRACT_UI
            super.onUpdateExtractingVisibility(ei)
        }
    }

    override fun onStartInputView(attribute: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(attribute, restarting)
        setInputView(onCreateInputView())
        sEditorInfo = attribute
    }

    fun controlKeyUpdateView() {
        mCurrentKeyboardLayoutView?.applyCtrlModifier(ctrl)
    }

    fun shiftKeyUpdateView() {
        mCurrentKeyboardLayoutView?.applyShiftModifier(shift)
    }

    private fun clearLongPressTimer() {
        if (timerLongPress != null) {
            timerLongPress!!.cancel()
        }
        timerLongPress = null
    }

    private fun setThemeByIndex(keyboardPreferences: KeyboardPreferences, index: Int): ThemeInfo {
        var themeInfo: ThemeInfo = ThemeDefinitions.Default()
        when (index) {
            0 -> {
                when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                    Configuration.UI_MODE_NIGHT_YES -> themeInfo = ThemeDefinitions.MaterialDark()
                    Configuration.UI_MODE_NIGHT_NO -> themeInfo = ThemeDefinitions.MaterialWhite()
                }
            }
            1 -> themeInfo = ThemeDefinitions.MaterialDark()
            2 -> themeInfo = ThemeDefinitions.MaterialWhite()
            3 -> themeInfo = ThemeDefinitions.PureBlack()
            4 -> themeInfo = ThemeDefinitions.White()
            5 -> themeInfo = ThemeDefinitions.Blue()
            6 -> themeInfo = ThemeDefinitions.Purple()
            else -> themeInfo = ThemeDefinitions.Default()
        }
        keyboardPreferences.setBgColor(themeInfo.backgroundColor.toString())
        keyboardPreferences.setFgColor(themeInfo.foregroundColor.toString())
        return themeInfo
    }

    private fun getDefaultThemeInfo(): ThemeInfo {
        return ThemeDefinitions.Default()
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.notification_channel_name)
            val description = getString(R.string.notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance)
            channel.description = description
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    @SuppressLint("MissingPermission")
    private fun setNotification(visible: Boolean) {
        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (visible && mNotificationReceiver == null) {
            val text: CharSequence = "Keyboard notification enabled."
            Log.i(javaClass.simpleName, "setNotification:$text")

            createNotificationChannel()
            mNotificationReceiver = NotificationReceiver(this)
            val pFilter = IntentFilter(NotificationReceiver.ACTION_SHOW)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                registerReceiver(mNotificationReceiver, pFilter, Context.RECEIVER_NOT_EXPORTED)
            } else {
                registerReceiver(mNotificationReceiver, pFilter)
            }


            val imeIntent = Intent(NotificationReceiver.ACTION_SHOW)
            val imePendingIntent = PendingIntent.getBroadcast(
                applicationContext,
                1, imeIntent, PendingIntent.FLAG_IMMUTABLE
            )

            // BUG: IM closes when notification drawer is closed
            // try making a input field here?
            // Key for the string that's delivered in the action's intent.
            val KEY_TEXT_REPLY = "key_text_reply"

            val remoteInput = RemoteInput.Builder(KEY_TEXT_REPLY)
                .setLabel("Now click first icon")
                .build()


            // Build a PendingIntent for the keyboard action to trigger first
            var flags = PendingIntent.FLAG_MUTABLE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                flags = PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_ALLOW_UNSAFE_IMPLICIT_INTENT
            }
            val replyPendingIntent = PendingIntent.getBroadcast(
                applicationContext,
                2,
                imeIntent,
                flags
            )


            // Create the reply action and add the remote input.
            val action = NotificationCompat.Action.Builder(
                R.drawable.icon_large,
                getString(R.string.notification_action_open_keyboard_workaround), replyPendingIntent
            )
                .addRemoteInput(remoteInput)
                .build()

            val settingsIntent = Intent(this, MainActivity::class.java)
            settingsIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            val settingsPendingIntent = PendingIntent.getActivity(
                this, 0,
                settingsIntent, PendingIntent.FLAG_IMMUTABLE
            )
            val title = "Show Codeboard Keyboard"
            val body = "Select this to open the keyboard. Disable in settings. You may have to fix open the fix as a workaround for newer Android versions"

            val mBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.icon_large)
                .setColor(0xff220044.toInt())
                .setAutoCancel(false)
                .setTicker(text)
                .setContentTitle(title)
                .setContentText(body)
                .setContentIntent(imePendingIntent)
                .setOngoing(true)
                .addAction(
                    R.drawable.icon_large, getString(R.string.notification_action_open_keyboard),
                    imePendingIntent
                )
                .addAction(
                    R.drawable.icon_large, getString(R.string.notification_action_settings),
                    settingsPendingIntent
                )
                .addAction(action)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            val notificationManager = NotificationManagerCompat.from(this)

            notificationManager.notify(NOTIFICATION_ONGOING_ID, mBuilder.build())

        } else if (!visible && mNotificationReceiver != null) {
            mNotificationManager.cancel(NOTIFICATION_ONGOING_ID)
            unregisterReceiver(mNotificationReceiver)
            mNotificationReceiver = null
        }
    }

    override fun onCreateInputMethodInterface(): AbstractInputMethodImpl {
        return MyInputMethodImpl()
    }

    inner class MyInputMethodImpl : InputMethodImpl() {
        override fun attachToken(token: IBinder?) {
            super.attachToken(token)
            Log.i(javaClass.simpleName, "attachToken $token")
            if (mToken == null) {
                mToken = token
            }
        }
    }
}

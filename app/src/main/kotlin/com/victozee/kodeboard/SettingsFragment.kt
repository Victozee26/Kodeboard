package com.victozee.kodeboard

import android.app.AlertDialog
import android.content.ComponentName
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.Secure.DEFAULT_INPUT_METHOD
import android.text.InputType
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.ColorInt
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.victozee.kodeboard.theme.IOnFocusListenable
import com.victozee.kodeboard.theme.ThemeDefinitions
import com.victozee.kodeboard.theme.ThemeInfo
import com.github.evilbunny2008.androidmaterialcolorpickerdialog.ColorPicker
import com.github.evilbunny2008.androidmaterialcolorpickerdialog.ColorPickerCallback

@Suppress("DEPRECATION")
class SettingsFragment : PreferenceFragmentCompat(), IOnFocusListenable {

    lateinit var keyboardPreferences: KeyboardPreferences

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        keyboardPreferences = KeyboardPreferences(requireActivity())

        //  Declare a new thread to do a preference check
        val t = Thread(Runnable {
            if (keyboardPreferences.isFirstStart()) {
                val i = Intent(requireActivity(), IntroActivity::class.java)
                startActivity(i)
                keyboardPreferences.setFirstStart(false)
            }
        })
        t.start()

        //Only allow numbers
        val numberOnlyPrefereces = arrayOf("vibrate_ms", "font_size", "size_portrait", "size_landscape")
        for (key in numberOnlyPrefereces) {
            val editTextPreference = preferenceManager.findPreference<EditTextPreference>(key)
            editTextPreference?.setOnBindEditTextListener(EditTextPreference.OnBindEditTextListener { editText: EditText ->
                editText.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED
            })
        }

        val themePreference = preferenceManager.findPreference<ListPreference>("theme")
        assert(themePreference != null)
        themePreference!!.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, newValue ->
            if (!keyboardPreferences.getCustomTheme()) {
                val index = Integer.parseInt(newValue.toString())
                preference.summary = resources.getStringArray(R.array.Themes)[index]
                setThemeByIndex(index)
                true
            } else {
                preference.summary = "Custom Theme is set"
                false
            }
        }

        val bundle = this.arguments
//        Log.d(this.javaClass.simpleName, "onCreatePreferences: $bundle" )
        if (bundle != null &&
            bundle.getInt("notification") == 1
        ) {
            scrollToPreference("notification")
        }
    }

    companion object {
        @JvmStatic
        fun getCurrentImeLabel(context: Context): CharSequence? {
            var readableName: CharSequence? = null
            val keyboard = Settings.Secure.getString(context.contentResolver, DEFAULT_INPUT_METHOD)
            val componentName = ComponentName.unflattenFromString(keyboard)
            if (componentName != null) {
                val packageName = componentName.packageName
                try {
                    val packageManager = context.packageManager
                    val info: ApplicationInfo = packageManager.getApplicationInfo(packageName, 0)
                    readableName = info.loadLabel(packageManager)
                } catch (e: PackageManager.NameNotFoundException) {
                    e.printStackTrace()
                }
            }
            return readableName
        }
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        val key = preference.key ?: return false
        //Run Intent
        when (key) {
            "change_keyboard" -> {
                val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showInputMethodPicker()
                preference.summary = getCurrentImeLabel(requireActivity().applicationContext)
            }
            "bg_colour_picker", "fg_colour_picker" -> {
                openColourPicker(key)
                preferenceManager.findPreference<Preference>("theme")?.summary = "Custom Theme is set"
            }
            "restore_default" -> confirmReset()
            "restore_old" -> classicSymbols()
            else -> {}
        }
        return super.onPreferenceTreeClick(preference)
    }

    private fun confirmReset() {
        AlertDialog.Builder(requireActivity())
            .setTitle("Reset?")
            .setMessage("This will reset all your custom symbols to the default")
            .setPositiveButton("Yes") { dialogInterface: DialogInterface, i: Int ->
                keyboardPreferences.resetAllToDefault()
                preferenceScreen?.removeAll()
                addPreferencesFromResource(R.xml.preferences)
            }
            .setNegativeButton("No") { dialogInterface: DialogInterface, i: Int -> }
            .show()
    }

    fun classicSymbols() {
        AlertDialog.Builder(requireActivity())
            .setTitle("Reset?")
            .setMessage("This will reset all your custom symbols to the old CodeBoard layout")
            .setPositiveButton("Yes") { dialogInterface: DialogInterface, i: Int ->
                keyboardPreferences.resetAllToDefault()
                var newValue = "()1234567890#"
                keyboardPreferences.setCustomSymbolsMain(newValue)
                keyboardPreferences.setCustomSymbolsSym(newValue)
                newValue = "+-=:*/{}+$[]"
                keyboardPreferences.setCustomSymbolsMain2(newValue)
                keyboardPreferences.setCustomSymbolsSym2(newValue)
                newValue = "&|%\\<>;',."
                keyboardPreferences.setCustomSymbolsMainBottom(newValue)
                keyboardPreferences.setCustomSymbolsSymBottom(newValue)
                preferenceScreen?.removeAll()
                addPreferencesFromResource(R.xml.preferences)
            }
            .setNegativeButton("No") { dialogInterface: DialogInterface, i: Int -> }
            .show()
    }

    private fun setThemeByIndex(index: Int) {
        val themeInfo: ThemeInfo = when (index) {
            1 -> ThemeDefinitions.MaterialDark()
            2 -> ThemeDefinitions.MaterialWhite()
            3 -> ThemeDefinitions.PureBlack()
            4 -> ThemeDefinitions.White()
            5 -> ThemeDefinitions.Blue()
            6 -> ThemeDefinitions.Purple()
            else -> ThemeDefinitions.Default()
        }
        keyboardPreferences.setBgColor(themeInfo.backgroundColor.toString())
        keyboardPreferences.setFgColor(themeInfo.foregroundColor.toString())
    }

    fun openColourPicker(key: String) {
        var color = 0
        if (key == "bg_colour_picker") {
            color = keyboardPreferences.getBgColor()
        } else if (key == "fg_colour_picker") {
            color = keyboardPreferences.getFgColor()
        }
        val cp = ColorPicker(
            requireActivity(),
            Color.red(color),
            Color.green(color),
            Color.blue(color)
        )
        cp.show()
        cp.enableAutoClose()
        cp.setCallback(object : ColorPickerCallback {
            override fun onColorChosen(@ColorInt color: Int) {
                if (key == "bg_colour_picker") {
                    keyboardPreferences.setBgColor(color.toString())
                } else if (key == "fg_colour_picker") {
                    keyboardPreferences.setFgColor(color.toString())
                }
            }
        })
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        if (hasFocus) {
            val imePreference = preferenceManager.findPreference<Preference>("change_keyboard")
            imePreference?.summary = getCurrentImeLabel(requireActivity().applicationContext)
        }
    }
}

package com.victozee.kodeboard

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.Secure.DEFAULT_INPUT_METHOD
import android.view.inputmethod.InputMethodManager
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.victozee.kodeboard.theme.IOnFocusListenable

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
            else -> {}
        }
        return super.onPreferenceTreeClick(preference)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        if (hasFocus) {
            val imePreference = preferenceManager.findPreference<Preference>("change_keyboard")
            imePreference?.summary = getCurrentImeLabel(requireActivity().applicationContext)
        }
    }
}

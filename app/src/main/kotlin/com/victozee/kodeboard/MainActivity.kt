package com.victozee.kodeboard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.victozee.kodeboard.theme.IOnFocusListenable

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_activity_main)
        val extras = intent.extras
        val frag = SettingsFragment()
        frag.arguments = extras
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings_container, frag)
            .commit()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        val currentFragment: Fragment? = supportFragmentManager.findFragmentById(R.id.settings_container)
        if (currentFragment is IOnFocusListenable) {
            currentFragment.onWindowFocusChanged(hasFocus)
        }
    }
}

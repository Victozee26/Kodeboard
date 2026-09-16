package com.victozee.kodeboard

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import com.github.appintro.AppIntro
import com.github.appintro.AppIntroFragment
import com.github.appintro.model.SliderPage

class IntroActivity : AppIntro() {

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addSlide(IntroFragment.newInstance(R.layout.codeboard_intro1))
        addSlide(IntroFragment.newInstance(R.layout.codeboard_intro2))

        @Suppress("DEPRECATION")
        val sliderPage = SliderPage(
            title = "All the shortcuts!",
            description = "Click 'ctrl' for select all, cut, copy, paste, or undo." +
                "\nCtrl+Shift+Z for redo" + "\n Long press Space to change keyboard",
            imageDrawable = R.drawable.intro_tutorial,
            backgroundColor = Color.parseColor("#3F51B5")
        )
        addSlide(AppIntroFragment.newInstance(sliderPage))
        // Set wizard mode to disable skip
        isWizardMode = true
    }

    override fun onSkipPressed(currentFragment: Fragment?) {
        super.onSkipPressed(currentFragment)
        // Do something when users tap on Skip button.
        finish()
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        // Do something when users tap on Done button.
        finish()
    }

    override fun onSlideChanged(oldFragment: Fragment?, newFragment: Fragment?) {
        super.onSlideChanged(oldFragment, newFragment)
        // Do something when the slide changes.
    }

    fun enableButtonIntro(v: View) {
        val intent = Intent(android.provider.Settings.ACTION_INPUT_METHOD_SETTINGS)
        startActivity(intent)
    }

    fun changeButtonIntro(v: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showInputMethodPicker()
    }
}

package com.victozee.kodeboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment

class IntroFragment : Fragment() {

    private var layoutResId: Int = 0

    companion object {
        private const val ARG_LAYOUT_RES_ID = "layoutResId"

        fun newInstance(layoutResId: Int): IntroFragment {
            val sampleSlide = IntroFragment()
            val args = Bundle()
            args.putInt(ARG_LAYOUT_RES_ID, layoutResId)
            sampleSlide.arguments = args
            return sampleSlide
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null && arguments!!.containsKey(ARG_LAYOUT_RES_ID)) {
            layoutResId = arguments!!.getInt(ARG_LAYOUT_RES_ID)
        }
    }

    @Nullable
    override fun onCreateView(inflater: LayoutInflater, @Nullable container: ViewGroup?, @Nullable savedInstanceState: Bundle?): View? {
        return inflater.inflate(layoutResId, container, false)
    }
}

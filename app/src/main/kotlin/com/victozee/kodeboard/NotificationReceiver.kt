package com.victozee.kodeboard

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.inputmethod.InputMethodManager

class NotificationReceiver internal constructor(private val mIME: CodeBoardIME) : BroadcastReceiver() {

    companion object {
        const val ACTION_SHOW = "com.victozee.kodeboard.SHOW"
    }

    init {
        Log.i(javaClass.simpleName, "NotificationReceiver created, ime=$mIME")
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action
        Log.i(javaClass.simpleName, "NotificationReceiver.onReceive called, action=$action")
        if (action != null) {
            if (action == ACTION_SHOW) {
                val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                if (imm != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        Log.i(javaClass.simpleName, "Version >= P" + Build.VERSION.SDK_INT)
                        mIME.requestShowSelf(InputMethodManager.SHOW_FORCED)
                    } else {
                        Log.i(javaClass.simpleName, "Version < P" + Build.VERSION.SDK_INT)
                        imm.showSoftInputFromInputMethod(mIME.mToken, InputMethodManager.SHOW_FORCED)
                    }
                }
            }
        }
    }
}

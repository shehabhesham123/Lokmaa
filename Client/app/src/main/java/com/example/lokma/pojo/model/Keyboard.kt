package com.example.lokma.pojo.model

import android.app.Activity
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil

class Keyboard {
    companion object {
        fun closeKeyboard(activity: Activity) {
            UIUtil.hideKeyboard(activity)
        }
    }
}
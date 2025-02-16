package de.sam.abinopolynew.func

import android.graphics.Color
import android.view.Window
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager

fun hideSystemUI(window: Window) {
    window.setDecorFitsSystemWindows(false)
    window.statusBarColor = Color.TRANSPARENT

    window.insetsController?.let {
        it.hide(WindowInsets.Type.navigationBars())
        it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}
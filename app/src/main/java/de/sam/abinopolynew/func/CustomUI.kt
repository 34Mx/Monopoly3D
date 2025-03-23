package de.sam.abinopolynew.func

import android.content.Context
import android.graphics.Color
import android.os.VibrationEffect
import android.os.Vibrator
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

fun vibrateShortly(context: Context) {
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    if (vibrator.hasVibrator()) {
        val effect = VibrationEffect.createOneShot(20, VibrationEffect.EFFECT_TICK)
        vibrator.vibrate(effect)
    }
}

fun vibrateLong(context: Context) {
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    if (vibrator.hasVibrator()) {
        val effect = VibrationEffect.createOneShot(500, VibrationEffect.EFFECT_HEAVY_CLICK)
        vibrator.vibrate(effect)
    }
}

fun vibrateXLong(context: Context, millis: Long) {
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    if (vibrator.hasVibrator()) {
        val effect = VibrationEffect.createOneShot(millis, VibrationEffect.EFFECT_HEAVY_CLICK)
        vibrator.vibrate(effect)
    }
}

fun vibrateDuhDuh(context: Context) {
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    if (vibrator.hasVibrator()) {
        val timings = longArrayOf(0, 50, 25, 35)
        val amplitudes = intArrayOf(0, 128, 0, 90)

        val effect = VibrationEffect.createWaveform(timings, -1)
        vibrator.vibrate(effect)
    }
}
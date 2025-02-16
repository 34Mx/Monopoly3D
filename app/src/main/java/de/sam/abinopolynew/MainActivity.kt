package de.sam.abinopolynew

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import de.sam.abinopolynew.func.hideSystemUI

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        hideSystemUI(window)

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, Home::class.java))
        }, 3000)
    }
}
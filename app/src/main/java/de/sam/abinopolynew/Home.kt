package de.sam.abinopolynew

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import de.sam.abinopolynew.func.hideSystemUI

class Home : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        hideSystemUI(window)
    }
}
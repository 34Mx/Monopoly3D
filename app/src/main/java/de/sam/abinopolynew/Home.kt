package de.sam.abinopolynew

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import de.sam.abinopolynew.func.hideSystemUI

class Home : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        hideSystemUI(window)
        val helpButton: Button = findViewById(R.id.button2)

        helpButton.setOnClickListener {
            startActivity(Intent(this, Help::class.java))
        }
    }
}
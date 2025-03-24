package de.sam.abinopolynew

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import de.sam.abinopolynew.func.hideSystemUI
import de.sam.abinopolynew.func.vibrateDuhDuh
import de.sam.abinopolynew.func.vibrateShortly

class Home : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        hideSystemUI(window)

        val newGameButton: Button = findViewById(R.id.newGameButton)
        val helpButton: Button = findViewById(R.id.button2)

        newGameButton.setOnClickListener {
            vibrateShortly(this)
            startActivity(Intent(this, NewGame::class.java))
        }

        helpButton.setOnClickListener {
//            startActivity(Intent(this, Help::class.java))
            finish()
        }
    }
}
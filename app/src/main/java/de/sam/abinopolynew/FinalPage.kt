package de.sam.abinopolynew

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import de.sam.abinopolynew.func.Player
import de.sam.abinopolynew.func.vibrateDuhDuh
import de.sam.abinopolynew.func.vibrateLong
import de.sam.abinopolynew.func.vibrateShortly
import de.sam.abinopolynew.func.vibrateXLong
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FinalPage : AppCompatActivity() {
    private lateinit var winnerMenu: ListView
    private lateinit var winnerHeading: TextView
    private val handler = Handler(Looper.getMainLooper())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_final_page)

        winnerMenu = findViewById(R.id.winnerMenu)
        winnerMenu.visibility = View.INVISIBLE

        winnerHeading = findViewById(R.id.winnerHeading)
        winnerHeading.visibility = View.INVISIBLE

        val winnerNames = intent.getStringArrayListExtra("winner_names") ?: arrayListOf()
        val winnerMoney = intent.getIntegerArrayListExtra("winner_money") ?: arrayListOf()
        winAnimation(this) {
            showWinner(winnerNames, winnerMoney)
            vibrateDuhDuh(this)
        }

        findViewById<AppCompatButton>(R.id.returnToHomeBtn).setOnClickListener {
            finish()
        }
    }


    private fun winAnimation(context: Context, onComplete: () -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            delay(500)
            // ta
            vibrateXLong(context, 300)
            delay(600)

            // taaaaaaaaaaaa
            vibrateXLong(context, 1000)
            delay(1000)

            onComplete()
        }
    }



    private fun showWinner(winnerNames: ArrayList<String>, winnerMoney: ArrayList<Int>) {
        val rankingList = winnerNames.indices.map { i ->
            "${i + 1}. ${winnerNames[i]}${if (i == 0) "\t👑" else ""}\t\t\t(${winnerMoney[i]}LZ)"
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, rankingList)
        winnerMenu.adapter = adapter

        winnerMenu.visibility = View.VISIBLE
        winnerHeading.visibility = View.VISIBLE
    }
}
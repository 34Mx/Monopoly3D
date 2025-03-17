package de.sam.abinopolynew

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import de.sam.abinopolynew.func.Board
import de.sam.abinopolynew.func.Player
import de.sam.abinopolynew.func.hideSystemUI


class Game : AppCompatActivity() {
    lateinit var game: Board
    lateinit var gameDisplay: TextView
    lateinit var cardView: ImageView
    lateinit var rollButton: AppCompatButton
    lateinit var dice1: ImageView
    lateinit var dice2: ImageView
    lateinit var dices: LinearLayout

    lateinit var fieldActionButton: AppCompatButton

    private val handler = Handler(Looper.getMainLooper())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        hideSystemUI(window)

        gameDisplay = findViewById(R.id.gameDisplay)

        cardView = findViewById(R.id.cardView)
        rollButton = findViewById(R.id.rollButton)
        dice1 = findViewById(R.id.dice1)
        dice2 = findViewById(R.id.dice2)
        dices = findViewById(R.id.dices)

        fieldActionButton = findViewById(R.id.fieldActionButton)


        game = Board()
        var testPlayer: Player = Player()
        game.addPlayer(testPlayer)

        rollButton.setOnClickListener {
            hideFieldMenu()
            rollAnimation {
                val roll = game.roll()
                setDices(roll)
                val diceSum = roll.sum()
                Log.d("34Mx.GAME", "Roll: $diceSum")
                handler.postDelayed({
                    game.movePlayer(testPlayer, diceSum)
                    game.getFieldInformation(testPlayer.position)?.fieldData?.let { it1 -> showFieldMenu(testPlayer.position, diceSum, it1.time) }
                }, 500)
            }
        }
    }

    fun showFieldMenu(id: Int, diceSum: Int, time: Int) {
        gameDisplay.setText("Gewürfelt: $diceSum!")
        val context: Context = this
        val id: Int = context.resources.getIdentifier("sub_$id", "drawable", context.packageName)
        cardView.setImageResource(id)
        dices.visibility = View.INVISIBLE
        cardView.visibility = View.VISIBLE
        fieldActionButton.visibility = View.VISIBLE
        fieldActionButton.setText("$time$");
    }


    fun hideFieldMenu() {
        dices.visibility = View.VISIBLE
        cardView.visibility = View.INVISIBLE
        fieldActionButton.visibility = View.INVISIBLE
        gameDisplay.setText("Spieler 1!")
    }

    private fun rollAnimation(onComplete: () -> Unit) {
        for (i in 0..10) {
            handler.postDelayed({
                val roll = game.roll()
                setDices(roll)
                if (i == 10) onComplete()
            }, i * 100L)
        }
    }

    fun setDices(values: Array<Int>) {
        val context: Context = this
        val id1: Int = context.resources.getIdentifier("dice_${values[0]}", "drawable", context.packageName)
        val id2: Int = context.resources.getIdentifier("dice_${values[1]}", "drawable", context.packageName)
        dice1.setImageResource(id1)
        dice2.setImageResource(id2)
    }

    /*
        Types:
            0 -> Eckpunkte
            1 -> Prüfung
            2 -> Gemeinschaftsfeld
            3 -> Ereignisfeld
            4 -> Kopiergeld
            5 -> Nachhilfe-Institut
            6 -> Fach
     */
//    fun handleActionConditions(position: Int): String {
//        val info = game.getFieldInformation(position)
//        if (info != null) {
//            when (info.type) {
//                0 -> fieldActionButton.visibility = View.INVISIBLE
//                1 -> showFieldActionButton("Prüfung starten")
//                2 -> showFieldActionButton()
//                3 -> showFieldActionButton("Prüfung starten")
//                4 -> showFieldActionButton("Prüfung starten")
//                5 -> showFieldActionButton("Prüfung starten")
//                6 -> showFieldActionButton("Prüfung starten")
//            }
//        }
//    }

    fun showFieldActionButton(text: String) {

    }
}
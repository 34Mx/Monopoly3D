package de.sam.abinopolynew

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import de.sam.abinopolynew.func.Board
import de.sam.abinopolynew.func.Player
import de.sam.abinopolynew.func.hideSystemUI


class Game : AppCompatActivity() {
    lateinit var game: Board
    lateinit var cardView: ImageView
    lateinit var rollButton: AppCompatButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        hideSystemUI(window)

        cardView = findViewById(R.id.cardView)
        rollButton = findViewById(R.id.rollButton)


        game = Board()
        var testPlayer: Player = Player("Samuel")
        game.addPlayer(testPlayer)

        rollButton.setOnClickListener {
            val roll = game.roll()
            Log.d("Game", "Roll: ${roll}")
            game.movePlayer(testPlayer, roll)
            updateGraphic(testPlayer.position)
        }
    }

    fun updateGraphic(id: Int) {
        val context: Context = this
        val id: Int = context.resources.getIdentifier("sub_${id}", "drawable", context.packageName)
        cardView.setImageResource(id)
    }
}
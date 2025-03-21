package de.sam.abinopolynew

import android.content.Context
import android.graphics.Color
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
import androidx.constraintlayout.widget.ConstraintLayout
import de.sam.abinopolynew.func.Board
import de.sam.abinopolynew.func.CommunityTask
import de.sam.abinopolynew.func.EventTask
import de.sam.abinopolynew.func.Field
import de.sam.abinopolynew.func.Player
import de.sam.abinopolynew.func.hideSystemUI
import org.w3c.dom.Text
import kotlin.math.absoluteValue


class Game : AppCompatActivity() {
    lateinit var game: Board
    lateinit var gameDisplay: TextView
    lateinit var cardView: ImageView
    lateinit var rollButton: AppCompatButton
    lateinit var dice1: ImageView
    lateinit var dice2: ImageView
    lateinit var dices: LinearLayout

    lateinit var moneyDisplay: TextView

    lateinit var fieldActionButton: AppCompatButton

    lateinit var commEventMenu: ConstraintLayout
    lateinit var commEventTitle: TextView
    lateinit var commEventContent: TextView

    lateinit var currentPlayer: Player

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

        moneyDisplay = findViewById(R.id.moneyDisplay)

        fieldActionButton = findViewById(R.id.fieldActionButton)

        commEventMenu = findViewById(R.id.commEventMenu)
        commEventTitle = findViewById(R.id.commEventTitle)
        commEventContent = findViewById(R.id.commEventContent)


        game = Board()
        currentPlayer = Player()
        game.addPlayer(currentPlayer)

        rollButton.setOnClickListener {
            hideCommEventMenu()
            hideFieldMenu()
            rollAnimation {
                val roll = game.roll()
                setDices(roll)
                val diceSum = roll.sum()
                Log.d("34Mx.GAME", "Roll: $diceSum")
                handler.postDelayed({
                    game.movePlayer(currentPlayer, diceSum)
                    val currentField: Field? = game.getFieldInformation(currentPlayer.position)
                    handleFieldAction(currentField, diceSum)
                }, 500)
            }
        }

        fieldActionButton.setOnClickListener {
            hideFieldActionButton()
            handleActionButton(game.getFieldInformation(currentPlayer.position))
        }
    }

    private fun showFieldMenu(id: Int, diceSum: Int, buttonText: String?) {
        gameDisplay.setText("Gewürfelt: $diceSum!")
        val context: Context = this
        val id: Int = context.resources.getIdentifier("sub_$id", "drawable", context.packageName)
        cardView.setImageResource(id)
        dices.visibility = View.INVISIBLE
        cardView.visibility = View.VISIBLE
        if (buttonText != null) showFieldActionButton(buttonText)
        else hideFieldActionButton()
    }

    private fun hideFieldMenu() {
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

    private fun setDices(values: Array<Int>) {
        val context: Context = this
        val id1: Int = context.resources.getIdentifier("dice_${values[0]}", "drawable", context.packageName)
        val id2: Int = context.resources.getIdentifier("dice_${values[1]}", "drawable", context.packageName)
        dice1.setImageResource(id1)
        dice2.setImageResource(id2)
    }

    /*
            Types:
                10 -> Eckpunkt: Start (+)
                11 -> Eckpunkt: Pausenhof (+)
                12 -> Eckpunkt: Mensa (+)
                13 -> Eckpunkt: Nachsitzen TODO: Nachsitzen
                1 -> Prüfung (-) TODO: Prüfung
                2 -> Gemeinschaftsfeld (+)
                3 -> Ereignisfeld (-) TODO: Ereignisfeld
                4 -> Kopiergeld (+)
                5 -> Nachhilfe-Institut (+)
                6 -> Fach (+)
         */
    private fun handleFieldAction(field: Field?, roll: Int) {
        if (field == null) return

        val type: Int = field.type
        val time: Int = field.fieldData?.time ?: 0

        var buttonText: String? = null

//        => wegen Startüberquert, GaLiGrü
        if (field.id - roll < 0) currentPlayer.deposit(200)

        when (type) {
            10 -> currentPlayer.deposit(400)
            12 -> currentPlayer.deposit(50)
            13 -> return // TODO: Later pos=Pausenhof
            1 -> buttonText = "Prüfung antreten"
            2, 3 -> buttonText = "Karte ziehen"
            4 -> currentPlayer.withdraw(50) // TODO: Insert correct value
            5 -> currentPlayer.withdraw(50)
            6 -> { if(field.fieldData?.time != null && field.fieldData.time <= currentPlayer.getMoney() && field.ownedBy == null) buttonText = "${time}$" }
            else -> {}
        }

        updateMoneyDisplay()
        showFieldMenu(field.id, roll, buttonText)
    }

    private fun handleActionButton(field: Field?) {
        if (field == null) return

        val type: Int = field.type
        val time = field.fieldData?.time ?: 0
        when (type) {
            2 -> handleCommunityTasks()
            3 -> handleEventTasks()
            6 -> { if (currentPlayer.withdraw(time)) { currentPlayer.addOwnedField(field.id); field.ownedBy = currentPlayer.id } }
        }

        updateMoneyDisplay()
    }

    private fun handleCommunityTasks() {
        val randomTask: CommunityTask = game.randomCommunityTask()
        val message = randomTask.text
        val time = randomTask.time

        showCommEventMenu("Gemeinschaftsfeld", message, "#eea5da")
        if (time < 0) currentPlayer.withdraw(time.absoluteValue)
        else currentPlayer.deposit(time.absoluteValue)
    }

    private fun handleEventTasks() {
        val randomTask: EventTask = game.randomEventTask()
        val message = randomTask.text
        val time = randomTask.time
        val newPos = randomTask.newPos

        Log.d("34Mx.GAME", message)

        showCommEventMenu("Ereignisfeld", message, "#8cc2e6")
        if (time != null) {
            if (time < 0) currentPlayer.withdraw(time.absoluteValue)
            else currentPlayer.deposit(time.absoluteValue)
        }
        if (newPos != null) game.setPlayerPosition(currentPlayer, newPos)
    }

    private fun showCommEventMenu(title: String, content: String, color: String) {
        commEventTitle.text = title
        commEventContent.text = content
        commEventMenu.setBackgroundColor(Color.parseColor(color))
        commEventMenu.visibility = View.VISIBLE
    }

    private fun hideCommEventMenu() {
        commEventMenu.visibility = View.INVISIBLE
    }

    private fun showFieldActionButton(text: String) {
        fieldActionButton.visibility = View.VISIBLE
        fieldActionButton.setText("$text");
    }

    private fun hideFieldActionButton() {
        fieldActionButton.visibility = View.INVISIBLE
    }

    private fun updateMoneyDisplay() {
        Log.d("34Mx.GAME", currentPlayer.getMoney().toString())
        moneyDisplay.setText(currentPlayer.getMoney().toString())
    }
}
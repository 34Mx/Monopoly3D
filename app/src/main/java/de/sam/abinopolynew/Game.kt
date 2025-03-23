package de.sam.abinopolynew

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import de.sam.abinopolynew.func.Board
import de.sam.abinopolynew.func.CommunityTask
import de.sam.abinopolynew.func.EventTask
import de.sam.abinopolynew.func.Field
import de.sam.abinopolynew.func.Player
import de.sam.abinopolynew.func.hideSystemUI
import de.sam.abinopolynew.func.vibrateDuhDuh
import de.sam.abinopolynew.func.vibrateLong
import de.sam.abinopolynew.func.vibrateShortly
import java.io.Serializable
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
    lateinit var moneyBalance: TextView
    lateinit var fieldActionButton: AppCompatButton
    lateinit var commEventMenu: ConstraintLayout
    lateinit var commEventTitle: TextView
    lateinit var commEventContent: TextView

    lateinit var paidButton: ImageView
    lateinit var paidDisplay: ConstraintLayout
    lateinit var paidViewHeader: TextView

    lateinit var paidCardView: ImageView
    lateinit var paidArrowLeft: ImageView
    lateinit var paidArrowRight: ImageView

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var players: List<Player>
    private var currentPlayerIndex: Int = 0
    private var currentPaidCardIndex: Int = 0

    private lateinit var abiBalance: TextView
    private lateinit var examMenu: ConstraintLayout
    private lateinit var abiHeading: TextView
    private lateinit var abiQuestion: TextView
    private lateinit var abiBtn1: AppCompatButton
    private lateinit var abiBtn2: AppCompatButton
    private lateinit var abiBtn3: AppCompatButton
    private lateinit var abiBtn4: AppCompatButton
    private lateinit var abiBtns: List<AppCompatButton>


    private var turnFinished = false
    var isProcessing: Boolean = false

    private val currentPlayer: Player
        get() = players[currentPlayerIndex]

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
        moneyBalance = findViewById(R.id.moneyBalance)
        fieldActionButton = findViewById(R.id.fieldActionButton)
        commEventMenu = findViewById(R.id.commEventMenu)
        commEventTitle = findViewById(R.id.commEventTitle)
        commEventContent = findViewById(R.id.commEventContent)

        paidButton = findViewById(R.id.paidButton)
        paidDisplay = findViewById(R.id.paidDisplay)
        paidViewHeader = findViewById(R.id.paidViewHeader)

        paidCardView = findViewById(R.id.paidCardView)
        paidArrowLeft = findViewById(R.id.paidArrowLeft)
        paidArrowRight = findViewById(R.id.paidArrowRight)

        abiBalance = findViewById(R.id.abiBalance)
        examMenu = findViewById(R.id.examMenu)
        abiHeading = findViewById(R.id.abiHeading)
        abiQuestion = findViewById(R.id.abiQuestion)
        abiBtn1 = findViewById(R.id.abiBtn1)
        abiBtn2 = findViewById(R.id.abiBtn2)
        abiBtn3 = findViewById(R.id.abiBtn3)
        abiBtn4 = findViewById(R.id.abiBtn4)
        abiBtns = listOf(abiBtn1, abiBtn2, abiBtn3, abiBtn4)

        game = Board()

        val b: Bundle? = intent.extras
        val playerCount: Int = b?.getInt("playerCount") ?: 2
        startGame(playerCount)

        rollButton.setOnClickListener {
            vibrateShortly(this)
            togglePaidDisplay(View.INVISIBLE)
            if (isProcessing) return@setOnClickListener
            if (turnFinished) {
                nextTurn()
                turnFinished = false
            }

            isProcessing = true
            rollButton.isEnabled = false

            hideCommEventMenu()
            hideFieldMenu()

            rollAnimation (this) {
                val roll = game.roll()
                setDices(roll)
                val diceSum = roll.sum()
                Log.d("34Mx.GAME", "Roll: $diceSum")
                handler.postDelayed({
                    game.movePlayer(currentPlayer, diceSum)
                    val currentField: Field? = game.getFieldInformation(currentPlayer.position)
                    handleFieldAction(currentField, diceSum)
                    isProcessing = false
                    rollButton.isEnabled = true
                }, 500)
            }
        }

        fieldActionButton.setOnClickListener {
            vibrateDuhDuh(this)
            hideFieldActionButton()
            handleActionButton(game.getFieldInformation(currentPlayer.position))
        }

        paidButton.setOnClickListener {
            vibrateShortly(this)
            togglePaidDisplay()
        }

        paidArrowLeft.setOnClickListener {
            vibrateShortly(this)
            movePaidCardLeft()
        }

        paidArrowRight.setOnClickListener {
            vibrateShortly(this)
            movePaidCardRight()
        }

        for (btnId in 0..3) {
            abiBtns[btnId].setOnClickListener {
                handleAbiAnswer(btnId)
            }
        }
    }



    private fun startGame(playerCount: Int) {
        if (playerCount !in 2..4) throw IllegalArgumentException("Spielerzahl muss 2-4 sein")
        players = List(playerCount) { Player("Spieler ${it + 1}", it + 1) }
        players.forEach { game.addPlayer(it) }
        currentPlayerIndex = 0
        updateUI()
    }

    private fun nextTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size
        latestPlayerMoney = currentPlayer.getMoney()
        updateUI()
    }

    private fun updateUI() {
        gameDisplay.text = "${currentPlayer.name} ist am Zug!"
        updateMoneyDisplay()
        updateAbiBalance()
    }

    private fun showFieldMenu(id: Int, diceSum: Int, buttonText: String?) {
        gameDisplay.text = "Gewürfelt: $diceSum!"
        val context: Context = this
        val drawableId: Int = context.resources.getIdentifier("sub_$id", "drawable", context.packageName)
        cardView.setImageResource(drawableId)
        dices.visibility = View.INVISIBLE
        cardView.visibility = View.VISIBLE
        if (buttonText != null) showFieldActionButton(buttonText)
        else hideFieldActionButton()
    }

    private fun hideFieldMenu() {
        dices.visibility = View.VISIBLE
        cardView.visibility = View.INVISIBLE
        fieldActionButton.visibility = View.INVISIBLE
        gameDisplay.text = "${currentPlayer.name} ist am Zug!"
    }

    private fun rollAnimation(context: Context, onComplete: () -> Unit) {
        for (i in 0..10) {
            handler.postDelayed({
                val roll = game.roll()
                setDices(roll)

                // Einfach Vibratiooooooooon beim Würfeln
                vibrateShortly(context)

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
       Feldtypen:
           10 -> Eckpunkt: Start (+)
           11 -> Eckpunkt: Pausenhof (+)
           12 -> Eckpunkt: Mensa (+)
           13 -> Eckpunkt: Nachsitzen (+)
           1  -> Prüfung (-) (TODO)
           2  -> Gemeinschaftsfeld (+)
           3  -> Ereignisfeld (+)
           4  -> Kopiergeld (+)
           5  -> Nachhilfe-Institut (+)
           6  -> Fach (+)
    */
    private fun handleFieldAction(field: Field?, roll: Int) {
        if (field == null) return

        val type: Int = field.type
        val time: Int = field.fieldData?.time ?: 0

        var buttonText: String? = null

        if (field.id - roll < 0 && field.id != 0) currentPlayer.deposit(200)

        turnFinished = true
        when (type) {
            10 -> currentPlayer.deposit(400)
            12 -> currentPlayer.deposit(50)
            13 -> { game.setPlayerPosition(currentPlayer, 10) }
            1  -> buttonText = "Prüfung antreten"
            2, 3 -> { rollButton.visibility = View.INVISIBLE; buttonText = "Karte ziehen"; turnFinished = false }
            4 -> currentPlayer.withdraw(50)
            5 -> currentPlayer.withdraw(50)
            6 -> {
                if (field.fieldData != null) {
                    if (field.fieldData.time <= currentPlayer.getMoney() && field.ownedBy == null) {
                        buttonText = "${time} LZ"
                    } else if (field.ownedBy != null && field.ownedBy != currentPlayer.id) {
                        val ownerId = field.ownedBy!!
                        val owner = game.getPlayerById(ownerId)!!

                        val groupFields = game.fields.filter { it.fieldData?.group == field.fieldData.group }
                        val ownsAllFieldsInGroup = groupFields.all { it.ownedBy == ownerId }

                        val rentToPay = if (ownsAllFieldsInGroup) {
                            field.fieldData.rentTotal
                        } else {
                            field.fieldData.rent
                        }

                        val enoughMoney = currentPlayer.withdraw(rentToPay)
                        if (enoughMoney) {
                            owner.deposit(rentToPay)
                        }
                    }
                }
            }
            else -> { }
        }

        updateMoneyDisplay()
        showFieldMenu(field.id, roll, buttonText)
    }

    private fun handleActionButton(field: Field?) {
        if (field == null) return

        val type: Int = field.type
        val time = field.fieldData?.time ?: 0
        when (type) {
            1 -> handleAbiTasks(field.id)
            2 -> handleCommunityTasks()
            3 -> handleEventTasks()
            6 -> {
                if (currentPlayer.withdraw(time)) {
                    currentPlayer.addOwnedField(field.id)
                    field.ownedBy = currentPlayer.id
                }
            }
        }
        updateMoneyDisplay()
    }

    var currentAbiAnswer: Int = 0
    private fun handleAbiTasks(id: Int) {
        val newIndex = when (id) {
            5 -> 0
            15 -> 1
            25 -> 2
            35 -> 3
            else -> 0
        }

        currentPlayer.addExam(newIndex)
        updateAbiBalance()
        Log.d("34Mx.GAME", "${currentPlayer.name} opened exam $newIndex")

        val task = game.getAbiTask(newIndex)
        currentAbiAnswer = task.rightAnswer
        abiHeading.text = game.getFieldInformation(id)!!.title
        abiQuestion.text = task.question
        for (n in 0..3) {
            abiBtns[n].text = task.answers[n]
        }
        openExamMenu()
    }

    private fun handleAbiAnswer(playerAnswer: Int) {
        if (playerAnswer == currentAbiAnswer) {
            vibrateLong(this)
            closeExamMenu()
            currentPlayer.deposit(100)
            Log.d("34Mx.GAME", "${currentPlayer.name} passed exam")
        }
        else {
            vibrateDuhDuh(this)
            closeExamMenu()
            currentPlayer.withdraw(100)
            Log.d("34Mx.GAME", "${currentPlayer.name} failed exam")
        }
        updateMoneyDisplay()
        if (currentPlayer.examsOwned.size == 4) {
            finishGame()
        }
    }

    private fun openExamMenu() {
        examMenu.visibility = View.VISIBLE
    }

    private fun closeExamMenu() {
        examMenu.visibility = View.INVISIBLE
    }

    private fun updateAbiBalance() {
        abiBalance.text = "Abi: ${currentPlayer.examsOwned.size}/4\n(${currentPlayer.examsOwned.joinToString{it -> "${getRomanNum(it+1)}"}})"
    }

    private fun getRomanNum(number: Int): String {
        return when(number) {
            1 -> "I"
            2 -> "II"
            3 -> "III"
            4 -> "IV"
            else -> "$number"
        }
    }

    private fun handleCommunityTasks() {
        val randomTask: CommunityTask = game.randomCommunityTask()
        val message = randomTask.text
        val time = randomTask.time

        showCommEventMenu("Gemeinschaftsfeld", message, "#eea5da")
        if (time < 0) currentPlayer.withdraw(time.absoluteValue)
        else currentPlayer.deposit(time.absoluteValue)
        rollButton.visibility = View.VISIBLE
        turnFinished = true
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
        Log.e("34MX.GAME", "newPos=$newPos")
        if (newPos != null) game.setPlayerPosition(currentPlayer, newPos)
        rollButton.visibility = View.VISIBLE
        turnFinished = true
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
        fieldActionButton.text = text
    }

    private fun hideFieldActionButton() {
        fieldActionButton.visibility = View.INVISIBLE
    }

    var latestPlayerMoney = 1500
    private fun updateMoneyDisplay() {
        Log.d("34Mx.GAME", currentPlayer.getMoney().toString())
        moneyDisplay.text = currentPlayer.getMoney().toString()
        val difference = currentPlayer.getMoney() - latestPlayerMoney
        Log.d("34Mx.GAME", "Diff: $difference")
        if (difference != 0) moneyBalance.text = "${if (difference > 0) "+" else ""} $difference"
        else moneyBalance.text = ""
    }

    private fun togglePaidDisplay(forcedValue: Int? = null) {
        currentPaidCardIndex = 0
        if (forcedValue != null) { paidDisplay.visibility = forcedValue; return }
        if (paidDisplay.visibility == View.INVISIBLE) {
            paidDisplay.visibility = View.VISIBLE
            updatePaidCardView()
        }
        else paidDisplay.visibility = View.INVISIBLE
    }

    private fun updatePaidCardView() {
        paidViewHeader.text = "Gekauft: ${currentPlayer.fieldsOwned.size}"
        Log.d("34MX.GAME", currentPlayer.fieldsOwned.toString())
        val context: Context = this
        val drawableId: Int = context.resources.getIdentifier(
            "sub_${getPaidCardByIndex(currentPaidCardIndex)}", "drawable", context.packageName
        )
        paidCardView.setImageResource(drawableId)
    }

    private fun getPaidCardByIndex(index: Int): Int {
        if (index !in 0 until currentPlayer.fieldsOwned.size) return -1
        return currentPlayer.fieldsOwned[index]
    }

    private fun movePaidCardRight() {
        val size = currentPlayer.fieldsOwned.size
        if (size == 0) return
        currentPaidCardIndex = (currentPaidCardIndex + 1) % size
        updatePaidCardView()
    }

    private fun movePaidCardLeft() {
        val size = currentPlayer.fieldsOwned.size
        if (size == 0) return
        currentPaidCardIndex = (currentPaidCardIndex - 1 + size) % size
        updatePaidCardView()
    }

    private fun finishGame() {
        val winnerRanking = getWinnerRanking()

        val names = ArrayList(winnerRanking.map { it.name })
        val money = ArrayList(winnerRanking.map { it.getMoney() })

        val intent = Intent(this@Game, FinalPage::class.java).apply {
            putStringArrayListExtra("winner_names", names)
            putIntegerArrayListExtra("winner_money", money)
        }

        startActivity(intent)
        finish()
    }

    private fun getWinnerRanking(): ArrayList<Player> {
        return ArrayList(players.sortedByDescending { it.getMoney() })
    }
}

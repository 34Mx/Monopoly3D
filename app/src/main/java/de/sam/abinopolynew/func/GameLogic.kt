package de.sam.abinopolynew.func

import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
data class FieldData(val time: Int, val rent: Int, val rentTotal: Int, val color: String)
@Serializable
data class Field(val id: Int, val title: String, val description: String, val type: Int, val fieldData: FieldData? = null)

data class CommunityTask(
    val text: String,
    val time: Int
)

data class EventTask(
    val text: String,
    val time: Int? = null,
    val newPos: Int? = null
)

class Player() {
    var position: Int = 0
    var balance: Int = 1500

    fun deposit(amount: Int) {
        if (amount > 0) {
            balance += amount
        }
    }

    fun withdraw(amount: Int): Boolean {
        return if (amount in 1..balance) {
            balance -= amount
            true
        } else {
            false
        }
    }

    fun getMoney(): Int {
        return balance
    }
}

class Board {
    private val fields: List<Field>
    private val players = mutableMapOf<Player, Int>()

    init {
        /*
            Types:
                10 -> Eckpunkt: Start
                11 -> Eckpunkt: Pausenhof
                12 -> Eckpunkt: Mensa
                13 -> Eckpunkt: Nachsitzen
                1 -> Prüfung
                2 -> Gemeinschaftsfeld
                3 -> Ereignisfeld
                4 -> Kopiergeld
                5 -> Nachhilfe-Institut
                6 -> Fach
         */
        val json = """
            [
                {"id": 0, "title": "Start", "description": "Startfeld", "type": 10},
                {"id": 1, "title": "Mathe", "description": "", "type": 6, "fieldData":  {"time": 400, "rent":  100, "rentTotal":  200, "color":  "sub_darkBlue"}},
                {"id": 2, "title": "Gemeinschaftsfeld", "description": "", "type": 2},
                {"id": 3, "title": "Deutsch", "description": "", "type": 6, "fieldData":  {"time": 350, "rent":  80, "rentTotal":  160, "color":  "sub_darkBlue"}},
                {"id": 4, "title": "Kopiergeld", "description": "", "type": 4},
                {"id": 5, "title": "Abitur I", "description": "", "type": 1},
                {"id": 6, "title": "Kunst", "description": "", "type": 6, "fieldData":  {"time": 100, "rent":  15, "rentTotal":  30, "color":  "sub_lightBlue"}},
                {"id": 7, "title": "Ereignisfeld", "description": "", "type": 3},
                {"id": 8, "title": "Musik", "description": "", "type": 6, "fieldData":  {"time": 100, "rent":  15, "rentTotal":  30, "color":  "sub_lightBlue"}},
                {"id": 9, "title": "Sport", "description": "", "type": 6, "fieldData":  {"time": 120, "rent":  20, "rentTotal":  40, "color":  "sub_lightBlue"}},
                {"id": 10, "title": "Pausenhof", "description": "", "type": 11},
                {"id": 11, "title": "Religion", "description": "", "type": 6, "fieldData":  {"time": 140, "rent":  25, "rentTotal":  50, "color":  "sub_pink"}},
                {"id": 12, "title": "Nachhilfe-Institut", "description": "", "type": 5},
                {"id": 13, "title": "Philosophie", "description": "", "type": 6, "fieldData":  {"time": 140, "rent":  25, "rentTotal":  50, "color":  "sub_pink"}},
                {"id": 14, "title": "Ethik", "description": "", "type": 6, "fieldData":  {"time": 160, "rent":  30, "rentTotal":  60, "color":  "sub_pink"}},
                {"id": 15, "title": "Abitur II", "description": "", "type": 1},
                {"id": 16, "title": "Informatik", "description": "", "type": 6, "fieldData":  {"time": 180, "rent":  30, "rentTotal":  60, "color":  "sub_orange"}},
                {"id": 17, "title": "Gemeinschaftsfeld", "description": "", "type": 2},
                {"id": 18, "title": "Politik", "description": "", "type": 6, "fieldData":  {"time": 180, "rent":  30, "rentTotal":  60, "color":  "sub_orange"}},
                {"id": 19, "title": "Geschichte", "description": "", "type": 6, "fieldData":  {"time": 200, "rent":  40, "rentTotal":  80, "color":  "sub_orange"}},
                {"id": 20, "title": "Mensa", "description": "", "type": 12},
                {"id": 21, "title": "Pädagogik", "description": "", "type": 6, "fieldData":  {"time": 220, "rent": 45, "rentTotal": 90, "color":  "sub_red"}},
                {"id": 22, "title": "Ereignisfeld", "description": "", "type": 3},
                {"id": 23, "title": "Erdkunde", "description": "", "type": 6, "fieldData":  {"time": 220, "rent": 45, "rentTotal": 90, "color":  "sub_red"}},
                {"id": 24, "title": "Psychologie", "description": "", "type": 6, "fieldData":  {"time": 240, "rent": 50, "rentTotal": 100, "color":  "sub_red"}},
                {"id": 25, "title": "Abitur III", "description": "", "type": 1},
                {"id": 26, "title": "Englisch", "description": "", "type": 6, "fieldData":  {"time": 240, "rent": 55, "rentTotal": 110, "color":  "sub_yellow"}},
                {"id": 27, "title": "Spanisch", "description": "", "type": 6, "fieldData":  {"time": 240, "rent": 55, "rentTotal": 110, "color":  "sub_yellow"}},
                {"id": 28, "title": "Gemeinschaftsfeld", "description": "", "type": 2},
                {"id": 29, "title": "Französisch", "description": "", "type": 6, "fieldData":  {"time": 260, "rent": 60, "rentTotal": 120, "color":  "sub_yellow"}},
                {"id": 30, "title": "Nachsitzen", "description": "", "type": 13},
                {"id": 31, "title": "Chemie", "description": "", "type": 6, "fieldData":  {"time": 300, "rent": 65, "rentTotal": 130, "color":  "sub_green"}},
                {"id": 32, "title": "Biologie", "description": "", "type": 6, "fieldData":  {"time": 300, "rent": 65, "rentTotal": 130, "color":  "sub_green"}},
                {"id": 33, "title": "Gemeinschaftsfeld", "description": "", "type": 2},
                {"id": 34, "title": "Physik", "description": "", "type": 6, "fieldData":  {"time": 320, "rent": 70, "rentTotal": 140, "color":  "sub_green"}},
                {"id": 35, "title": "Abitur IV", "description": "", "type": 1},
                {"id": 36, "title": "Ereignisfeld", "description": "", "type": 3},
                {"id": 37, "title": "Latein", "description": "", "type": 6, "fieldData":  {"time": 60, "rent": 10, "rentTotal": 20, "color":  "sub_purple"}},
                {"id": 38, "title": "Kopiergeld", "description": "", "type": 4},
                {"id": 39, "title": "Chinesisch", "description": "", "type": 6, "fieldData":  {"time": 80, "rent": 10, "rentTotal": 20, "color":  "sub_purple"}}
            ]
        """
        fields = Json.decodeFromString(json)
    }

    fun addPlayer(player: Player) {
        players[player] = player.position
    }

    fun movePlayer(player: Player, amount: Int) {
        val newPosition = (players[player]!! + amount - 1) % fields.size + 1
        players[player] = newPosition
        player.position = newPosition
    }

    fun getPlayerPosition(player: Player): Int {
        return players[player] ?: error("Spieler nicht gefunden")
    }

    fun getFieldInformation(position: Int): Field? {
        return fields.find { it.id == position }
    }

    fun roll(): Array<Int> {
//        val dice1 = (1..6).random()
//        val dice2 = (1..6).random()
//        return arrayOf(dice1, dice2)
        return arrayOf(1,1)
    }

    fun randomCommunityTask(): CommunityTask {
        val tasks = listOf(
            CommunityTask("Du musst die Renovierung der Schule finanziell unterstützen.\nGib 100min Lernzeit ab.", -100),
            CommunityTask("Du hast aus Versehen den Feueralarm ausgelöst.\nGib als Strafe 60min Lernzeit ab.", -60),
            CommunityTask("Da du dich für ein MINT-EC Camp angemeldet hast, erhälst du als Belohnung 70min Lernzeit!", 70),
            CommunityTask("Du hast das Kopiergeld nicht bezahlt.\nMache 5 Liegestützen", 0)
        )
        return tasks[(tasks.indices).random()]
    }

    fun randomEventTask(): EventTask {
        val tasks = listOf(
            EventTask("Gehe zurück auf Los. Erhalte 200min Lernzeit.", 200, 0),
            EventTask("Du hast eine 6 in deinem letzten Fach erhalten. Gib 60min Lernzeit ab!", -60),
            EventTask("Du hast deine Hausaufgaben vergessen. Gib als Strafe 100min Lernzeit ab!", -100),
            EventTask("Du hast in deinem letzten Fach eine Zusatzaufgabe erledigt. Erhalte als Belohnung 100min Lernzeit!", 100),
            EventTask("Du gehst nach dem Lernen zur Erholung in die Mensa. Erhalte 20min Lernzeit!", 20, 20),
            EventTask("Du hast zu lange Pause gemacht und musst aussetzen. Gehe auf den Pausenhof.", newPos=10),
            EventTask("Du hast eine Freistunde. Begib dich auf den Pausenhof.", newPos=10),
            EventTask("Du hast Sport geschwänzt. Mache 10 Hampelmänner!"),
            EventTask("Um dich zu verbessern, begibst du dich zur Nachhilfe. Gib 90min Lernzeit ab.", -90, 12),
        )

        return tasks[(tasks.indices).random()]
    }
}
package de.sam.abinopolynew.func

class Field(val id: Int, val description: String)

class Player(val name: String) {
    var position: Int = 1
}

class Board {
    private val fields = List(40) { i -> Field(i + 1, "Feld ${i + 1}") }
    private val players = mutableMapOf<Player, Int>()

    fun addPlayer(player: Player) {
        players[player] = player.position
    }

    fun movePlayer(player: Player, amount: Int) {
        val newPosition = (players[player]!! + amount - 1) % 40 + 1
        players[player] = newPosition
        player.position = newPosition
    }

    fun getPlayerPosition(player: Player): Int {
        return players[player] ?: error("Spieler nicht gefunden")
    }

    fun getFieldInformation(position: Int): String {
        return fields.find { it.id == position }?.description ?: "Ungültige Position"
    }
}

//fun main() {
//    val board = Board()
//    val player1 = Player("Sam")
//    val player2 = Player("Simon")
//    board.addPlayer(player1)
//    board.addPlayer(player2)
//
//    board.movePlayer(player1, 5)
//    println("${player1.name} ist jetzt auf Feld ${board.getPlayerPosition(player1)}")
//    println("Feldbeschreibung: ${board.getFieldInformation(board.getPlayerPosition(player1))}")
//}
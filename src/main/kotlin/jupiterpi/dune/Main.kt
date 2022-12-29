package jupiterpi.dune

fun main() {
    val game = Game(
        listOf(
            Player("Player 1", Player.Color.RED, Leader.ATREIDES_PAUL),
            Player("Player 2", Player.Color.GREEN, Leader.HARKONNEN_BEAST),
            Player("Player 3", Player.Color.BLUE, Leader.THORVALD_MEMNO),
            Player("Player 4", Player.Color.YELLOW, Leader.RICHESE_ILBAN),
        )
    )
}
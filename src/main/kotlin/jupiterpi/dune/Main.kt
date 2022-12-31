package jupiterpi.dune

fun main() {
    val game = Game()
    game.players.addAll(listOf(
        Player(game, "Player 1", Player.Color.RED, Leader.ATREIDES_PAUL),
        Player(game, "Player 2", Player.Color.GREEN, Leader.HARKONNEN_BEAST),
        Player(game, "Player 3", Player.Color.BLUE, Leader.THORVALD_MEMNO),
        Player(game, "Player 4", Player.Color.YELLOW, Leader.RICHESE_ILBAN),
    ))
}
package jupiterpi.dune

import jupiterpi.dune.game.Game
import jupiterpi.dune.game.Leader
import jupiterpi.dune.game.Player
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

fun main() {
    val game = Game()
    game.players.addAll(listOf(
        Player(game, "Player 1", Player.Color.RED, Leader.ATREIDES_PAUL),
        Player(game, "Player 2", Player.Color.GREEN, Leader.HARKONNEN_BEAST),
        Player(game, "Player 3", Player.Color.BLUE, Leader.THORVALD_MEMNO),
        Player(game, "Player 4", Player.Color.YELLOW, Leader.RICHESE_ILBAN),
    ))

    runApplication<DuneApplication>()
}

@SpringBootApplication
class DuneApplication

@RestController
class Controller {
    @GetMapping("/test")
    fun test() = "works!"
}
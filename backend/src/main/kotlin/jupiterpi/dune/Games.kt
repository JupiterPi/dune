package jupiterpi.dune

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import jupiterpi.dune.game.Game
import jupiterpi.dune.game.Leader
import jupiterpi.dune.game.Player
import kotlinx.serialization.Serializable
import java.util.concurrent.atomic.AtomicLong

object Games {
    private val pendingGamesId = AtomicLong()
    private val pendingGames = mutableMapOf<Long, PendingGame>()

    private val games = mutableMapOf<Long, Game>()

    fun Application.configureGames() {
        routing {
            route("games") {
                post("create") {
                    val id = pendingGamesId.incrementAndGet()
                    pendingGames[id] = PendingGame()
                    call.respond(mapOf("gameId" to id))
                }
                webSocket("{id}/join") {
                    @Serializable
                    data class PlayerJoinDTO(
                        val name: String,
                        val color: Player.Color,
                        val leader: Leader,
                    )
                    val dto = receiveDeserialized<PlayerJoinDTO>()

                    val id: Long by call.parameters
                    val pendingGame = pendingGames[id] ?: return@webSocket call.respondText("Game not found", status = HttpStatusCode.NotFound)

                    pendingGame.players += Player(dto.name, dto.color, dto.leader, PlayerConnection(this))

                    println("... would receive")

                    //TODO tmp, make the connection not close immediately
                    for (frame in incoming) {
                        println(frame)
                        println((frame as? Frame.Text)?.readText())
                    }
                }
                post("{id}/start") {
                    val id: Long by call.parameters
                    val pendingGame = pendingGames[id] ?: return@post call.respondText("Game not found", status = HttpStatusCode.NotFound)
                    games[id] = Game(pendingGame.players)
                    pendingGames.remove(id)

                    call.respond("")
                }
            }
        }
    }

    data class PendingGame(
        val players: MutableList<Player> = mutableListOf(),
    )
}
package jupiterpi.dune

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import jupiterpi.dune.game.Game
import jupiterpi.dune.game.Leader
import jupiterpi.dune.game.Player
import kotlinx.serialization.Serializable
import java.util.concurrent.atomic.AtomicInteger

private val lobbyId = AtomicInteger()
private class Lobby {
    val id = lobbyId.incrementAndGet()
    val players = mutableListOf<Player>()

    lateinit var game: Game
    val isStarted get() = ::game.isInitialized
    fun start() {
        game = Game(players)
    }
}
private val lobbies = mutableListOf<Lobby>()

fun Application.configureGames() {
    routing {
        route("games") {
            post("create") {
                val lobby = Lobby().also { lobbies += it }
                call.respond(mapOf("gameId" to lobby.id))
            }
            webSocket("{id}/join") {
                val username = authenticate() ?: return@webSocket

                @Serializable
                data class PlayerJoinDTO(
                    val color: Player.Color,
                    val leader: Leader,
                )
                val dto = receiveDeserialized<PlayerJoinDTO>()

                val id: Int by call.parameters
                val lobby = lobbies.singleOrNull { it.id == id } ?: return@webSocket close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Game not found"))

                if (lobby.players.any { it.name == username }) {
                    val player = lobby.players.single { it.name == username }
                    player.connection.session = this
                    if (lobby.isStarted) {
                        player.connection.refreshGameState(lobby.game)
                        player.connection.refreshPlayerGameStates(player)
                    }
                } else {
                    lobby.players += Player(username, dto.color, dto.leader, PlayerConnection(this))
                }

                //TODO tmp, make the connection not close immediately
                for (frame in incoming) {
                    println(frame)
                    println((frame as? Frame.Text)?.readText())
                }
            }
            authenticate {
                post("{id}/start") {
                    val username = call.principal<UserIdPrincipal>()!!.name
                    val id: Int by call.parameters
                    val lobby = lobbies.singleOrNull { it.id == id } ?: return@post call.respondText("Game not found", status = HttpStatusCode.NotFound)
                    if (lobby.players.none { it.name == username }) return@post call.respondText("Player not in the game", status = HttpStatusCode.Forbidden)
                    if (lobby.isStarted) return@post call.respondText("Game already started", status = HttpStatusCode.Conflict)
                    lobby.start()
                    call.respondText("Started")
                }
            }
        }
    }
}
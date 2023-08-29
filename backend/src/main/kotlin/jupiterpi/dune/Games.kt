package jupiterpi.dune

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.util.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import jupiterpi.dune.game.Game
import jupiterpi.dune.game.Leader
import jupiterpi.dune.game.Player
import kotlinx.serialization.Serializable
import java.util.concurrent.atomic.AtomicInteger

data class PlayerData(
    val name: String,
)
fun SessionsConfig.configurePlayerData() {
    cookie<PlayerData>("player_data", SessionStorageMemory())
}

private val lobbyId = AtomicInteger()
private class Lobby {
    val id = lobbyId.incrementAndGet()
    val players = mutableListOf<Player>()

    lateinit var game: Game
    val started get() = ::game.isInitialized
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
                @Serializable
                data class PlayerJoinDTO(
                    val name: String,
                    val color: Player.Color,
                    val leader: Leader,
                )
                val dto = receiveDeserialized<PlayerJoinDTO>()

                val id: Int by call.parameters
                val lobby = lobbies.find { it.id == id } ?: return@webSocket call.respondText("Game not found", status = HttpStatusCode.NotFound)

                lobby.players += Player(dto.name, dto.color, dto.leader, PlayerConnection(this))

                println("... would receive")

                //TODO tmp, make the connection not close immediately
                for (frame in incoming) {
                    println(frame)
                    println((frame as? Frame.Text)?.readText())
                }
            }
            post("{id}/start") {
                val id: Int by call.parameters
                val lobby = lobbies.find { it.id == id } ?: return@post call.respondText("Game not found", status = HttpStatusCode.NotFound)
                lobby.start()

                call.respond("")
            }
        }
    }
}
package jupiterpi.dune

import jupiterpi.dune.game.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.messaging.simp.annotation.SubscribeMapping
import org.springframework.stereotype.Controller
import java.util.*

class SocketHandler(
    private val template: SimpMessagingTemplate,
) : Handler {
    override fun refreshGameState() {
        template.convertAndSend("/topic/game", GameDTO(game))
    }

    override fun refreshPlayerGameStates() {
        game.players.forEach {
            template.convertAndSend("/topic/player/${it.name}/game", PlayerDTO(it))
        }
    }

    override fun requestPlayerActionType(player: Player): Game.PlayerActionType
    = Game.PlayerActionType.valueOf(
        request(player, UserRequest("PLAYER_ACTION_TYPE")).content
    )

    override fun requestAgentCard(player: Player): AgentCard
    = AgentCard.valueOf(
        request(player, UserRequest("AGENT_CARD")).content
    )

    override fun requestAgentAction(player: Player): AgentAction
    = AgentAction.valueOf(
        request(player, UserRequest("AGENT_ACTION")).content
    )

    private val responses = mutableMapOf<String, UserResponse>()

    private fun request(player: Player, request: UserRequest): UserResponse {
        template.convertAndSend("/topic/player/${player.name}/requests", request)

        var response: UserResponse? = null
        val job = GlobalScope.launch {
            while (responses[request.id] == null) {
                delay(100)
            }
            response = responses[request.id]
        }
        runBlocking {
            job.join()
        }
        return response!!
    }

    fun handleResponse(response: UserResponse) {
        responses[response.requestId] = response
    }
}

data class UserRequest(
    val type: String,
    val content: String? = null,
) {
    val id = UUID.randomUUID().toString()
}

data class UserResponse(
    val requestId: String,
    val content: String,
)

@Controller
class GameController(
    template: SimpMessagingTemplate,
) {
    init {
        handler = SocketHandler(template)
    }

    @SubscribeMapping("/game")
    fun getGame() = GameDTO(game)

    @SubscribeMapping("/player/{name}/game")
    fun getPlayerGame(@DestinationVariable("name") playerName: String): PlayerGameDTO? {
        val player = game.players.find { it.name == playerName }
        return if (player == null) null else PlayerGameDTO(player)
    }

    @MessageMapping("/player/requests")
    fun handleResponse(userResponse: UserResponse) {
        val handler = handler
        if (handler is SocketHandler) handler.handleResponse(userResponse)
    }
}
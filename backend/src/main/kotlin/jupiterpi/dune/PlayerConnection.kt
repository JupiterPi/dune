package jupiterpi.dune

import io.ktor.server.websocket.*
import io.ktor.util.reflect.*
import io.ktor.websocket.*
import jupiterpi.dune.game.*
import kotlinx.coroutines.delay
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.lang.Exception
import java.nio.charset.Charset
import java.util.UUID

class PlayerConnection private constructor(
    private val session: DefaultWebSocketServerSession
): Handler {
    override suspend fun refreshGameState(game: Game) {
        session.send("game", GameDTO(game))
    }

    override suspend fun refreshPlayerGameStates(player: Player) {
        session.send("playerGame", PlayerGameDTO(player))
    }

    override suspend fun requestSimpleChoice(choices: List<String>, min: Int, max: Int): List<Int> {
        val results = request("SIMPLE_CHOICE", mapOf(
            "options" to choices,
            "min" to min,
            "max" to max,
        )).split(",").map { it.toInt() }
        if (results.size !in min..max) throw Exception("Amount of results $results are not in range of $min..$max")
        return results
    }

    override suspend fun requestPlayerActionType(): Game.PlayerActionType
    = Game.PlayerActionType.valueOf(request("PLAYER_ACTION_TYPE"))

    override suspend fun requestAgentCard(): AgentCard
    = AgentCard.valueOf(request("AGENT_CARD"))

    override suspend fun requestAgentAction(): AgentAction
    = AgentAction.valueOf(request("AGENT_CARD"))

    override suspend fun requestOptionallyPlaceTroopsIntoConflict(amount: Int): Int
    = request("OPTIONALLY_PLACE_TROOPS", mapOf("amount" to amount)).toInt()

    override suspend fun requestSellSpiceAmount(maxAmount: Int): Int
    = request("SELL_SPICE_AMOUNT", mapOf("maxAmount" to maxAmount)).toInt()

    override suspend fun requestDestroyCardFromHand(): AgentCard
    = AgentCard.valueOf(request("DESTROY_CARD"))

    override suspend fun requestInfluenceWithAny(): Faction
    = Faction.valueOf(request("INFLUENCE_WITH_ANY"))

    // -----

    companion object {
        suspend fun create(session: DefaultWebSocketServerSession): PlayerConnection {
            val connection = PlayerConnection(session)
            for (frame in session.incoming) {
                connection.handleIncomingPacket(session.deserialize<Packet>(frame))
            }
            return connection
        }
    }

    private fun handleIncomingPacket(packet: Packet) {
        when (packet.topic) {
            "request" -> {
                data class UserResponse(
                    val requestId: String,
                    val content: String,
                )
                val response = packet.payload as UserResponse
                responses[response.requestId] = response.content
            }
            else -> {
                println(packet)
            }
        }
    }

    private val responses = mutableMapOf<String, String>()

    private suspend fun request(type: String, payload: Any? = null) = request(UserRequest(type, payload))
    private suspend fun request(request: UserRequest): String {
        session.send("request", request)
        while(responses[request.id] == null) {
            delay(10)
        }
        return responses.remove(request.id)!!
    }

    @Serializable
    data class UserRequest(
        val type: String,
        @Contextual val payload: Any? = null
    ) {
        val id = UUID.randomUUID().toString()
    }
}

suspend inline fun <reified T> WebSocketServerSession.deserialize(frame: Frame): T
= converter?.deserialize(Charset.defaultCharset(), TypeInfo(T::class, T::class.java), frame) as T

suspend fun WebSocketServerSession.send(topic: String, payload: Any) {
    sendSerialized(Packet(topic, payload))
}

@Serializable
data class Packet(
    val topic: String,
    @Contextual val payload: Any,
)
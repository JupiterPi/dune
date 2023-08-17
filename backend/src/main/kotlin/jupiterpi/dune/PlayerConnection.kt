package jupiterpi.dune

import io.ktor.server.websocket.*
import jupiterpi.dune.game.Game
import jupiterpi.dune.game.Player
import jupiterpi.dune.game.enums.AgentAction
import jupiterpi.dune.game.enums.AgentCard
import jupiterpi.dune.game.enums.Faction
import jupiterpi.dune.game.enums.IntrigueCard
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.*

class PlayerConnection(private val session: DefaultWebSocketServerSession): Handler {
    override suspend fun refreshGameState(game: Game) {
        session.send("game", GameDTO(game))
    }

    override suspend fun refreshPlayerGameStates(player: Player) {
        session.send("playerGame", PlayerGameDTO(player))
    }

    override suspend fun requestMultipleChoices(title: String, choices: List<String>, min: Int, max: Int): List<Int> {
        val results = request("SIMPLE_CHOICE", mapOf(
            "options" to choices,
            "min" to min,
            "max" to max,
        )).split(",").map { it.toInt() }
        if (results.size !in min..max) throw Exception("Amount of results $results are not in range of $min..$max")
        return results
    }

    override suspend fun requestChoice(title: String, choices: List<String>)
    = requestMultipleChoices(title, choices, 1, 1).single()

    override suspend fun requestPlayerActionType(): Game.PlayerActionType
    = requestChoice(
        "Choose a player action",
        listOf("Plot Intrigue Card", "Agent Action", "Uncover Action")
    ).let { Game.PlayerActionType.entries[it] }

    override suspend fun requestIntrigueCard(type: IntrigueCard.Type)
    = IntrigueCard.valueOf(request("INTRIGUE_CARD", mapOf("type" to type)))

    override suspend fun requestAgentCard()
    = AgentCard.valueOf(request("AGENT_CARD"))

    override suspend fun requestAgentCardAndAction(available: Map<AgentCard, List<AgentAction>>)
    = request("AGENT_CARD_AND_ACTION", available).split(",")
        .let { AgentCard.valueOf(it[0]) to AgentAction.valueOf(it[1]) }

    override suspend fun requestAgentCardFromMarket()
    = AgentCard.valueOf(request("AGENT_CARD_FROM_MARKET"))

    override suspend fun requestOptionallyPlaceTroopsIntoConflict(amount: Int): Int
    = request("OPTIONALLY_PLACE_TROOPS", mapOf("amount" to amount)).toInt()

    override suspend fun requestSellSpiceAmount(maxAmount: Int): Int
    = request("SELL_SPICE_AMOUNT", mapOf("maxAmount" to maxAmount)).toInt()

    override suspend fun requestDestroyCardFromHand(): AgentCard
    = AgentCard.valueOf(request("DESTROY_CARD"))

    override suspend fun requestInfluenceWithAny(): Faction
    = Faction.valueOf(request("INFLUENCE_WITH_ANY"))

    // -----

    private suspend fun request(type: String, payload: Any? = null) = request(UserRequest(type, payload))
    private suspend fun request(request: UserRequest): String {
        session.send("request", request)
        while (true) {
            val packet = session.receiveDeserialized<Packet>()
            val valid = packet.topic == "request" && (packet.payload as? UserResponse)?.requestId == request.id
            if (valid) return (packet.payload as UserResponse).content
            else error("Unhandled packet: $packet")
        }
    }

    @Serializable
    data class UserRequest(
        val type: String,
        @Contextual val payload: Any? = null
    ) {
        val id = UUID.randomUUID().toString()
    }

    @Serializable
    data class UserResponse(
        val requestId: String,
        val content: String,
    )
}

suspend fun WebSocketServerSession.send(topic: String, payload: Any) {
    sendSerialized(Packet(topic, payload))
}

@Serializable
data class Packet(
    val topic: String,
    @Contextual val payload: Any,
)
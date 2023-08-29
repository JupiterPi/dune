package jupiterpi.dune

import io.ktor.server.websocket.*
import jupiterpi.dune.game.Game
import jupiterpi.dune.game.Player
import jupiterpi.dune.game.enums.AgentAction
import jupiterpi.dune.game.enums.AgentCard
import jupiterpi.dune.game.enums.Faction
import jupiterpi.dune.game.enums.IntrigueCard
import kotlinx.serialization.Serializable
import java.util.*

class PlayerConnection(var session: DefaultWebSocketServerSession): Handler {
    override suspend fun refreshGameState(game: Game) {
        session.send("game", GameDTO(game))
    }

    override suspend fun refreshPlayerGameStates(player: Player) {
        session.send("playerGame", PlayerGameDTO(player))
    }

    override suspend fun requestMultipleChoices(title: String, choices: List<String>, min: Int, max: Int): List<Int> {
        @Serializable data class DTO(val choices: List<String>, val min: Int, val max: Int)
        val results = request("SIMPLE_CHOICE", DTO(choices, min, max)).split(",").map { it.toInt() }
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

    private suspend fun request(type: String) = request<String?>(type, null)
    private suspend inline fun <reified T> request(type: String, payload: T) = request(UserRequest(type, payload))
    private suspend inline fun <reified T> request(request: UserRequest<T>): String {
        session.send("request", request)
        while (true) {
            val response = session.receive<UserResponse>("request")
            if (response.requestId == request.id) return response.content
            else error("Unhandled response: $response")
        }
    }

    @Serializable
    data class UserRequest<T>(
        val type: String,
        val payload: T
    ) {
        val id = UUID.randomUUID().toString()
    }

    @Serializable
    data class UserResponse(
        val requestId: String,
        val content: String,
    )
}

suspend inline fun <reified T> WebSocketServerSession.send(topic: String, payload: T) {
    sendSerialized(Packet(topic, payload))
}

suspend inline fun <reified T> WebSocketServerSession.receive(topic: String): T {
    while (true) {
        val packet = receiveDeserialized<Packet<T>>()
        println("received packet: $packet, ${packet.payload}")
        if (packet.topic == topic) return packet.payload
        else error("Unhandled packet: $packet")
    }
}

@Serializable
class Packet<T>(
    val topic: String,
    val payload: T,
)
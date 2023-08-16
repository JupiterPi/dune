package jupiterpi.dune

import jupiterpi.dune.game.*
import jupiterpi.dune.game.enums.AgentAction
import jupiterpi.dune.game.enums.AgentCard
import jupiterpi.dune.game.enums.Faction
import jupiterpi.dune.game.enums.IntrigueCard

interface Handler {
    suspend fun refreshGameState(game: Game)
    suspend fun refreshPlayerGameStates(player: Player)

    suspend fun requestMultipleChoices(title: String, choices: List<String>, min: Int, max: Int): List<Int>
    suspend fun requestChoice(title: String, choices: List<String>): Int

    suspend fun requestPlayerActionType(): Game.PlayerActionType
    suspend fun requestIntrigueCard(type: IntrigueCard.Type): IntrigueCard
    suspend fun requestAgentCard(): AgentCard
    suspend fun requestAgentCardAndAction(available: Map<AgentCard, List<AgentAction>>): Pair<AgentCard, AgentAction>
    suspend fun requestAgentCardFromMarket(): AgentCard
    suspend fun requestOptionallyPlaceTroopsIntoConflict(amount: Int): Int
    suspend fun requestSellSpiceAmount(maxAmount: Int): Int
    suspend fun requestDestroyCardFromHand(): AgentCard
    suspend fun requestInfluenceWithAny(): Faction
}
package jupiterpi.dune

import jupiterpi.dune.game.*

interface Handler {
    suspend fun refreshGameState(game: Game)
    suspend fun refreshPlayerGameStates(player: Player)

    suspend fun requestSimpleChoice(choices: List<String>, min: Int, max: Int): List<Int>

    suspend fun requestPlayerActionType(): Game.PlayerActionType
    suspend fun requestAgentCard(): AgentCard
    suspend fun requestAgentAction(): AgentAction
    suspend fun requestOptionallyPlaceTroopsIntoConflict(amount: Int): Int
    suspend fun requestSellSpiceAmount(maxAmount: Int): Int
    suspend fun requestDestroyCardFromHand(): AgentCard
    suspend fun requestInfluenceWithAny(): Faction
}
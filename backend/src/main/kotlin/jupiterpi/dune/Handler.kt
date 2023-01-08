package jupiterpi.dune

import jupiterpi.dune.game.*

lateinit var handler: Handler

interface Handler {
    fun refreshGameState()
    fun refreshPlayerGameStates()

    fun requestSimpleChoice(player: Player, choices: List<String>, min: Int, max: Int): List<Int>

    fun requestPlayerActionType(player: Player): Game.PlayerActionType
    fun requestAgentCard(player: Player): AgentCard
    fun requestAgentAction(player: Player): AgentAction
    fun requestOptionallyPlaceTroopsIntoConflict(player: Player, amount: Int): Int
    fun requestSellSpiceAmount(player: Player, maxAmount: Int): Int
    fun requestDestroyCardFromHand(player: Player): AgentCard
    fun requestInfluenceWithAny(player: Player): Faction
}
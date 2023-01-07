package jupiterpi.dune

import jupiterpi.dune.game.AgentAction
import jupiterpi.dune.game.AgentCard
import jupiterpi.dune.game.Game
import jupiterpi.dune.game.Player

lateinit var handler: Handler

interface Handler {
    fun refreshGameState()
    fun refreshPlayerGameStates()

    fun requestPlayerActionType(player: Player): Game.PlayerActionType
    fun requestAgentCard(player: Player): AgentCard
    fun requestAgentAction(player: Player): AgentAction
}
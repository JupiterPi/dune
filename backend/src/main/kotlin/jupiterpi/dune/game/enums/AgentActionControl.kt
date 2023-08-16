package jupiterpi.dune.game.enums

import jupiterpi.dune.game.Player

enum class AgentActionControl(
    val grantControlBonusToPlayer: (controlledBy: Player) -> Unit,
) {
    ARRAKEEN({ controlledBy -> controlledBy.solari += 1 }),
    CARTHAG({ controlledBy -> controlledBy.solari += 1 }),
    IMPERIAL_BASIN({ controlledBy -> controlledBy.spice += 1 }),
    ;

    companion object {
        fun get(agentAction: AgentAction) = when (agentAction) {
            AgentAction.ARRAKEEN -> ARRAKEEN
            AgentAction.CARTHAG -> CARTHAG
            AgentAction.IMPERIAL_BASIN -> IMPERIAL_BASIN
            else -> null
        }
    }
}
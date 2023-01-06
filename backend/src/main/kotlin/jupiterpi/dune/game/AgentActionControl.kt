package jupiterpi.dune.game

enum class AgentActionControl(
    val grantControlBonusToPlayer: (controlledBy: Player) -> Unit,
) {
    ARRAKEEN({ controlledBy -> controlledBy.solari += 1 }),
    CARTHAG({ controlledBy -> controlledBy.solari += 1 }),
    IMPERIAL_BASIN({ controlledBy -> controlledBy.spice += 1 }),
}
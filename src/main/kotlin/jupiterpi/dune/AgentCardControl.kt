package jupiterpi.dune

enum class AgentCardControl(
    private val grantControlBonusToPlayer: (controlledBy: Player) -> Unit,
) {
    ARRAKEEN({ controlledBy -> controlledBy.solari += 1 }),
    CARTHAG({ controlledBy -> controlledBy.solari += 1 }),
    IMPERIAL_BASIN({ controlledBy -> controlledBy.spice += 1 });

    var controlledBy: Player? = null

    fun grantControlBonus() {
        controlledBy.let {
            if (it != null) grantControlBonusToPlayer(it)
        }
    }
}
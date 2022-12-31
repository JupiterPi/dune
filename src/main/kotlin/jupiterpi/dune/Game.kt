package jupiterpi.dune

class Game {
    val players = mutableListOf<Player>()

    // conflict cards

    private val conflictCardStack = mutableListOf<ConflictCard>()
    lateinit var activeConflictCard: ConflictCard
        private set

    init {
        for (level in 1..3) {
            conflictCardStack.addAll(ConflictCard.values().filter { it.level == level }.shuffled())
        }
        drawNextConflictCard()
    }

    private fun drawNextConflictCard() {
        activeConflictCard = conflictCardStack.removeFirst()
    }

    // intrigue cards

    private val intrigueCardStack = IntrigueCard.values().flatMap { card -> generateSequence { card }.take(card.amountInGame).toList() }.toMutableList()

    fun drawIntrigueCard(): IntrigueCard = intrigueCardStack.removeFirst()

    // allies

    val allies = mutableMapOf<Faction, Player?>(
        Faction.IMPERATOR to null,
        Faction.SPACING_GUILD to null,
        Faction.BENE_GESSERIT to null,
        Faction.FREMEN to null,
    )

    // agent card control

    val control = mutableMapOf<AgentActionControl, Player?>(
        AgentActionControl.ARRAKEEN to null,
        AgentActionControl.CARTHAG to null,
        AgentActionControl.IMPERIAL_BASIN to null,
    )

    fun grantControlBonus(agentActionControl: AgentActionControl) {
        agentActionControl.grantControlBonusToPlayer(control[agentActionControl]!!)
    }
}
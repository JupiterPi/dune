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

    // agent actions

    val availableAgentActions = mutableSetOf<AgentAction>()

    init { refreshAvailableAgentActions() }
    private fun refreshAvailableAgentActions() {
        availableAgentActions.clear()
        availableAgentActions.addAll(AgentAction.values())
    }

    fun blockAgentAction(agentAction: AgentAction) {
        availableAgentActions.remove(agentAction)
    }

    // agent actions: high council

    val highCouncilMembers = mutableSetOf<Player>()

    fun grantHighCouncilBenefits() {
        highCouncilMembers.forEach { it.convictionPoints += 2 }
    }

    // agent actions: aggregated spice

    private val aggregatedSpice = mutableMapOf(
        AgentAction.GREAT_PLAIN to 0,
        AgentAction.HAGGA_BASIN to 0,
        AgentAction.IMPERIAL_BASIN to 0,
    )

    private val spiceAggregationActionVisited = mutableMapOf(
        AgentAction.GREAT_PLAIN to false,
        AgentAction.HAGGA_BASIN to false,
        AgentAction.IMPERIAL_BASIN to false,
    )

    fun aggregateSpice() {
        aggregatedSpice.keys.forEach { action ->
            if (spiceAggregationActionVisited[action] == true) {
                spiceAggregationActionVisited[action] = false
            } else {
                aggregatedSpice[action] = aggregatedSpice[action]!! + 1
            }
        }
    }

    fun consumeAggregatedSpice(agentAction: AgentAction): Int {
        spiceAggregationActionVisited[agentAction] = true
        val spice = aggregatedSpice[agentAction] ?: 0
        aggregatedSpice[agentAction] = 0
        return spice
    }
}
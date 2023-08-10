package jupiterpi.dune.game

class Game(
    val players: List<Player>,
) {
    init {
        players.forEach { it.game = this }
    }

    // conflict cards

    private val conflictCardStack = mutableListOf<ConflictCard>()
    lateinit var activeConflictCard: ConflictCard
        private set

    init {
        for (level in 1..3) {
            conflictCardStack.addAll(ConflictCard.entries.filter { it.level == level }.shuffled())
        }
    }

    private fun drawNextConflictCard() {
        activeConflictCard = conflictCardStack.removeFirst()
        activeConflictCard.grantImmediateEffects(this)
    }

    // intrigue cards

    private val intrigueCardStack = IntrigueCard.entries.flatMap { card -> generateSequence { card }.take(card.amountInGame).toList() }.toMutableList()

    fun drawIntrigueCard(): IntrigueCard = intrigueCardStack.removeFirst()

    init {
        //TODO tmp
        repeat(4) {
            intrigueCardStack.addAll(intrigueCardStack)
        }
    }

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
        availableAgentActions.addAll(AgentAction.entries.toTypedArray())
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

    val aggregatedSpice = mutableMapOf(
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

    // ----- run -----

    var lifecyclePhase: LifecyclePhase = LifecyclePhase.NOT_STARTED
        private set

    suspend fun run() {
        while (true) {

            // --- ROUND_START ---
            lifecyclePhase = LifecyclePhase.ROUND_START

            drawNextConflictCard()

            players.forEach {
                it.drawCardsFromDeck(5)
            }

            // --- PLAYERS ---
            lifecyclePhase = LifecyclePhase.PLAYERS

            val activePlayers = players.toMutableList()
            while (activePlayers.isNotEmpty()) {
                val playersToRemove = mutableListOf<Player>()
                activePlayers.forEach { player ->

                    val playerActionType: PlayerActionType = if (player.agentsLeft > 0) {
                        player.connection.requestPlayerActionType()
                    } else {
                        PlayerActionType.UNCOVER_ACTION
                    }
                    when (playerActionType) {
                        PlayerActionType.AGENT_ACTION -> {

                            val agentCard = player.connection.requestAgentCard()
                            player.agentsLeft -= 1
                            player.discardCardFromHand(agentCard)
                            player.cardsPlayedThisRound.add(agentCard)

                            val agentAction = player.connection.requestAgentAction()
                            if (agentAction.isUsableForPlayer(player, agentCard.agentSymbols)) {
                                agentAction.useForPlayer(player)
                                //TODO block card
                            }

                        }
                        PlayerActionType.UNCOVER_ACTION -> {

                            player.hand.forEach {
                                it.uncoverEffect(player)
                            }
                            player.cardsPlayedThisRound.addAll(player.hand)
                            player.discardHand()
                            playersToRemove.add(player)

                        }
                    }

                }
                activePlayers.removeAll(playersToRemove)
            }

            // --- CONFLICT ---
            lifecyclePhase = LifecyclePhase.CONFLICT
            //TODO ...

            // --- SANDWORMS ---
            lifecyclePhase = LifecyclePhase.SANDWORMS
            aggregateSpice()

            // --- RECALL ---
            lifecyclePhase = LifecyclePhase.RECALL
            //TODO ...

        }
    }

    enum class LifecyclePhase(
        val title: String,
    ) {
        NOT_STARTED("Not Started"),
        ROUND_START("Round Start"),
        PLAYERS("Players' Turns"),
        CONFLICT("Conflict"),
        SANDWORMS("Sandworms"),
        RECALL("Recall"),
    }

    enum class PlayerActionType {
        AGENT_ACTION, UNCOVER_ACTION
    }
}
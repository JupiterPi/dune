package jupiterpi.dune.game

import jupiterpi.dune.game.enums.*
import kotlinx.coroutines.runBlocking

class Game(
    val players: List<Player>,
) {
    init {
        players.forEach { it.game = this }
    }

    // conflict cards

    private val conflictCardStack = mutableListOf<ConflictCard>()
    lateinit var activeConflictCard: ConflictCard

    init {
        mapOf(1 to 1, 2 to 5, 3 to 4).forEach { (level, n) ->
            conflictCardStack += ConflictCard.entries.filter { it.level == level }.shuffled().take(n)
        }
    }

    fun drawNextConflictCard() {
        activeConflictCard = conflictCardStack.removeFirst()
        activeConflictCard.grantImmediateEffects(this)
    }

    // intrigue cards

    private val intrigueCardStack = IntrigueCard.entries.flatMap { card -> generateSequence { card }.take(card.amountInGame).toList() }.toMutableList()

    fun drawIntrigueCard() = intrigueCardStack.removeFirst()

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

    fun refreshAvailableAgentActions() {
        availableAgentActions.clear()
        availableAgentActions.addAll(AgentAction.entries.toTypedArray())
    }

    fun blockAgentAction(agentAction: AgentAction) {
        availableAgentActions -= agentAction
    }

    // agent actions: high council

    val highCouncilMembers = mutableSetOf<Player>()

    fun conditionallyGrantHighCouncilBenefits(player: Player) {
        if (player in highCouncilMembers) player.convictionPoints += 2
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

    // market

    val market = mutableListOf<AgentCard>()

    fun consumeFromMarket(card: AgentCard) {
        market -= card
        //TODO tmp
        market += AgentCard.entries.random()
    }

    // ----- run -----

    var lifecyclePhase: LifecyclePhase = LifecyclePhase.NOT_STARTED
        private set

    suspend fun run() {

        lifecyclePhase = LifecyclePhase.ROUND_START
        run_roundStart()

        lifecyclePhase = LifecyclePhase.PLAYERS
        run_players()

        lifecyclePhase = LifecyclePhase.CONFLICT
        run_conflict()

        lifecyclePhase = LifecyclePhase.SANDWORMS
        aggregateSpice()

        if (players.any { it.victoryPoints >= 10 }) {
            lifecyclePhase = LifecyclePhase.FINALE
            run_finale()
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
        FINALE("Finale"),
    }

    enum class PlayerActionType {
        PLOT_INTRIGUE_CARD, AGENT_ACTION, UNCOVER_ACTION
    }

    init {
        //TODO tmp
        runBlocking {
            players.forEach {
                it.connection.refreshGameState(this@Game)
                it.connection.refreshPlayerGameStates(it)
            }
        }
    }
}
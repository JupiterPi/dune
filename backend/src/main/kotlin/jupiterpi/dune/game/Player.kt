package jupiterpi.dune.game

import jupiterpi.dune.PlayerConnection

class Player(
    val name: String,
    val color: Color,
    val leader: Leader,
    val connection: PlayerConnection,
) {
    lateinit var game: Game

    enum class Color(val rgb: Int) {
        RED(0xF23838),
        GREEN(0x33A650),
        BLUE(0x448FF2),
        YELLOW(0xF2B705),
    }

    // basic resources
    //TODO add checks for max

    var solari = 0
    var spice = 0
    var water = 0

    var victoryPoints = 1

    var agentsLeft = 0
    var totalAgents = 2

    var convictionPoints = 0

    //TODO correctly implement whether troops can be placed into conflict (-> Leader.HARKONNEN_BEAST)
    var troopsInConflict = 0
    var additionalMilitaryStrength = 0
    var troopsInGarrison = 0
    val totalMilitaryStrength get() = if (troopsInConflict > 0) troopsInConflict * 2 + additionalMilitaryStrength else 0

    fun resetPerRoundResources() {
        agentsLeft = totalAgents
        troopsInConflict = 0
        additionalMilitaryStrength = 0
        convictionPoints = 0
    }

    // troops
    suspend fun optionallyPlaceTroopsIntoConflict(amountFromGarrison: Int, amountExtra: Int = 0) {
        troopsInGarrison -= amountFromGarrison
        val amount = amountFromGarrison + amountExtra
        val chosenAmount = connection.requestOptionallyPlaceTroopsIntoConflict(amount)
        troopsInGarrison += amount - chosenAmount
        troopsInConflict += chosenAmount
    }

    // deck

    var deck = mutableListOf(
        AgentCard.CONVINCING_ARGUMENT, AgentCard.CONVINCING_ARGUMENT,
        AgentCard.DAGGER, AgentCard.DAGGER,
        AgentCard.DIPLOMACY,
        AgentCard.DUNE, AgentCard.DUNE,
        AgentCard.MILITARY_INTELLIGENCE,
        AgentCard.SEARCH_FOR_ALLIES,
        AgentCard.SIGNET_RING,
    )
    var hand = mutableListOf<AgentCard>()
    var cardsPlayedThisRound = mutableListOf<AgentCard>()
    var discardedCards = mutableListOf<AgentCard>()

    init {
        //TODO tmp
        drawCardsFromDeck(5)
    }

    private fun consumeFromDeck(amount: Int): List<AgentCard> {
        return mutableListOf<AgentCard>().apply {
            repeat(amount) {
                if (deck.isEmpty()) {
                    deck.addAll(discardedCards.shuffled())
                    discardedCards.clear()
                }
                add(deck.removeFirst())
            }
        }
    }

    fun drawCardsFromDeck(amount: Int) {
        hand.addAll(consumeFromDeck(amount))
    }

    fun discardCardFromHand(card: AgentCard) {
        hand.remove(card)
        discardedCards.add(card)
    }
    fun discardHand() {
        discardedCards.addAll(hand)
        hand.clear()
    }

    fun discardPlayedCards() {
        cardsPlayedThisRound.clear()
    }

    fun destroyCardFromHand(card: AgentCard) {
        hand.remove(card)
    }

    // intrigue cards

    val intrigueCards = mutableListOf<IntrigueCard>()

    fun drawIntrigueCards(amount: Int) {
        repeat(amount) {
            intrigueCards.add(game.drawIntrigueCard())
        }
    }

    init {
        //TODO tmp
        drawIntrigueCards(6)
    }

    // influence

    val influenceLevels = mutableMapOf(
        Faction.IMPERATOR to 0,
        Faction.SPACING_GUILD to 0,
        Faction.BENE_GESSERIT to 0,
        Faction.FREMEN to 0,
    )

    fun getInfluenceLevel(faction: Faction) = influenceLevels[faction] ?: 0

    fun raiseInfluenceLevel(faction: Faction, raiseBy: Int) {
        setInfluenceLevel(faction, getInfluenceLevel(faction) + raiseBy)
    }

    suspend fun raiseInfluenceLevelWithAny(raiseBy: Int) {
        val faction = connection.requestInfluenceWithAny()
        raiseInfluenceLevel(faction, raiseBy)
    }

    fun setInfluenceLevel(faction: Faction, influenceLevel: Int) {
        val formerInfluenceLevel = getInfluenceLevel(faction)
        influenceLevels[faction] = influenceLevel

        if (formerInfluenceLevel < 2 && influenceLevel >= 2) victoryPoints += 1
        if (formerInfluenceLevel >= 2 && influenceLevel < 2) victoryPoints -= 1

        if (formerInfluenceLevel < 4 && influenceLevel >= 4) faction.grantLevel4InfluenceEffect(this)

        if (influenceLevel >= 4 && influenceLevels.count { it.value >= influenceLevel } == 1) {
            game.allies[faction].let {
                if (it != null) it.victoryPoints -= 1
            }
            game.allies[faction] = this
            victoryPoints += 1
        }
    }
}

enum class Leader(
    val title: String,
    val signetRingAction: (player: Player) -> Unit
) {
    ATREIDES_PAUL("Paul Atreides", { player -> player.drawCardsFromDeck(1) }),
    HARKONNEN_BEAST("Glossu \"Beast\" Rabban", { player ->
        player.troopsInGarrison += 1
        if (player.game.allies.count { it.value === player } >= 1) player.troopsInGarrison += 1
        //TODO check if they can be placed into conflict (see also TODO in Player)
    }),
    THORVALD_MEMNO("Count Memnon Thorvald", { player -> player.spice += 1 }),
    RICHESE_ILBAN("Count Ilban Richese", { player -> player.solari += 1 }),
}
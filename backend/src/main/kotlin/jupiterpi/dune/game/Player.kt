package jupiterpi.dune.game

class Player(
    val game: Game,
    val name: String,
    val color: Color,
    val leader: Leader,
) {
    enum class Color(val rgb: Int) {
        RED(0xF23838),
        GREEN(0x33A650),
        BLUE(0x448FF2),
        YELLOW(0xF2B705),
    }

    // basic resources

    var solari = 0
    var spice = 0
    var water = 0

    var totalAgents = 2
    var troopsInGarrison = 0

    var convictionPoints = 0
    var victoryPoints = 1

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

    fun raiseInfluenceLevel(faction: Faction, raiseBy: Int) = setInfluenceLevel(faction, getInfluenceLevel(faction) + raiseBy)

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
        //TODO check for faction alliance -> grants 2 instead
        player.troopsInGarrison += 1
    }),
    THORVALD_MEMNO("Count Memnon Thorvald", { player -> player.spice += 1 }),
    RICHESE_ILBAN("Count Ilban Richese", { player -> player.solari += 1 }),
}
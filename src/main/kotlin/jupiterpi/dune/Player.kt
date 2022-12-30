package jupiterpi.dune

class Player(
    val game: Game,
    val name: String,
    val color: Color,
    val leader: Leader,
) {
    enum class Color(val rgb: Int) {
        RED(0xFF0000),
        GREEN(0x00FF00),
        BLUE(0x0000FF),
        YELLOW(0xFFFF00),
    }

    var solari = 0
    var spice = 0
    var water = 0

    var totalAgents = 2
    var troopsInGarrison = 0

    var convictionPoints = 0
    var victoryPoints = 1

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

    val intrigueCards = mutableListOf<IntrigueCard>()

    fun drawIntrigueCards(amount: Int) {
        repeat(amount) {
            intrigueCards.add(game.drawIntrigueCard())
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
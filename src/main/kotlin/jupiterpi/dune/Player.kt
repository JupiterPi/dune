package jupiterpi.dune

const val maxUnusedControlMarkers = 3
const val maxTotalAgents = 3

const val totalTroops = 16

class Player(
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

    var unusedControlMarkers = 3
    var totalAgents = 2
    var troopsInGarrison = 0

    var convictionPoints = 0
    var victoryPoints = 1

    var deck: MutableList<AgentCard> = mutableListOf(
        AgentCard.CONVINCING_ARGUMENT, AgentCard.CONVINCING_ARGUMENT,
        AgentCard.DAGGER, AgentCard.DAGGER,
        AgentCard.DIPLOMACY,
        AgentCard.DUNE, AgentCard.DUNE,
        AgentCard.MILITARY_INTELLIGENCE,
        AgentCard.SEARCH_FOR_ALLIES,
        AgentCard.SIGNET_RING,
    )
    var hand: MutableList<AgentCard> = mutableListOf()
    var discardedCards: MutableList<AgentCard> = mutableListOf()

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
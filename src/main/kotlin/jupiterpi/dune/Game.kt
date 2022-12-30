package jupiterpi.dune

class Game(
    val players: List<Player>,
) {
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

    private val intrigueCardStack = mutableListOf<IntrigueCard>()

    init {
        intrigueCardStack.addAll(IntrigueCard.values().flatMap {
                card -> generateSequence { card }.take(card.amountInGame).toList()
        })
    }

    fun drawIntrigueCard(): IntrigueCard = intrigueCardStack.removeFirst()
}
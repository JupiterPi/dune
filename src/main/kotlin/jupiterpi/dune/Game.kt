package jupiterpi.dune

class Game(
    val players: List<Player>,
) {
    private val conflictCardStack = mutableListOf<ConflictCard>()
    lateinit var activeConflictCard: ConflictCard
        private set

    init {
        val conflictCards = ConflictCard.values()
        for (level in 1..3) {
            val levelCards = conflictCards.filter { it.level == level }
            val shuffledLevelCards = levelCards.shuffled()
            conflictCardStack.addAll(shuffledLevelCards)
        }
        drawNextConflictCard()
    }

    private fun drawNextConflictCard() {
        activeConflictCard = conflictCardStack.removeFirst()
    }
}
package jupiterpi.dune

enum class Faction(
    val title: String,
    private val grantLevel4InfluenceEffect: (player: Player) -> Unit
) {
    IMPERATOR("Imperator", { player -> player.troopsInGarrison += 2 }),
    SPACING_GUILD("Spacing Guild", { player -> player.solari += 3 }),
    BENE_GESSERIT("Bene Gesserit", { player -> player.drawIntrigueCards(1) }),
    FREMEN("Fremen", { player -> player.water += 1 });

    private val influenceLevels = mutableMapOf<Player, Int>()
    var ally: Player? = null
    fun getInfluenceLevel(player: Player): Int {
        return influenceLevels[player] ?: 0
    }
    fun raiseInfluenceLevel(player: Player, levels: Int) {
        setInfluenceLevel(player, getInfluenceLevel(player) + levels)
    }
    fun setInfluenceLevel(player: Player, influenceLevel: Int) {
        val formerInfluenceLevel = getInfluenceLevel(player)
        influenceLevels[player] = influenceLevel

        if (formerInfluenceLevel < 2 && influenceLevel >= 2) player.victoryPoints += 1
        if (formerInfluenceLevel >= 2 && influenceLevel < 2) player.victoryPoints -= 1

        if (formerInfluenceLevel < 4 && influenceLevel >= 4) grantLevel4InfluenceEffect(player)

        if (influenceLevel >= 4 && influenceLevels.count { it.value >= influenceLevel } == 1) {
            ally.let {
                if (it != null) it.victoryPoints -= 1
            }
            ally = player
            player.victoryPoints += 1
        }
    }
}
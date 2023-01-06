package jupiterpi.dune.game

enum class Faction(
    val title: String,
    val grantLevel4InfluenceEffect: (player: Player) -> Unit
) {
    IMPERATOR("Imperator", { player -> player.troopsInGarrison += 2 }),
    SPACING_GUILD("Spacing Guild", { player -> player.solari += 3 }),
    BENE_GESSERIT("Bene Gesserit", { player -> player.drawIntrigueCards(1) }),
    FREMEN("Fremen", { player -> player.water += 1 }),
}
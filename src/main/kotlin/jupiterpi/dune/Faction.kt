package jupiterpi.dune

enum class Faction(
    val title: String,
    val agentSymbol: AgentSymbol,
) {
    IMPERATOR("Imperator", AgentSymbol.IMPERATOR),
    SPACING_GUILD("Spacing Guild", AgentSymbol.SPACING_GUILD),
    BENE_GESSERIT("Bene Gesserit", AgentSymbol.BENE_GESSERIT),
    FREMEN("Fremen", AgentSymbol.FREMEN),
}
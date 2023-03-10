package jupiterpi.dune.game

enum class AgentCard(
    val title: String,
    val amountAvailableToBuy: Int,
    val convictionCost: Int,
    val agentSymbols: List<AgentSymbol>,
    val immediateEffect: (player: Player) -> Unit,
    val uncoverEffect: (player: Player) -> Unit,
) {
    CONVINCING_ARGUMENT(
        "Convincing Argument", 0, 0,
        listOf(),
        {}, { player -> player.convictionPoints += 2 }
    ),
    DAGGER(
        "Dagger", 0, 0,
        listOf(AgentSymbol.LANDSRAAD),
        {}, { player -> player.additionalMilitaryStrength += 1 }
    ),
    DIPLOMACY(
        "Diplomacy", 0, 0,
        listOf(AgentSymbol.IMPERATOR, AgentSymbol.SPACING_GUILD, AgentSymbol.BENE_GESSERIT, AgentSymbol.FREMEN),
        {}, { player -> player.convictionPoints += 1 }
    ),
    DUNE(
        "Dune", 0, 0,
        listOf(AgentSymbol.SPICE),
        {}, { player -> player.convictionPoints += 1 }
    ),
    MILITARY_INTELLIGENCE(
        "Military Intelligence", 0, 0,
        listOf(AgentSymbol.CITY),
        {}, { player -> player.convictionPoints += 1 }
    ),
    SEARCH_FOR_ALLIES(
        "Search for Allies", 0, 0,
        listOf(AgentSymbol.IMPERATOR, AgentSymbol.SPACING_GUILD, AgentSymbol.BENE_GESSERIT, AgentSymbol.FREMEN),
        { player -> player.destroyCardFromHand(SEARCH_FOR_ALLIES) }, {}
    ),
    SIGNET_RING(
        "Signet Ring", 0, 0,
        listOf(AgentSymbol.LANDSRAAD, AgentSymbol.CITY, AgentSymbol.SPICE),
        { player -> player.leader.signetRingAction(player) }, { player -> player.convictionPoints += 1 }
    ),


    FOLD_SPACE(
        "Fold Space", 0, 0,
        AgentSymbol.values().toList(),
        { player ->
            player.drawCardsFromDeck(1)
            player.destroyCardFromHand(FOLD_SPACE)
        }, {}
    ),
    //TODO dummy for the sake of IntrigueCard.SKIM_MARKET
    THE_SPICE_MUST_FLOW(
        "The Spice Must Flow", 0, 0, listOf(), {}, {}
    )
}
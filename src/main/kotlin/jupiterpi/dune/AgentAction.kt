package jupiterpi.dune

enum class AgentAction(
    val title: String,
    val symbol: AgentSymbol,
    val faction: Faction?,
    private val checkUsabilityForPlayer: (player: Player) -> Boolean,
    val conflictAction: Boolean,
    private val applyEffectForPlayer: (player: Player) -> Unit,
) {
    CONSPIRACY(
        "Conspiracy", AgentSymbol.IMPERATOR, Faction.IMPERATOR,
        { player -> player.spice >= 4 }, false, { player ->
            player.spice -= 4
            player.solari += 2
            player.troopsInGarrison += 2
            //TODO grant 1 intrigue card
        }
    ),
    WEALTH(
        "Wealth", AgentSymbol.IMPERATOR, Faction.IMPERATOR,
        { player -> true }, false, { player -> player.solari += 2 }
    ),


    HEIGHLINER(
        "Heighliner", AgentSymbol.SPACING_GUILD, Faction.SPACING_GUILD,
        { player -> player.spice >= 6 }, true, { player ->
            player.spice -= 6
            player.troopsInGarrison += 5 //TODO troops could also be placed in conflict pool
            player.water += 2
        }
    ),
    FOLD_SPACE(
        "Fold Space", AgentSymbol.SPACING_GUILD, Faction.SPACING_GUILD,
        { player -> true }, false, { player -> {
            //TODO grant Fold Space card
        } }
    ),


    SELECTIVE_BREEDING(
        "Selective Breeding", AgentSymbol.BENE_GESSERIT, Faction.BENE_GESSERIT,
        { player -> player.spice >= 2 }, false, { player ->
            player.spice -= 2
            //TODO remove one card
            player.drawCardsFromDeck(2)
        }
    ),
    SECRETS(
        "Secrets", AgentSymbol.BENE_GESSERIT, Faction.BENE_GESSERIT,
        { player -> true }, false, { player ->
            //TODO grant 1 intrigue card
            //TODO check other players for 4 intrigue cards...
        }
    ),


    TOUGH_WARRIORS(
        "Tough Warriors", AgentSymbol.FREMEN, Faction.FREMEN,
        { player -> player.water >= 1 }, true, { player ->
            player.water -= 1
            player.troopsInGarrison += 2 //TODO troops could also be placed in conflict pool
        }
    ),
    STILLSUIT(
        "Stillsuit", AgentSymbol.FREMEN, Faction.FREMEN,
        { player -> true }, true, { player -> player.water += 1 }
    ),


    HIGH_COUNCIL(
        "High Council", AgentSymbol.LANDSRAAD, null,
        { player ->
            //TODO check that player hasn't done this yet
            player.solari >= 5
        }, false, { player ->
            player.solari -= 5
            //TODO grant seat in high council
        }
    ),
    SPEAKER_HALL(
        "Speaker Hall", AgentSymbol.LANDSRAAD, null,
        { player -> true }, false, { player ->
            player.troopsInGarrison += 1
            player.convictionPoints += 1
        }
    ),
    MENTAT(
        "Mentat", AgentSymbol.LANDSRAAD, null,
        { player -> player.solari >= 2 }, false, { player ->
            player.solari -= 2
            player.drawCardsFromDeck(1)
            //TODO grant mentat
        }
    ),
    COLLECT_TROUPS(
        "Collect Troups", AgentSymbol.LANDSRAAD, null,
        { player -> player.solari >= 4 }, false, { player ->
            player.solari -= 4
            player.troopsInGarrison += 4
        }
    ),
    SWORD_MASTER(
        "Sword Master", AgentSymbol.LANDSRAAD, null,
        { player -> player.solari >= 8 }, false, { player ->
            player.solari -= 8
            //TODO grant 3rd agent
        }
    ),


    SIETCH_TABR(
        "Sietch Tabr", AgentSymbol.CITY, null,
        { player ->
            Faction.FREMEN.getInfluenceLevel(player) >= 2
        }, true, { player ->
            player.troopsInGarrison += 1 //TODO troops could also be placed in conflict pool
            player.water += 1
        }
    ),
    RESEARCH_CENTER(
        "Research Center", AgentSymbol.CITY, null,
        { player -> player.water >= 2 }, true, { player ->
            player.water -= 2
            player.drawCardsFromDeck(3)
        }
    ),
    CARTHAG(
        "Carthag", AgentSymbol.CITY, null,
        { player -> true }, true, { player ->
            player.troopsInGarrison += 1 //TODO troops could also be placed in conflict pool
            //TODO grant 1 intrigue card
            AgentCardControl.CARTHAG.grantControlBonus()
        }
    ),
    ARRAKEEN(
        "Arrakeen", AgentSymbol.CITY, null,
        { player -> true }, true, { player ->
            player.troopsInGarrison += 1 //TODO troops could also be placed in conflict pool
            player.drawCardsFromDeck(1)
            jupiterpi.dune.AgentCardControl.ARRAKEEN.grantControlBonus()
        }
    ),


    GREAT_PLAIN(
        "Great Plain", AgentSymbol.SPICE, null,
        { player -> player.water >= 2 }, true, { player ->
            player.water -= 2
            player.spice += 3
            //TODO grant additional aggregated spice
        }
    ),
    HAGGA_BASIN(
        "Hagga Basin", AgentSymbol.SPICE, null,
        { player -> player.water >= 1 }, true, { player ->
            player.water -= 1
            player.spice += 2
            //TODO grant additional aggregated spice
        }
    ),
    IMPERIAL_BASIN(
        "Imperial Basin", AgentSymbol.SPICE, null,
        { player -> true }, true, { player ->
            player.spice += 1
            //TODO grant additional aggregated spice
            jupiterpi.dune.AgentCardControl.IMPERIAL_BASIN.grantControlBonus()
        }
    ),
    SELL_SPICE(
        "Sell Spice", AgentSymbol.SPICE, null,
        //TODO better system to handle multiple options
        { player -> player.spice >= 2 }, false, { player ->
            player.spice -= 2 //TODO handle multiple options
            player.solari += 6
        }
    ),
    MAKE_DEAL(
        "Make Deal", AgentSymbol.SPICE, null,
        { player -> true }, false, { player -> player.solari += 3 }
    );


    fun usableForPlayer(player: Player): Boolean {
        //TODO check if action is blocked
        return checkUsabilityForPlayer(player)
    }

    fun useForPlayer(player: Player) {
        applyEffectForPlayer(player)
        //TODO players can choose to play troops on conflict actions
        faction?.setInfluenceLevel(player, faction.getInfluenceLevel(player))
    }
}
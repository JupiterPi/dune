package jupiterpi.dune

enum class AgentAction(
    val title: String,
    val symbol: AgentSymbol,
    val faction: Faction?,
    val usableForPlayer: (player: Player) -> Boolean,
    val conflictAction: Boolean,
    private val applyEffectForPlayer: (player: Player) -> Unit,
) {
    CONSPIRACY(
        "Conspiracy", AgentSymbol.IMPERATOR, Faction.IMPERATOR,
        { player -> player.spice >= 4 }, false, { player ->
            player.spice -= 4
            player.solari += 2
            player.troopsInGarrison += 2
            player.drawIntrigueCards(1)
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
            player.drawIntrigueCards(1)
            player.game.players.forEach {
                if (player.intrigueCards.size >= 4) {
                    val card = it.intrigueCards.random()
                    it.intrigueCards.remove(card)
                    player.intrigueCards.add(card)
                }
            }
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
            !player.game.highCouncilMembers.contains(player) && player.solari >= 5
        }, false, { player ->
            player.solari -= 5
            player.game.highCouncilMembers.add(player)
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
            player.getInfluenceLevel(Faction.FREMEN) >= 2
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
            player.drawIntrigueCards(1)
            player.game.grantControlBonus(AgentActionControl.CARTHAG)
        }
    ),
    ARRAKEEN(
        "Arrakeen", AgentSymbol.CITY, null,
        { player -> true }, true, { player ->
            player.troopsInGarrison += 1 //TODO troops could also be placed in conflict pool
            player.drawCardsFromDeck(1)
            player.game.grantControlBonus(AgentActionControl.ARRAKEEN)
        }
    ),


    GREAT_PLAIN(
        "Great Plain", AgentSymbol.SPICE, null,
        { player -> player.water >= 2 }, true, { player ->
            player.water -= 2
            player.spice += 3
            player.spice += player.game.consumeAggregatedSpice(AgentAction.GREAT_PLAIN)
        }
    ),
    HAGGA_BASIN(
        "Hagga Basin", AgentSymbol.SPICE, null,
        { player -> player.water >= 1 }, true, { player ->
            player.water -= 1
            player.spice += 2
            player.spice += player.game.consumeAggregatedSpice(AgentAction.HAGGA_BASIN)
        }
    ),
    IMPERIAL_BASIN(
        "Imperial Basin", AgentSymbol.SPICE, null,
        { player -> true }, true, { player ->
            player.spice += 1
            //TODO grant additional aggregated spice
            player.spice += player.game.consumeAggregatedSpice(AgentAction.IMPERIAL_BASIN)
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

    fun useForPlayer(player: Player) {
        player.game.blockAgentAction(this)
        applyEffectForPlayer(player)
        //TODO players can choose to play troops on conflict actions
        when (symbol) {
            AgentSymbol.IMPERATOR -> player.raiseInfluenceLevel(Faction.IMPERATOR, 1)
            AgentSymbol.SPACING_GUILD -> player.raiseInfluenceLevel(Faction.SPACING_GUILD, 1)
            AgentSymbol.BENE_GESSERIT -> player.raiseInfluenceLevel(Faction.BENE_GESSERIT, 1)
            AgentSymbol.FREMEN -> player.raiseInfluenceLevel(Faction.FREMEN, 1)
            else -> {}
        }
    }
}
package jupiterpi.dune.game

enum class AgentAction(
    val title: String,
    val symbol: AgentSymbol,
    val faction: Faction?,
    private val usableForPlayer: (player: Player) -> Boolean,
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
            player.optionallyPlaceTroopsIntoConflict(2, amountExtra = 5)
            player.water += 2
        }
    ),
    FOLD_SPACE(
        "Fold Space", AgentSymbol.SPACING_GUILD, Faction.SPACING_GUILD,
        { player -> true }, false, { player ->
            player.discardedCards.add(AgentCard.FOLD_SPACE)
        }
    ),


    SELECTIVE_BREEDING(
        "Selective Breeding", AgentSymbol.BENE_GESSERIT, Faction.BENE_GESSERIT,
        { player -> player.spice >= 2 }, false, { player ->
            player.spice -= 2
            player.destroyCardFromHand(player.game.handler.requestDestroyCardFromHand(player))
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
            player.optionallyPlaceTroopsIntoConflict(2, amountExtra = 2)
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
            player.agentsLeft += 1
        }
    ),
    COLLECT_TROOPS(
        "Collect Troops", AgentSymbol.LANDSRAAD, null,
        { player -> player.solari >= 4 }, false, { player ->
            player.solari -= 4
            player.troopsInGarrison += 4
        }
    ),
    SWORD_MASTER(
        "Sword Master", AgentSymbol.LANDSRAAD, null,
        { player -> player.solari >= 8 }, false, { player ->
            player.solari -= 8
            player.totalAgents += 1
            player.agentsLeft += 1
        }
    ),


    SIETCH_TABR(
        "Sietch Tabr", AgentSymbol.CITY, null,
        { player ->
            player.getInfluenceLevel(Faction.FREMEN) >= 2
        }, true, { player ->
            player.optionallyPlaceTroopsIntoConflict(2, amountExtra = 1)
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
            player.optionallyPlaceTroopsIntoConflict(2, amountExtra = 1)
            player.drawIntrigueCards(1)
            player.game.grantControlBonus(AgentActionControl.CARTHAG)
        }
    ),
    ARRAKEEN(
        "Arrakeen", AgentSymbol.CITY, null,
        { player -> true }, true, { player ->
            player.optionallyPlaceTroopsIntoConflict(2, amountExtra = 1)
            player.drawCardsFromDeck(1)
            player.game.grantControlBonus(AgentActionControl.ARRAKEEN)
        }
    ),


    GREAT_PLAIN(
        "Great Plain", AgentSymbol.SPICE, null,
        { player -> player.water >= 2 }, true, { player ->
            player.water -= 2
            player.spice += 3
            player.spice += player.game.consumeAggregatedSpice(GREAT_PLAIN)
        }
    ),
    HAGGA_BASIN(
        "Hagga Basin", AgentSymbol.SPICE, null,
        { player -> player.water >= 1 }, true, { player ->
            player.water -= 1
            player.spice += 2
            player.spice += player.game.consumeAggregatedSpice(HAGGA_BASIN)
        }
    ),
    IMPERIAL_BASIN(
        "Imperial Basin", AgentSymbol.SPICE, null,
        { player -> true }, true, { player ->
            player.spice += 1
            player.spice += player.game.consumeAggregatedSpice(IMPERIAL_BASIN)
        }
    ),
    SELL_SPICE(
        "Sell Spice", AgentSymbol.SPICE, null,
        { player -> player.spice >= 2 }, false, { player ->
            val amount = player.game.handler.requestSellSpiceAmount(player, kotlin.math.min(player.spice, 5)).coerceIn(2..5)
            player.spice -= amount
            player.solari += when (amount) {
                2 -> 6
                3 -> 8
                4 -> 10
                5 -> 12
                else -> 0
            }
        }
    ),
    MAKE_DEAL(
        "Make Deal", AgentSymbol.SPICE, null,
        { player -> true }, false, { player -> player.solari += 3 }
    );

    fun isUsableForPlayer(player: Player, agentSymbolsAvailable: List<AgentSymbol>): Boolean {
        return agentSymbolsAvailable.contains(this.symbol) and this.usableForPlayer(player)
    }

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
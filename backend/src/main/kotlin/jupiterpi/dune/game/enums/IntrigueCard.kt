package jupiterpi.dune.game.enums

import jupiterpi.dune.game.Player

enum class IntrigueCard(
    val title: String,
    val amountInGame: Int,
    val type: List<Type>,
    val grantEffect: (player: Player) -> Unit,
) {
    //TODO THIS IS JUST A SELECTION; OTHER CARDS ARE YET TO BE TYPED DOWN

    LUCK(
        "Luck", 1, listOf(Type.PLOT), { player ->
            player.solari += 2
        }
    ),
    IMPERATORS_FAVORITE(
        "Imperator's Favorite", 1, listOf(Type.PLOT), { player ->
            player.raiseInfluenceLevel(Faction.IMPERATOR, 1)
        }
    ),
    WATER_UNION(
        "Water Salesmen's Union", 1, listOf(Type.PLOT), { player ->
            player.water += 1
        }
    ),
    // ...


    AMBUSH(
        "Ambush", 2, listOf(Type.CONFLICT), { player -> player.additionalMilitaryStrength += 4 }
    ),
    // ...


    DECIDER(
        "Decider", 1, listOf(Type.CONFLICT, Type.FINALE), { player ->
            //TODO (for fight): grant 2 military strength
            //TODO (for finale): grant 10 spice
        }
    ),
    // ...


    SKIM_MARKET(
        "Skim The Market", 1, listOf(Type.FINALE), { player ->
            val totalCards = mutableMapOf<Player, MutableList<AgentCard>>()
            player.game.players.forEach {
                totalCards[it] = mutableListOf()
                totalCards[it]!!.addAll(player.deck)
                totalCards[it]!!.addAll(player.hand)
                totalCards[it]!!.addAll(player.discardedCards)
            }
            val amounts = totalCards.mapValues { it.value.count { it === AgentCard.THE_SPICE_MUST_FLOW } }
            if (amounts[player]!! >= 1) player.victoryPoints += 1
            if (amounts[player]!! > amounts.toMutableMap().apply { remove(player) }.maxOf { it.value } ) player.victoryPoints += 1
        }
    );
    // ...

    enum class Type(
        val title: String,
    ) {
        PLOT("Plot"),
        CONFLICT("Fight"),
        FINALE("Finale"),
    }
}
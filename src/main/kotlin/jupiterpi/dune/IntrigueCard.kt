package jupiterpi.dune

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
        "Ambush", 2, listOf(Type.FIGHT), { player ->
            //TODO grant 4 military strength
        }
    ),
    // ...


    DECIDER(
        "Decider", 1, listOf(Type.FIGHT, Type.FINALE), { player ->
            //TODO (for fight): grant 2 military strength
            //TODO (for finale): grant 10 spice
        }
    ),
    // ...


    SKIM_MARKET(
        "Skim The Market", 1, listOf(Type.FINALE), { player ->
            //TODO (see card)
        }
    );
    // ...

    enum class Type(
        val title: String,
    ) {
        PLOT("Plot"),
        FIGHT("Fight"),
        FINALE("Finale"),
    }
}
package jupiterpi.dune.game

enum class ConflictCard(
    val title: String,
    val level: Int,
    val grantRewards: (first: Player, second: Player, third: Player) -> Unit,
) {
    I_FIGHT_1("Fight (I)", 1, { first, second, third ->
        //TODO (for first) add influence with any
        first.solari += 2
        second.solari += 3
        third.solari += 2
    }),
    I_FIGHT_2("Fight (I)", 1, { first, second, third ->
        //TODO (for first) add influence with any
        first.spice += 1
        second.spice += 2
        third.spice += 1
    }),
    I_FIGHT_3("Fight (I)", 1, { first, second, third ->
        first.victoryPoints += 1
        second.drawIntrigueCards(1)
        second.solari += 2
        third.solari += 2
    }),
    I_FIGHT_4("Fight (I)", 1, { first, second, third ->
        first.victoryPoints += 1
        second.water += 1
        third.spice += 1
    }),


    II_ATTACK_ON_GUILD_BANK("Attack On The Guild Bank (II)", 2, { first, second, third ->
        first.solari += 6
        second.solari += 4
        third.solari += 2
    }),
    II_SEE_THROUGH_CHAOS("See Through The Chaos (II)", 2, { first, second, third ->
        //TODO (for first) add mentat
        first.drawIntrigueCards(1)
        first.solari += 2
        second.drawIntrigueCards(1)
        second.solari += 2
        third.solari += 2
    }),
    II_LOOT_SUPPLIES("Loot Supplies (II)", 2, { first, second, third ->
        first.drawIntrigueCards(1)
        first.spice += 3
        second.spice += 2
        third.spice += 1
    }),
    II_IN_THE_NIGHT("In The Night (II)", 2, { first, second, third ->
        //TODO (for first) add 1 influence for any
        first.drawIntrigueCards(2)
        second.drawIntrigueCards(1)
        second.spice += 1
        //TODO (for third) add 1 intrigue card **OR** 1 spice
    }),
    II_DARK_DOINGS("Dark Doings (II)", 2, { first, second, third ->
        //TODO (for first) choose influence with two of: imperator, spacing guild, bene gesserit, fremen
        second.water += 1
        second.solari += 2
        third.water += 1
    }),
    II_FORCE_OF_DESERT("Force Of The Desert (II)", 2, { first, second, third ->
        first.victoryPoints += 1
        first.water += 1
        second.water += 1
        second.spice += 1
        third.spice += 1
    }),
    II_GRUESOME_INTENTIONS("Gruesome Intentions (II)", 2, { first, second, third ->
        first.victoryPoints += 1
        //TODO (for first) destroy one card
        second.water += 1
        second.spice += 1
        third.spice += 1
    }),
    II_SIEGE_OF_ARRAKEEN("Siege Of Arrakeen (II)", 2, { first, second, third ->
        first.victoryPoints += 1
        first.game.control[AgentActionControl.ARRAKEEN] = first
        second.solari += 4
        third.solari += 2
    }),
    II_SIEGE_OF_CARTHAG("Siege Of Carthag (II)", 2, { first, second, third ->
        first.victoryPoints += 1
        first.game.control[AgentActionControl.CARTHAG] = first
        second.drawIntrigueCards(1)
        second.spice += 1
        third.spice += 1
    }),
    II_SECURE_IMPERIAL_BASIN("Secure The Imperial Basin (II)", 2, { first, second, third ->
        first.victoryPoints += 1
        first.game.control[AgentActionControl.IMPERIAL_BASIN] = first
        second.water += 2
        third.water += 1
    }),


    III_GREAT_VISION("Great Vision (III)", 3, { first, second, third ->
        //TODO (for first) grant 2 influence with any
        first.drawIntrigueCards(1)
        second.drawIntrigueCards(1)
        second.spice += 3
        third.spice += 3
    }),
    III_BATTLE_OF_ARRAKEEN("Battle Of Arrakeen (III)", 3, { first, second, third ->
        first.victoryPoints += 2
        first.game.control[AgentActionControl.ARRAKEEN] = first
        //TODO (for second) choose 2 of: 1 intrigue card, 2 spice, 3 solari
        third.drawIntrigueCards(1)
        third.solari += 2
    }),
    III_BATTLE_OF_CARTHAG("Battle Of Carthag (III)", 3, { first, second, third ->
        first.victoryPoints += 2
        first.game.control[AgentActionControl.CARTHAG] = first
        second.drawIntrigueCards(1)
        second.spice += 3
        third.spice += 3
    }),
    III_BATTLE_OF_IMPERIAL_BASIN("Battle Of Imperial Basin (III)", 3, { first, second, third ->
        first.victoryPoints += 2
        first.game.control[AgentActionControl.IMPERIAL_BASIN] = first
        second.spice += 5
        third.spice += 3
    }),
}
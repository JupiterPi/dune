package jupiterpi.dune.game.enums

import jupiterpi.dune.game.Game
import jupiterpi.dune.game.Player

enum class ConflictCard(
    val title: String,
    val level: Int,
    val grantImmediateEffects: (game: Game) -> Unit,
    val grantRewards: suspend (first: Player, second: Player, third: Player) -> Unit,
) {
    I_FIGHT_1("Fight (I)", 1, {}, { first, second, third ->
        first.raiseInfluenceLevelWithAny(1)
        first.solari += 2
        second.solari += 3
        third.solari += 2
    }),
    I_FIGHT_2("Fight (I)", 1, {}, { first, second, third ->
        first.raiseInfluenceLevelWithAny(1)
        first.spice += 1
        second.spice += 2
        third.spice += 1
    }),
    I_FIGHT_3("Fight (I)", 1, {}, { first, second, third ->
        first.victoryPoints += 1
        second.drawIntrigueCards(1)
        second.solari += 2
        third.solari += 2
    }),
    I_FIGHT_4("Fight (I)", 1, {}, { first, second, third ->
        first.victoryPoints += 1
        second.water += 1
        third.spice += 1
    }),


    II_ATTACK_ON_GUILD_BANK("Attack On The Guild Bank (II)", 2, {}, { first, second, third ->
        first.solari += 6
        second.solari += 4
        third.solari += 2
    }),
    II_SEE_THROUGH_CHAOS("See Through The Chaos (II)", 2, {}, { first, second, third ->
        //TODO (for first) add mentat
        first.drawIntrigueCards(1)
        first.solari += 2
        second.drawIntrigueCards(1)
        second.solari += 2
        third.solari += 2
    }),
    II_LOOT_SUPPLIES("Loot Supplies (II)", 2, {}, { first, second, third ->
        first.drawIntrigueCards(1)
        first.spice += 3
        second.spice += 2
        third.spice += 1
    }),
    II_IN_THE_NIGHT("In The Night (II)", 2, {}, { first, second, third ->
        first.raiseInfluenceLevelWithAny(1)
        first.drawIntrigueCards(2)
        second.drawIntrigueCards(1)
        second.spice += 1
        when (third.connection.requestChoice("Choose 1", listOf(
            "1 Intrigue Card",
            "1 Spice",
        ))) {
            0 -> third.drawIntrigueCards(1)
            1 -> third.spice += 1
        }
    }),
    II_DARK_DOINGS("Dark Doings (II)", 2, {}, { first, second, third ->
        first.connection.requestMultipleChoices("Choose 2", listOf(
            "1 Influence with Imperator",
            "1 Influence with Spacing Guild",
            "1 Influence with Bene Gesserit",
            "1 Influence with Fremen",
        ), 2, 2).forEach { when (it) {
            0 -> first.raiseInfluenceLevel(Faction.IMPERATOR, 1)
            1 -> first.raiseInfluenceLevel(Faction.SPACING_GUILD, 1)
            2 -> first.raiseInfluenceLevel(Faction.BENE_GESSERIT, 1)
            3 -> first.raiseInfluenceLevel(Faction.FREMEN, 1)
        } }

        second.water += 1
        second.solari += 2
        third.water += 1
    }),
    II_FORCE_OF_DESERT("Force Of The Desert (II)", 2, {}, { first, second, third ->
        first.victoryPoints += 1
        first.water += 1
        second.water += 1
        second.spice += 1
        third.spice += 1
    }),
    II_GRUESOME_INTENTIONS("Gruesome Intentions (II)", 2, {}, { first, second, third ->
        first.victoryPoints += 1
        first.destroyCardFromHand(first.connection.requestDestroyCardFromHand())
        second.water += 1
        second.spice += 1
        third.spice += 1
    }),
    II_SIEGE_OF_ARRAKEEN("Siege Of Arrakeen (II)", 2, { game ->
        val controller = game.control[AgentActionControl.ARRAKEEN]
        if (controller != null) controller.troopsInConflict += 1
    }, { first, second, third ->
        first.victoryPoints += 1
        first.game.control[AgentActionControl.ARRAKEEN] = first
        second.solari += 4
        third.solari += 2
    }),
    II_SIEGE_OF_CARTHAG("Siege Of Carthag (II)", 2, { game ->
        val controller = game.control[AgentActionControl.CARTHAG]
        if (controller != null) controller.troopsInConflict += 1
    }, { first, second, third ->
        first.victoryPoints += 1
        first.game.control[AgentActionControl.CARTHAG] = first
        second.drawIntrigueCards(1)
        second.spice += 1
        third.spice += 1
    }),
    II_SECURE_IMPERIAL_BASIN("Secure The Imperial Basin (II)", 2, { game ->
        val controller = game.control[AgentActionControl.IMPERIAL_BASIN]
        if (controller != null) controller.troopsInConflict += 1
    }, { first, second, third ->
        first.victoryPoints += 1
        first.game.control[AgentActionControl.IMPERIAL_BASIN] = first
        second.water += 2
        third.water += 1
    }),


    III_GREAT_VISION("Great Vision (III)", 3, {}, { first, second, third ->
        first.raiseInfluenceLevelWithAny(2)
        first.drawIntrigueCards(1)
        second.drawIntrigueCards(1)
        second.spice += 3
        third.spice += 3
    }),
    III_BATTLE_OF_ARRAKEEN("Battle Of Arrakeen (III)", 3, { game ->
        val controller = game.control[AgentActionControl.ARRAKEEN]
        if (controller != null) controller.troopsInConflict += 1
    }, { first, second, third ->
        first.victoryPoints += 2
        first.game.control[AgentActionControl.ARRAKEEN] = first
        second.let { player ->
            player.connection.requestMultipleChoices("Choose 2", listOf(
                "1 Intrigue Card",
                "2 Spice",
                "3 Solari",
            ), 2, 2).forEach { when(it) {
                0 -> player.drawIntrigueCards(1)
                1 -> player.spice += 2
                2 -> player.solari += 3
            } }
        }
        third.drawIntrigueCards(1)
        third.solari += 2
    }),
    III_BATTLE_OF_CARTHAG("Battle Of Carthag (III)", 3, { game ->
        val controller = game.control[AgentActionControl.CARTHAG]
        if (controller != null) controller.troopsInConflict += 1
    }, { first, second, third ->
        first.victoryPoints += 2
        first.game.control[AgentActionControl.CARTHAG] = first
        second.drawIntrigueCards(1)
        second.spice += 3
        third.spice += 3
    }),
    III_BATTLE_OF_IMPERIAL_BASIN("Battle Of The Imperial Basin (III)", 3, { game ->
        val controller = game.control[AgentActionControl.IMPERIAL_BASIN]
        if (controller != null) controller.troopsInConflict += 1
    }, { first, second, third ->
        first.victoryPoints += 2
        first.game.control[AgentActionControl.IMPERIAL_BASIN] = first
        second.spice += 5
        third.spice += 3
    }),
}
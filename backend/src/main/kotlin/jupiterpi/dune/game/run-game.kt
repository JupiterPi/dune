@file:Suppress("FunctionName")

package jupiterpi.dune.game

import jupiterpi.dune.game.Game.PlayerActionType
import jupiterpi.dune.game.enums.AgentActionControl
import jupiterpi.dune.game.enums.AgentCard
import jupiterpi.dune.game.enums.IntrigueCard

fun Game.run_roundStart() {
    drawNextConflictCard()
    refreshAvailableAgentActions()

    players.forEach {
        it.resetPerRoundResources()
        it.drawCardsFromDeck(5)
    }
}

suspend fun Game.run_players() {
    val activePlayers = players.toMutableList()
    while (activePlayers.isNotEmpty()) {
        val playersToRemove = mutableListOf<Player>()
        activePlayers.forEach { player ->

            while (true) {
                val playerActionType = player.connection.requestPlayerActionType()
                when (playerActionType) {
                    PlayerActionType.PLOT_INTRIGUE_CARD -> {

                        val intrigueCard = player.connection.requestIntrigueCard(IntrigueCard.Type.PLOT)
                        intrigueCard.grantEffect(player)

                    }
                    PlayerActionType.AGENT_ACTION -> {

                        val (agentCard, agentAction) = player.connection.requestAgentCardAndAction(mapOf(
                            *player.hand.map { card -> card to availableAgentActions.filter { it.isUsableForPlayer(player, card.agentSymbols) } }.toTypedArray()
                        ))
                        player.agentsLeft -= 1
                        player.discardCardFromHand(agentCard)

                        val firstEffect = player.connection.requestChoice(
                            "Choose the first effect",
                            listOf("Agent Card effect", "Agent Action effect")
                        )
                        listOf(
                            {
                                player.cardsPlayedThisRound += agentCard
                                agentCard.immediateEffect(player)
                            },
                            suspend {
                                agentAction.useForPlayer(player)
                                AgentActionControl.get(agentAction)?.let { grantControlBonus(it) }
                            }
                        ).let { if (firstEffect == 1) it.reversed() else it }.forEach { it.invoke() }

                        //TODO troops

                    }
                    PlayerActionType.UNCOVER_ACTION -> {

                        conditionallyGrantHighCouncilBenefits(player)

                        fun uncoverCards(cards: List<AgentCard>) {
                            cards.forEach { it.uncoverEffect(player) }
                            player.cardsPlayedThisRound.addAll(cards)
                            cards.forEach { player.discardCardFromHand(it) }
                        }
                        when (player.connection.requestChoice(
                            "Choose an action",
                            listOf("Play Plot Intrigue Card", "Buy Agent Card", "Grant Uncover effect", "Grant all Uncover effects")
                        )) {
                            0 -> {
                                val intrigueCard = player.connection.requestIntrigueCard(IntrigueCard.Type.PLOT)
                                intrigueCard.grantEffect(player)
                            }
                            1 -> {
                                val card = player.connection.requestAgentCardFromMarket()
                                player.convictionPoints -= card.convictionCost
                                consumeFromMarket(card)
                                player.discardedCards += card
                            }
                            2 -> {
                                val card = player.connection.requestAgentCard()
                                uncoverCards(listOf(card))
                            }
                            3 -> {
                                uncoverCards(player.hand)
                            }
                            else -> { throw Exception("invalid choice") }
                        }
                        playersToRemove += player

                    }
                }

                if (playerActionType != PlayerActionType.PLOT_INTRIGUE_CARD) break
            }

        }
        activePlayers.removeAll(playersToRemove)
    }
}

suspend fun Game.run_conflict() {
    while (true) {
        var again = false
        players.forEach { player ->
            val result = player.connection.requestChoice(
                "Play an Intrigue Card?",
                listOf("Play Conflict Intrigue Card", "Pass")
            )
            if (result == 0) {
                again = true

                val intrigueCard = player.connection.requestIntrigueCard(IntrigueCard.Type.CONFLICT)
                intrigueCard.grantEffect(player)
            }
        }
        if (!again) break
    }

    //TODO tmp (refine special rules for draws)
    val winners = players.sortedBy { it.totalMilitaryStrength }
    activeConflictCard.grantRewards(winners[0], winners[1], winners[2])

    //TODO "fight won" intrigue cards
}

fun Game.run_finale() {
    //TODO
}
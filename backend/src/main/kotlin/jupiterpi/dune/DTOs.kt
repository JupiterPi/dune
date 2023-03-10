package jupiterpi.dune

import jupiterpi.dune.game.*
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.simp.annotation.SubscribeMapping
import org.springframework.stereotype.Controller

data class GameDTO(
    val players: List<PlayerDTO>,
    val allies: Map<String, String?>,
    val control: Map<String, String?>,
    val highCouncilMembers: List<String>,
    val aggregatedSpice: Map<String, Int>,
) {
    constructor(game: Game) : this(
        game.players.map { PlayerDTO(it) },
        game.allies.map { it.key.name to it.value?.name }.toMap(),
        game.control.map { it.key.name to it.value?.name }.toMap(),
        game.highCouncilMembers.map { it.name },
        game.aggregatedSpice.mapKeys { it.key.name }
    )
}

data class PlayerDTO(
    val name: String,
    val color: String,
    val leader: LeaderDTO,
    val solari: Int,
    val spice: Int,
    val water: Int,
    val militaryStrength: Int,
    val victoryPoints: Int,
    val influenceLevels: Map<String, Int>,
) {
    constructor(player: Player) : this(
        player.name,
        "#" + String.format("%06X", 0xFFFFFF and player.color.rgb),
        LeaderDTO(player.leader),
        player.solari, player.spice, player.water, 0, player.victoryPoints,
        player.influenceLevels.mapKeys { it.key.name }
    )
}

data class LeaderDTO(
    val id: String,
    val name: String,
) {
    constructor(leader: Leader) : this(leader.name, leader.title)
}

data class PlayerGameDTO(
    val hand: List<AgentCardDTO>,
    val intrigueCards: List<IntrigueCardDTO>,
) {
    constructor(player: Player) : this(
        player.hand.map { AgentCardDTO(it) },
        player.intrigueCards.map { IntrigueCardDTO(it) }
    )
}

data class AgentCardDTO(
    val id: String,
    val title: String,
    val agentSymbols: List<String>,
) {
    constructor(agentCard: AgentCard) : this(
        agentCard.name,
        agentCard.title,
        agentCard.agentSymbols.map { it.name }
    )
}

data class IntrigueCardDTO(
    val id: String,
    val title: String,
) {
    constructor(intrigueCard: IntrigueCard) : this(
        intrigueCard.name,
        intrigueCard.title
    )
}
package jupiterpi.dune

import jupiterpi.dune.game.Game
import jupiterpi.dune.game.Leader
import jupiterpi.dune.game.Player
import org.springframework.messaging.simp.annotation.SubscribeMapping
import org.springframework.stereotype.Controller
import java.awt.Color

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

@Controller
class GameController {
    @SubscribeMapping("/game")
    fun getGame() = GameDTO(game)
}
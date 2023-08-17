package jupiterpi.dune.richtext

import jupiterpi.dune.game.Player
import jupiterpi.dune.game.enums.AgentAction
import jupiterpi.dune.game.enums.AgentCard
import jupiterpi.dune.game.enums.IntrigueCard
import kotlinx.serialization.Serializable

@Serializable
sealed interface RichTextComponent {
    val type: String
}

@Serializable
class RichTextContainer private constructor(
    override val type: String,
    val components: List<RichTextComponent>,
) : RichTextComponent {
    constructor(components: List<RichTextComponent>) : this("container", components)
    constructor(vararg components: RichTextComponent) : this("container", components.toList())
}

@Serializable
class TextComponent private constructor(
    override val type: String,
    val text: String,
    val bold: Boolean,
) : RichTextComponent {
    constructor(text: String, bold: Boolean = false) : this("text", text, bold)
}

@Serializable
class PlayerComponent private constructor(
    override val type: String,
    val name: String,
) : RichTextComponent {
    constructor(player: Player) : this("player", player.name)
}

@Serializable
class EnumeratedComponent private constructor(
    override val type: String,
    val id: String,
) : RichTextComponent {
    constructor(agentCard: AgentCard) : this("agent_card", agentCard.name)
    constructor(agentAction: AgentAction) : this("agent_action", agentAction.name)
    constructor(intrigueCard: IntrigueCard) : this("intrigue_card", intrigueCard.name)
}
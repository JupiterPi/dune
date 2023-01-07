package jupiterpi.dune

import jupiterpi.dune.game.Player
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.messaging.simp.annotation.SubscribeMapping
import org.springframework.stereotype.Controller
import java.util.UUID

lateinit var handler: Handler

interface Handler {
    fun refreshGameState()
    fun refreshPlayerGameStates()

    fun requestTest1(player: Player, content: String): String
}
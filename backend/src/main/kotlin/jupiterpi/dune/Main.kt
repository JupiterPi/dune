package jupiterpi.dune

import jupiterpi.dune.game.Game
import jupiterpi.dune.game.Leader
import jupiterpi.dune.game.Player
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer
import kotlin.concurrent.thread

lateinit var game: Game

fun main() {
    runApplication<DuneApplication>()

    thread {
        game = Game(handler)
        game.players.addAll(listOf(
            Player(game, "Player1", Player.Color.RED, Leader.ATREIDES_PAUL),
            Player(game, "Player2", Player.Color.GREEN, Leader.HARKONNEN_BEAST),
            Player(game, "Player3", Player.Color.BLUE, Leader.THORVALD_MEMNO),
            Player(game, "Player4", Player.Color.YELLOW, Leader.RICHESE_ILBAN),
        ))
    }

    thread {
        runBlocking {
            delay(1000 * 5)
            game.start()
        }
    }
}

@SpringBootApplication
class DuneApplication

@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig : WebSocketMessageBrokerConfigurer {
    override fun configureMessageBroker(registry: MessageBrokerRegistry) {
        registry.enableSimpleBroker("/topic")
        registry.setApplicationDestinationPrefixes("/topic")
    }

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS()
    }
}
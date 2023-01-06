package jupiterpi.dune

import jupiterpi.dune.game.Game
import jupiterpi.dune.game.Leader
import jupiterpi.dune.game.Player
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

lateinit var game: Game

fun main() {
    game = Game()
    game.players.addAll(listOf(
        Player(game, "Player 1", Player.Color.RED, Leader.ATREIDES_PAUL),
        Player(game, "Player 2", Player.Color.GREEN, Leader.HARKONNEN_BEAST),
        Player(game, "Player 3", Player.Color.BLUE, Leader.THORVALD_MEMNO),
        Player(game, "Player 4", Player.Color.YELLOW, Leader.RICHESE_ILBAN),
    ))

    runApplication<DuneApplication>()
}

@SpringBootApplication
class DuneApplication

@RestController
class Controller {
    @GetMapping("/test")
    fun test() = "works!"
}

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
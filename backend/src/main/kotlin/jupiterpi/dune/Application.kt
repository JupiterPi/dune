package jupiterpi.dune

import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.websocket.*
import jupiterpi.dune.users.Users
import jupiterpi.dune.users.configureAuth
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Duration

fun main() {
    setupDatabase()
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun setupDatabase() {
    Database.connect("jdbc:h2:file:./build/db", driver = "org.h2.Driver")
    transaction {
        SchemaUtils.create(Users)
    }
}

fun Application.module() {
    val serialization = Json {
        classDiscriminator = "jsonClassDiscriminator"
    }

    install(ContentNegotiation) {
        json(serialization)
    }

    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)

        allowHost("0.0.0.0:4200")
        anyHost()
    }

    configureAuth()

    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
        contentConverter = KotlinxWebsocketSerializationConverter(serialization)
    }

    configureGames()
}

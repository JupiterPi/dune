package jupiterpi.dune

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.serialization.Serializable

@Serializable
data class UserCredentials(
    val name: String,
    var password: String
)

val users = mutableListOf(
    UserCredentials("JupiterPii", "password"),
)
private fun validateUser(name: String, password: String) = users.contains(UserCredentials(name, password))

fun Application.configureAuth() {
    authentication {
        basic {
            realm = "Access to '/'"
            validate { credentials ->
                UserIdPrincipal(credentials.name).takeIf { validateUser(credentials.name, credentials.password) }
            }
        }
    }

    routing {
        route("auth") {
            post("register") {
                users += call.receive<UserCredentials>()
                call.respondText("Registered")
            }
            post("validateCredentials") {
                val credentials = call.receive<UserCredentials>()
                call.respond(mapOf("valid" to users.contains(credentials)))
            }
            authenticate {
                post("changePassword") {
                    @Serializable data class DTO(val password: String)
                    val dto = call.receive<DTO>()
                    val username = call.principal<UserIdPrincipal>()!!.name
                    users.single { it.name == username }.password = dto.password
                    call.respondText("Password changed")
                }
            }
        }
    }
}

suspend fun DefaultWebSocketServerSession.authenticate(): String? {
    val credentials = receiveDeserialized<UserCredentials>()
    if (!validateUser(credentials.name, credentials.password)) {
        close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Wrong credentials"))
        return null
    }
    return credentials.name
}
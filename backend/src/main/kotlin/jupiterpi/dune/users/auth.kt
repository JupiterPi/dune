package jupiterpi.dune.users

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

fun Application.configureAuth() {
    authentication {
        basic {
            realm = "Access to '/'"
            validate {
                val credentials = UserCredentials(it.name, it.password)
                UserIdPrincipal(credentials.name).takeIf { UserRepo.validateUser(credentials) }
            }
        }
    }

    routing {
        route("auth") {
            post("register") {
                val credentials = call.receive<UserCredentials>()
                UserRepo.createUser(credentials)
                call.respondText("Registered")
            }
            post("validateCredentials") {
                val credentials = call.receive<UserCredentials>()
                call.respond(mapOf("valid" to UserRepo.validateUser(credentials)))
            }
            authenticate {
                post("changePassword") {
                    @Serializable data class DTO(val password: String)
                    val dto = call.receive<DTO>()
                    val username = call.principal<UserIdPrincipal>()!!.name
                    UserRepo.changePassword(username, dto.password)
                    call.respondText("Password changed")
                }
            }
        }
    }
}

suspend fun DefaultWebSocketServerSession.authenticate(): String? {
    val credentials = receiveDeserialized<UserCredentials>()
    if (!UserRepo.validateUser(credentials)) {
        close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Wrong credentials"))
        return null
    }
    return credentials.name
}
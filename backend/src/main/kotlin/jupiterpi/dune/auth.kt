package jupiterpi.dune

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class UserCredentials(
    val name: String,
    var password: String
)

val users = mutableListOf(
    UserCredentials("JupiterPii", "password"),
)
fun validateUser(name: String, password: String) = users.contains(UserCredentials(name, password))

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
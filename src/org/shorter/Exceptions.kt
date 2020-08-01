package org.shorter

import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Routing

class AuthenticationException : RuntimeException()
class AuthorizationException : RuntimeException()
class UrlNotFoundException : RuntimeException()

fun Routing.installExceptionHandling() {
    install(StatusPages) {
        exception<AuthenticationException> { cause ->
            call.respond(HttpStatusCode.Unauthorized)
        }

        exception<AuthorizationException> { cause ->
            call.respond(HttpStatusCode.Forbidden)
        }

        exception<UrlNotFoundException> { cause ->
            call.respond(HttpStatusCode.NotFound)
        }
    }
}
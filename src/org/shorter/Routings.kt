package org.shorter.org.shorter

import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get

fun Routing.configureRouting() {

    get("/") {
        call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
    }

    get("/json/jackson") {
        call.respond(mapOf("hello" to "world"))
    }

}
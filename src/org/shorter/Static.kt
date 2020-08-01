package org.shorter.org.shorter

import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.routing.Routing

fun Routing.configureStatic() {
    static("/static") {
        resources("static")
    }
}
package org.shorter

import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import org.testcontainers.containers.PostgreSQLContainer
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.ExperimentalTime

@ExperimentalTime
class ApplicationTest {

    private fun container() = PostgreSQLContainer<Nothing>("postgres:12.3").apply {
        withDatabaseName("shorter")
        withUsername("shorter")
        withPassword("shorter")
    }

    @Test
    fun testRoot() {
        container().use {
            it.start()

            withTestApplication({ module(testing = true, dbHost = it.host, dbPort = it.firstMappedPort) }) {
                handleRequest(HttpMethod.Get, "/").apply {
                    assertEquals(HttpStatusCode.OK, response.status())
                    assertEquals("HELLO WORLD!", response.content)
                }
            }
        }
    }
}

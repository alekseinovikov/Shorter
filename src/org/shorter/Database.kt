package org.shorter.org.shorter

import io.ktor.application.Application
import io.ktor.application.ApplicationStarted
import io.r2dbc.pool.ConnectionPool
import io.r2dbc.pool.ConnectionPoolConfiguration
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration
import io.r2dbc.postgresql.PostgresqlConnectionFactory
import io.r2dbc.spi.Connection
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.runBlocking
import reactor.kotlin.core.publisher.toFlux
import kotlin.time.ExperimentalTime
import kotlin.time.seconds
import kotlin.time.toJavaDuration

@ExperimentalTime
object Database {

    private val connectionFactory = PostgresqlConnectionFactory(
        PostgresqlConnectionConfiguration.builder()
            .host("localhost")
            .port(15432)
            .username("shorter")
            .password("shorter")
            .database("shorter")
            .build()
    )

    private val poolConfig = ConnectionPoolConfiguration.builder(connectionFactory)
        .maxIdleTime(10.seconds.toJavaDuration())
        .maxSize(20)
        .build()

    private val pool = ConnectionPool(poolConfig)
    private suspend fun getConnection() = pool.create().awaitSingle()

    fun Application.initDB() {
        environment.monitor.subscribe(ApplicationStarted) {
            launch { pool.warmup().awaitFirst() }
        }

        runBlocking { initDBStructure() }
    }

    private suspend fun initDBStructure() = updateOnConnection(
        """
        CREATE TABLE IF NOT EXISTS urls(
            id BIGSERIAL NOT NULL PRIMARY KEY,
            shorter VARCHAR(56) NOT NULL UNIQUE,
            url VARCHAR(2056) NOT NULL UNIQUE
        );
        """.trimMargin()
    )

    suspend fun updateOnConnection(sql: String) = updateOnConnection {
        it.createStatement(sql).execute()
            .toFlux()
            .collectList()
            .awaitFirst()
    }

    suspend fun updateOnConnection(statement: suspend (Connection) -> Unit) =
        getConnection().let { connection ->
            try {
                statement(connection)
            } finally {
                connection.close()
                    .awaitFirstOrNull()
            }
        }

    suspend fun <T> queryOnConnection(statement: suspend (Connection) -> T) =
        getConnection().let { connection ->
            try {
                statement(connection)
            } finally {
                connection.close()
                    .awaitFirstOrNull()
            }
        }

}
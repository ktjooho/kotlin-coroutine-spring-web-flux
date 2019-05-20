package com.example.demo

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.reactive.function.server.*

@Configuration

class AppConfig {

    val log by LoggerDelegate()
    @Bean
    fun springWebFilterChain(http:ServerHttpSecurity): SecurityWebFilterChain {
        return http.addFilterAt({ exchange, chain ->
            log.debug("Filter...")
            chain.filter(exchange)
        },SecurityWebFiltersOrder.FIRST).build()
    }

    suspend fun listView(req:ServerRequest):ServerResponse =
            ServerResponse.ok().bodyAndAwait("Hello List" )

    @Bean
    fun route():RouterFunction<ServerResponse> = coRouter {
        GET("/reactive/test") {
            listView(it)
        }
    }




}
package com.example.demo

import kotlinx.coroutines.*
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.util.StopWatch
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitExchange
import reactor.core.publisher.Flux
import reactor.core.scheduler.Schedulers
import kotlin.collections.ArrayList as ArrayList1

class PerformTest {

    val log by LoggerDelegate()
    @Test
    fun `perform`() = runBlocking{
        val webClient = WebClient.create("http://localhost:8080")
        val watch = StopWatch()
        watch.start()
        val list:MutableList<Deferred<*>> = kotlin.collections.ArrayList()
        repeat(100) {
            val async = async {
                withContext(Dispatchers.IO) {
                    log.debug("시작.")
                    webClient.get()
                            .uri("/delay")
                            .accept(MediaType.ALL)
                            .awaitExchange()
                }
            }
            list.add(async)

        }
        awaitAll(*list.toTypedArray())
        watch.stop()
        log.debug("끝 ... ${watch.totalTimeMillis} ms")
        /*
        Flux.range(0,500)
            .flatMap {
                webClient.get()
                        .uri("/delay")
                        .accept(MediaType.ALL)
                        .awaitExchange()
                        .exchange()
                        .map { it.bodyToMono(String::class.java) }
                        .publishOn(Schedulers.elastic())
            }
        .subscribe({
            log.debug("시작 ${it}")
        },{},{
            watch.stop()
            log.debug("끝 ${watch.totalTimeMillis}")
        })
        Thread.sleep(500000L)
        */
    }



}
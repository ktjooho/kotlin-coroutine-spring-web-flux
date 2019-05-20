package com.example.demo

import kotlinx.coroutines.delay
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.time.Duration
import java.time.temporal.ChronoUnit
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty
import kotlin.reflect.full.companionObject

@SpringBootApplication
class DemoApplication

fun main(args: Array<String>) {
	runApplication<DemoApplication>(*args)
}

class LoggerDelegate<in R : Any> : ReadOnlyProperty<R, Logger> {
	override fun getValue(thisRef: R, property: KProperty<*>): Logger {
		return LoggerFactory.getLogger(getClassForLogging(thisRef.javaClass))
	}
}
fun <T:Any> T.logger(): Logger = LoggerFactory.getLogger(getClassForLogging(javaClass))

inline fun <T : Any> getClassForLogging(javaClass:Class<T>): Class<*> {
	return javaClass.enclosingClass?.takeIf {
		it.kotlin.companionObject?.java == javaClass
	} ?: javaClass
}

@RestController
class TestController {
	val log by LoggerDelegate()

	@GetMapping("/coroutine/delay")
	@ResponseBody
	suspend fun coroutineDelayed(): String {
		log.debug("Start Block")
		delay(25000L)
//		Thread.sleep(5000L)
		log.debug("End Block")
		return "Hello"
	}

	@GetMapping("/reactor/delay")
	fun reactorDelayed() : Mono<String> {
		log.debug("Start Block")
		return Mono.defer { Mono.delay(Duration.of(5,ChronoUnit.SECONDS)) }
				.map {
					log.debug("End Block")
					"Hello"
				}
	}

	@GetMapping("/reactor/block")
	fun reactorBlocked() : Mono<String> {
		log.debug("Start Block")
		return  Mono.fromCallable {
			Thread.sleep(5000L)
			log.debug("End Block")
			"Hello"
			}
		.subscribeOn(Schedulers.elastic())
	}

}


@Component
class FilterTest : WebFilter {
	val log by LoggerDelegate()
	override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
		log.debug("HELLO")
		return chain.filter(exchange)
	}
}







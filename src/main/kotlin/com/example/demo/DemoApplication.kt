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

	@GetMapping("/delay")
	@ResponseBody
	suspend fun block(): String {
		log.debug("Start Block")
		delay(25000L)
//		Thread.sleep(5000L)
		log.debug("End Block")
		return "Hello"
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







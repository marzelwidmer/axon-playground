package ch.keepcalm


import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class AxonApplication

fun main(args: Array<String>) {
	runApplication<AxonApplication>(*args)
}

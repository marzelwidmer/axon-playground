package ch.keepcalm

import org.springframework.beans.factory.annotation.Qualifier
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.databind.SerializationFeature
import org.axonframework.serialization.json.JacksonSerializer
import org.axonframework.commandhandling.distributed.RoutingStrategy
import org.axonframework.commandhandling.distributed.AnnotationRoutingStrategy
import org.axonframework.commandhandling.distributed.UnresolvedRoutingKeyPolicy
import org.axonframework.messaging.interceptors.LoggingInterceptor
import org.springframework.beans.factory.annotation.Autowired
import org.axonframework.commandhandling.CommandBus
import org.axonframework.config.EventProcessingConfigurer
import org.axonframework.queryhandling.QueryBus
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway
import org.axonframework.extensions.reactor.queryhandling.gateway.ReactorQueryGateway
import org.axonframework.commandhandling.CommandMessage
import org.axonframework.commandhandling.CommandResultMessage
import reactor.core.publisher.Flux
import org.axonframework.queryhandling.QueryMessage
import org.axonframework.messaging.ResultMessage
import reactor.core.publisher.Hooks
import org.axonframework.eventhandling.EventBus
import org.axonframework.messaging.Message
import org.axonframework.serialization.Serializer
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.lang.invoke.MethodHandles

@Configuration
open class ApplicationConfig {
    @Bean
    @Qualifier("messageSerializer")
    open fun messageSerializer(): Serializer {
        val objectMapper = ObjectMapper()
        objectMapper.registerModule(JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        return JacksonSerializer.builder()
            .objectMapper(objectMapper)
            .lenientDeserialization()
            .build()
    }

    @Bean
    open fun routingStrategy(): RoutingStrategy {
        return AnnotationRoutingStrategy.builder()
            .fallbackRoutingStrategy(UnresolvedRoutingKeyPolicy.RANDOM_KEY)
            .build()
    }

    @Bean
    open fun loggingInterceptor(): LoggingInterceptor<Message<*>> {
        return LoggingInterceptor()
    }

//    @Autowired
//    fun configureLoggingInterceptorFor(
//        commandBus: CommandBus,
//        loggingInterceptor: LoggingInterceptor<Message<*>?>
//    ) {
//        commandBus.registerDispatchInterceptor(loggingInterceptor)
//        commandBus.registerHandlerInterceptor(loggingInterceptor)
//    }
//
//    @Autowired
//    fun configureLoggingInterceptorFor(eventBus: EventBus, loggingInterceptor: LoggingInterceptor<Message<*>?>?) {
//        eventBus.registerDispatchInterceptor(loggingInterceptor)
//    }
//
//    @Autowired
//    fun configureLoggingInterceptorFor(eventProcessingConfigurer: EventProcessingConfigurer, loggingInterceptor: LoggingInterceptor<Message<*>?>?) {
//        eventProcessingConfigurer.registerDefaultHandlerInterceptor { config: org.axonframework.config.Configuration?, processorName: String? -> loggingInterceptor }
//    }
//
//    @Autowired
//    fun configureLoggingInterceptorFor(queryBus: QueryBus, loggingInterceptor: LoggingInterceptor<Message<*>?>) {
//        queryBus.registerDispatchInterceptor(loggingInterceptor)
//        queryBus.registerHandlerInterceptor(loggingInterceptor)
//    }
//
//    @Autowired
//    fun configureResultHandlerInterceptors(
//        commandGateway: ReactorCommandGateway,
//        queryGateway: ReactorQueryGateway
//    ) {
//        commandGateway.registerResultHandlerInterceptor { cmd: CommandMessage<*>?, result: Flux<CommandResultMessage<*>?> ->
//            result.onErrorMap { exception: Throwable ->
//                ExceptionMapper.mapRemoteException(exception)
//            }
//        }
//        queryGateway.registerResultHandlerInterceptor { query: QueryMessage<*, *>?, result: Flux<ResultMessage<*>?> ->
//            result.onErrorMap { exception: Throwable ->
//                ExceptionMapper.mapRemoteException(exception)
//            }
//        }
//    }

    /**
     * This [Hooks.onErrorDropped] is included as a recommendation from RSocket Java's GitHub issue
     * [#1018](https://github.com/rsocket/rsocket-java/issues/1018). Ideally the problem would be taken care off by
     * RSocket, but at this stage the below solution is recommended by a contributor. To be certain not all exception
     * are blocked, only the `"Exceptions$ErrorCallbackNotImplemented"` is covered.
     */
    @Autowired
    fun configureHooks() {
        Hooks.onErrorDropped { t: Throwable ->
            if (t.javaClass.toString() != "class reactor.core.Exceptions\$ErrorCallbackNotImplemented") {
                logger.warn("Invoked onErrorDropped for exception [{}]", t.javaClass, t)
            }
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass())
    }
}



//import com.fasterxml.jackson.databind.ObjectMapper
//import com.fasterxml.jackson.databind.SerializationFeature
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
//import org.axonframework.commandhandling.CommandBus
//import org.axonframework.commandhandling.distributed.AnnotationRoutingStrategy
//import org.axonframework.commandhandling.distributed.RoutingStrategy
//import org.axonframework.commandhandling.distributed.UnresolvedRoutingKeyPolicy
//import org.axonframework.config.Configuration
//import org.axonframework.config.EventProcessingConfigurer
//import org.axonframework.eventhandling.EventBus
//import org.axonframework.messaging.Message
//import org.axonframework.messaging.interceptors.LoggingInterceptor
//import org.axonframework.queryhandling.QueryBus
//import org.axonframework.serialization.Serializer
//import org.axonframework.serialization.json.JacksonSerializer
//import org.slf4j.LoggerFactory
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.beans.factory.annotation.Qualifier
//import org.springframework.context.annotation.Bean
//import org.springframework.stereotype.Component
//import reactor.core.publisher.Hooks
//
//// see : https://github.com/smcvb/gamerental/blob/master/src/main/java/io/axoniq/demo/gamerental/ApplicationConfig.java
//@Component
//class ApplicationConfig {
//
//    private val logger = LoggerFactory.getLogger(javaClass)
//
//    @Bean
//    @Qualifier("messageSerializer")
//    fun messageSerializer(): Serializer? {
//        val objectMapper = ObjectMapper()
//        objectMapper.registerModule(JavaTimeModule())
//            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
//            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
//        return JacksonSerializer.builder()
//            .objectMapper(objectMapper)
//            .lenientDeserialization()
//            .build()
//    }
//
//    @Bean
//    fun routingStrategy(): RoutingStrategy? {
//        return AnnotationRoutingStrategy.builder()
//            .fallbackRoutingStrategy(UnresolvedRoutingKeyPolicy.RANDOM_KEY)
//            .build()
//    }
//
//    @Bean
//    fun loggingInterceptor(): LoggingInterceptor<Message<*>?>? {
//        return LoggingInterceptor()
//    }
//
//    @Autowired
//    fun configureLoggingInterceptorFor(
//        commandBus: CommandBus,
//        loggingInterceptor: LoggingInterceptor<Message<*>?>
//    ) {
//        commandBus.registerDispatchInterceptor(loggingInterceptor)
//        commandBus.registerHandlerInterceptor(loggingInterceptor)
//    }
//
////    @Autowired
////    fun configureLoggingInterceptorFor(eventBus: EventBus, loggingInterceptor: LoggingInterceptor<Message<*>?>?) {
////        eventBus.registerDispatchInterceptor(loggingInterceptor)
////    }
////
////    @Autowired
////    fun configureLoggingInterceptorFor(
////        eventProcessingConfigurer: EventProcessingConfigurer,
////        loggingInterceptor: LoggingInterceptor<Message<*>?>?
////    ) {
////        eventProcessingConfigurer.registerDefaultHandlerInterceptor { config: Configuration?, processorName: String? -> loggingInterceptor }
////    }
////
////    @Autowired
////    fun configureLoggingInterceptorFor(queryBus: QueryBus, loggingInterceptor: LoggingInterceptor<Message<*>?>) {
////        queryBus.registerDispatchInterceptor(loggingInterceptor)
////        queryBus.registerHandlerInterceptor(loggingInterceptor)
////    }
//
//
//    @Autowired
//    fun configureHooks() {
//        Hooks.onErrorDropped { t: Throwable ->
//            if (t.javaClass.toString() != "class reactor.core.Exceptions\$ErrorCallbackNotImplemented") {
//                logger.warn("Invoked onErrorDropped for exception [{}]", t.javaClass, t)
//            }
//        }
//    }
//}
//
//

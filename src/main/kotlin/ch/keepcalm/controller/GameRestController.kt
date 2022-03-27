package ch.keepcalm.controller

import ch.keepcalm.coreapi.RegisterGameCommand
import kotlinx.coroutines.reactive.awaitSingle
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.extensions.kotlin.send
import org.slf4j.LoggerFactory
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.MediaTypes
import org.springframework.hateoas.config.EnableHypermediaSupport
import org.springframework.hateoas.server.reactive.WebFluxLinkBuilder
import org.springframework.hateoas.support.WebStack
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.time.Instant

@RestController
@RequestMapping(produces = [MediaTypes.HAL_JSON_VALUE])
@EnableHypermediaSupport(stacks = [WebStack.WEBFLUX], type = [EnableHypermediaSupport.HypermediaType.HAL])
class GameRestController(val commandGateway: CommandGateway) {

    private val log = LoggerFactory.getLogger(javaClass)

    @PostMapping(value = ["/register"])
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun registerGame(@RequestBody request: Game): EntityModel<Unit> {
        log.info("-----------> {}", request)

        commandGateway.send<Any>(
            RegisterGameCommand(
                gameIdentifier = request.gameIdentifier,
                title = request.title,
                releaseDate = request.releaseDate,
                description = request.description,
                singleplayer = request.singleplayer,
                multiplayer = request.multiplayer
            )
        )


        val response = EntityModel.of(
            Unit, WebFluxLinkBuilder.linkTo(
                WebFluxLinkBuilder.methodOn(
                    GameRestController::class.java
                ).registerGame(request)
            ).withSelfRel().toMono().awaitSingle()
        )


        return response
    }

}

data class Game(
    var gameIdentifier: String,
    val title: String,
    val releaseDate: Instant,
    val description: String,
    val singleplayer: Boolean = false,
    val multiplayer: Boolean = false
)

package ch.keepcalm.command

import ch.keepcalm.coreapi.*
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateCreationPolicy
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.modelling.command.CreationPolicy
import org.axonframework.spring.stereotype.Aggregate
import org.slf4j.LoggerFactory
import java.time.Instant

//@Profile("command")
@Aggregate
class Game {

    private val log = LoggerFactory.getLogger(javaClass)

//    private var stock: Int = null
    private var stock: Int = 0
    private lateinit var releaseDate: Instant
    private lateinit var renters: HashSet<String>

    // TODO: 23.03.22 Better use Typed Identifier
    @AggregateIdentifier
    private lateinit var gameIdentifier: String

    @CreationPolicy(AggregateCreationPolicy.CREATE_IF_MISSING)
    @CommandHandler
    fun handleRegisterGameCommand(command: RegisterGameCommand) {
        log.info("CreationPolicy - handleRegisterGameCommand ----------->  {}", command)
        if (!this::gameIdentifier.isInitialized) {
            AggregateLifecycle.apply(
                GameRegisteredEvent(
                    gameIdentifier = command.gameIdentifier,
                    title = command.title,
                    description= command.description,
                    releaseDate = command.releaseDate,
                    singleplayer = command.singleplayer,
                    multiplayer = command.multiplayer
                )
            )
        }
    }


    @CommandHandler
    fun handle(command: RentGameCommand) {
        log.info("handle RentGameCommand ----------->  {}", command)
        check(stock >= 0) {
            "Insufficient items in stock for game with identifier [$gameIdentifier]"
        }
        check(!Instant.now().isBefore(releaseDate)) {
            "Game with identifier [$gameIdentifier] cannot be rented out as it has not been released yet"
        }
        AggregateLifecycle.apply(GameRentedEvent(gameIdentifier = gameIdentifier, renter = command.renter))
    }

    @CommandHandler
    fun handle(command: ReturnGameCommand) {
        log.info("handle ReturnGameCommand ----------->  {}", command)
        check(renters.contains(command.returner)) { "A game should be returned by someone who has actually rented it" }
        AggregateLifecycle.apply(GameReturnedEvent(gameIdentifier = gameIdentifier, returner = command.returner))
    }


    
    @EventSourcingHandler
    fun on(event: GameRegisteredEvent) {
        log.info("EventSourcing GameRegisteredEvent ----------->  {}", event)

        gameIdentifier = event.gameIdentifier
        stock = 1
        releaseDate = event.releaseDate
        renters = HashSet<String>()

    }

    @EventSourcingHandler
    fun on(event: GameRentedEvent) {
        log.info("EventSourcing GameRentedEvent ----------->  {}", event)

        stock.minus(1)
        renters.add(event.renter)
    }

    @EventSourcingHandler
    fun on(event: GameReturnedEvent) {
        log.info("EventSourcing GameReturnedEvent ----------->  {}", event)

        stock.plus(1)
        renters.remove(event.returner)
    }

}

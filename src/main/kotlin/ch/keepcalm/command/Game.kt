package ch.keepcalm.command

import ch.keepcalm.coreapi.*
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateCreationPolicy
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.modelling.command.CreationPolicy
import org.axonframework.spring.stereotype.Aggregate
import java.time.Instant

//@Profile("command")
@Aggregate
class Game {

    private var stock: Int? = null
    private var releaseDate: Instant? = null

    // TODO: 26.03.22 Lateinit Version
    private lateinit var renters: HashSet<String>

    // TODO: 23.03.22 Better use Typed Identifier
    @AggregateIdentifier
    private lateinit var gameIdentifier: String

    @CreationPolicy(AggregateCreationPolicy.CREATE_IF_MISSING)
    @CommandHandler
    fun handleRegisterGameCommand(command: RegisterGameCommand) {
//        if(stock <= 0 ) {
//            throw IllegalStateException("Insufficient items in stock for game with identifier [$gameIdentifier]")
//        }
        stock.let {
            if(Instant.now().isBefore(releaseDate)){
                throw IllegalStateException(
                    "Game with identifier [$gameIdentifier] cannot be rented out as it has not been released yet"
                )
            }

            if (!this::gameIdentifier.isInitialized) {
                AggregateLifecycle.apply(
                    GameRegisteredEvent(
                        gameIdentifier = command.gameIdentifier,
                        title = command.title,
                        releaseDate = command.releaseDate,
                        singleplayer = command.singleplayer,
                        multiplayer = command.multiplayer
                    )
                )
            }
        }
        throw IllegalStateException("Insufficient items in stock for game with identifier [$gameIdentifier]")

    }

    @CommandHandler
    fun handle(command: RentGameCommand) {
        AggregateLifecycle.apply(GameRentedEvent(gameIdentifier = gameIdentifier, renter = command.renter))
    }

    @CommandHandler
    fun handle(command: ReturnGameCommand) {
        check(renters.contains(command.returner)) { "A game should be returned by someone who has actually rented it" }
        AggregateLifecycle.apply(GameReturnedEvent(gameIdentifier = gameIdentifier, returner = command.returner))
    }

    @EventSourcingHandler
    fun on(event: GameRegisteredEvent) {
        gameIdentifier = event.gameIdentifier
        stock = 1
        releaseDate = event.releaseDate
        renters = HashSet<String>()

    }
    @EventSourcingHandler
    fun on(event: GameRentedEvent) {
        stock?.minus(1)
        renters.add(event.renter)
    }

    @EventSourcingHandler
    fun on(event: GameReturnedEvent){
    	stock?.plus(1)
        renters.remove(event.returner)
    }

}

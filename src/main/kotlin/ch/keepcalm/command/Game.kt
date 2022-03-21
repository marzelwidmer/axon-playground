package ch.keepcalm.command

import ch.keepcalm.coreapi.GameRegisteredEvent
import ch.keepcalm.coreapi.RegisterGameCommand
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateCreationPolicy
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.modelling.command.CreationPolicy
import org.axonframework.spring.stereotype.Aggregate
import org.springframework.context.annotation.Profile
import java.util.*

@Profile("command")
@Aggregate
class Game {

        @AggregateIdentifier
        private lateinit var gameIdentifier: String

        @CreationPolicy(AggregateCreationPolicy.CREATE_IF_MISSING)
        @CommandHandler
        fun handleRegisterGameCommand(command: RegisterGameCommand) {
            if (!this::gameIdentifier.isInitialized) {
                AggregateLifecycle.apply(
                    GameRegisteredEvent(gameIdentifier = command.gameIdentifier,
                            title = command.title,
                    releaseDate = command.releaseDate,
                    singleplayer = command.singleplayer,
                    multiplayer = command.multiplayer)
                )
            }
        }

        @EventSourcingHandler
    	fun on(event: GameRegisteredEvent){
    		gameIdentifier = event.gameIdentifier
    	}

}

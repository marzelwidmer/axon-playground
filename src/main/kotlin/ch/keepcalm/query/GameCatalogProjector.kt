package ch.keepcalm.query

import ch.keepcalm.coreapi.*
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrElse
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.axonframework.queryhandling.QueryHandler
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.*

@Component
@ProcessingGroup(value = "catalog")
class GameCatalogProjector(private val repository: GameViewRepository) {

    private val log = LoggerFactory.getLogger(javaClass)

    @EventHandler
    fun handle(event: GameRegisteredEvent) {
        log.info("GameRegisteredEvent -----------> {}", event)



        repository.save(
            GameView(
                gameIdentifier = event.gameIdentifier,
                title = event.title,
                description = event.description,
                releaseDate = event.releaseDate.toString(),
                singleplayer = event.singleplayer,
                multiplayer = event.multiplayer
            )
        )
    }


    @EventHandler
     fun on(event: GameRentedEvent) {
        log.info("GameRentedEvent -----------> {}", event)

        val result: Optional<GameView> = repository.findById(event.gameIdentifier)
//        val result: GameView? = repository.findById(event.gameIdentifier).awaitFirst()
//        if (result != null) {
        if (result.isPresent()) {
            result.get().decrementStock()
        } else {
            throw IllegalArgumentException("Game with id [${event.gameIdentifier}] could not be found.")
        }
    }

    @EventHandler
     fun on(event: GameReturnedEvent) {
        log.info("GameReturnedEvent -----------> {}", event)


        val result: Optional<GameView> = repository.findById(event.gameIdentifier)
//        val result: GameView? = repository.findById(event.gameIdentifier).awaitFirst()
//        if (result != null) {
        if (result.isPresent()) {
            result.get().incrementStock()
        } else {
            throw IllegalArgumentException("Game with id [${event.gameIdentifier}] could not be found.")
        }
    }


//    @QueryHandler
//     fun handle(query: FindGameQuery): Game? {
//        val gameIdentifier: String = query.gameIdentifier
//        return repository.findById(gameIdentifier)
//            .map { gameView ->
//                Game(
//                    title = gameView.title,
//                    releaseDate = gameView.releaseDate,
//                    description = gameView.description,
//                    singleplayer = gameView.singleplayer,
//                    multiplayer = gameView.multiplayer
//                )
//            }
//            .orElseThrow {  IllegalArgumentException("Game with id [$gameIdentifier] could not be found.") }
////            .awaitFirstOrElse { throw IllegalArgumentException("Game with id [$gameIdentifier] could not be found.") }
//    }
}

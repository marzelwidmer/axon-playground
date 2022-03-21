package ch.keepcalm.coreapi

import java.time.Instant

data class GameRegisteredEvent(val gameIdentifier: String,
                               val title: String,
                               val releaseDate: Instant,
                               val singleplayer: Boolean,
                               val multiplayer: Boolean)

data class GameRentedEvent(val gameIdentifier: String,
                           val renter: String)

data class GameReturnedEvent(val gameIdentifier: String,
                             val renter: String)

package ch.keepcalm.coreapi

import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.time.Instant

data class RegisterGameCommand(@TargetAggregateIdentifier val gameIdentifier: String,
                               val title: String,
                               val releaseDate: Instant,
                               val singleplayer: Boolean,
                               val multiplayer: Boolean)

data class RentGameCommand(@TargetAggregateIdentifier val gameIdentifier: String,
                           val renter: String)

data class ReturnGameCommand(@TargetAggregateIdentifier val gameIdentifier: String,
                             val renter: String)


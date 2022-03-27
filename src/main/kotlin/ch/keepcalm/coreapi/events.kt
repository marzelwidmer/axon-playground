package ch.keepcalm.coreapi

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import java.beans.ConstructorProperties
import java.time.Instant

data class GameRegisteredEvent @ConstructorProperties("gameIdentifier", "title", "releaseDate", "description", "singleplayer", "multiplayer") constructor(
    val gameIdentifier: String,
    val title: String,
    val description: String,
//    @JsonSerialize(using = LocalDateTimeSerializer::class)
//    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    val releaseDate: Instant,
    val singleplayer: Boolean,
    val multiplayer: Boolean
)

data class GameRentedEvent @ConstructorProperties("gameIdentifier", "renter") constructor(
    val gameIdentifier: String,
    val renter: String
)

data class GameReturnedEvent @ConstructorProperties("gameIdentifier", "returner") constructor(
    val gameIdentifier: String,
    val returner: String
)



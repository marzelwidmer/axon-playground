package ch.keepcalm.query

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.format.annotation.DateTimeFormat
import java.time.Instant

@Document
class GameView(
    @Id val gameIdentifier: String,
    val title: String,
//    @JsonProperty("releaseDate")
//    @JsonDeserialize(using = LocalDateDeserializer::class)
//    @JsonSerialize(using = LocalDateSerializer::class)
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss Z")
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
//    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    val releaseDate: String,
    val description: String,
    val singleplayer: Boolean,
    val multiplayer: Boolean,
    var stock: Int = 1
) {
    fun incrementStock() {
        stock++
    }

    fun decrementStock() {
        stock--
    }
}



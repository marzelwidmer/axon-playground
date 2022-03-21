import java.time.Instant

data class Game(
    val title: String,
    val releaseDate: Instant,
    val description: String,
    val singleplayer: Boolean,
    val multiplayer: Boolean
)


class FullGameCatalogQuery {
    override fun toString(): String {
        return "FullGameCatalogQuery"
    }
}

package ch.keepcalm.query

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.ReactiveMongoRepository

//interface GameViewRepository : ReactiveMongoRepository<GameView, String>
interface GameViewRepository : MongoRepository<GameView, String>

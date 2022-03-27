package ch.keepcalm

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.mongodb.client.MongoClient
import com.thoughtworks.xstream.XStream
import org.axonframework.config.Configuration
import org.axonframework.config.Configurer
import org.axonframework.eventhandling.tokenstore.TokenStore
import org.axonframework.eventsourcing.EventCountSnapshotTriggerDefinition
import org.axonframework.eventsourcing.SnapshotTriggerDefinition
import org.axonframework.eventsourcing.eventstore.EventStorageEngine
import org.axonframework.extensions.mongo.DefaultMongoTemplate
import org.axonframework.extensions.mongo.eventsourcing.eventstore.MongoEventStorageEngine
import org.axonframework.extensions.mongo.eventsourcing.tokenstore.MongoTokenStore
import org.axonframework.serialization.Serializer
import org.axonframework.serialization.json.JacksonSerializer
import org.axonframework.serialization.xml.XStreamSerializer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.format.datetime.DateFormatter
import org.springframework.format.datetime.DateFormatterRegistrar
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar
import org.springframework.format.number.NumberFormatAnnotationFormatterFactory
import org.springframework.format.support.DefaultFormattingConversionService
import org.springframework.format.support.FormattingConversionService
import org.springframework.stereotype.Component
import java.time.format.DateTimeFormatter


@Component
class AxonMongoDBConfiguration{

//	@Bean
//	fun formattingConversionService(): FormattingConversionService? {
//		// Use the DefaultFormattingConversionService but do not register defaults
//		val conversionService = DefaultFormattingConversionService(false)
//		// Ensure @NumberFormat is still supported
//		conversionService.addFormatterForFieldAnnotation(NumberFormatAnnotationFormatterFactory())
//		// Register JSR-310 date conversion with a specific global format
//		val dateTimeFormatterRegistrar = DateTimeFormatterRegistrar()
//		dateTimeFormatterRegistrar.setDateFormatter(DateTimeFormatter.ofPattern("yyyyMMdd"))
//		dateTimeFormatterRegistrar.registerFormatters(conversionService)
//
//		// Register date conversion with a specific global format
////		val dateFormatterRegistrar = DateFormatterRegistrar()
////		dateFormatterRegistrar.setFormatter(DateFormatter("yyyyMMdd"))
////		dateFormatterRegistrar.registerFormatters(conversionService)
//
//		return conversionService
//
//	}


	// But for all our messages we'd prefer the JacksonSerializer due to JSON's smaller format
	@Bean
	@Qualifier("messageSerializer")
	fun jacksonMessageSerializer(): Serializer {
		val objectMapper = ObjectMapper()
		objectMapper.registerModule(KotlinModule.Builder().build())
		objectMapper.registerModule(JavaTimeModule())
			.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
			.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
		return JacksonSerializer.builder()
			.objectMapper(objectMapper)
			.lenientDeserialization()
			.build()
	}

	@Bean
	@Qualifier("messageSerializer")
	fun messageSerializer(): Serializer = XStreamSerializer.builder()
		.xStream(xStream())
		.build()

	fun xStream(): XStream {
		val xStream = XStream()
		xStream.allowTypesByWildcard(
			arrayOf(
				"org.axonframework.**",
				"**",
			)
		)
		return XStreamSerializer.builder().xStream(xStream).build().xStream
	}

	/**
	 * Uses the Configurer to wire everything together including Mongo as the Event and Token Store.
	 */
	@Autowired
	fun configuration(configurer: Configurer, client: MongoClient) {
		configurer
			.configureEmbeddedEventStore { storageEngine(client) }
			.eventProcessing { conf -> conf.registerTokenStore { tokenStore(client, it.serializer()) } }
	}


	/**
	 * Create a Mongo based Event Storage Engine.
	 */
	@Bean
	fun storageEngine(client: MongoClient?): EventStorageEngine = MongoEventStorageEngine.builder()
		.eventSerializer(messageSerializer())
		.snapshotSerializer(messageSerializer())
		.mongoTemplate(
			DefaultMongoTemplate
				.builder()
				.mongoDatabase(client)
				.build()
		).build()


	/**
	 * Create a Mongo based Token Store.
	 */
	fun tokenStore(client: MongoClient, serializer: Serializer): TokenStore = MongoTokenStore.builder()
		.mongoTemplate(
			DefaultMongoTemplate.builder()
				.mongoDatabase(client)
				.build()
		)
		.serializer(serializer)
		.build()

	/**
	 * Configures a snapshot trigger to create a Snapshot every 5 events.
	 * 5 is an arbitrary number used only for testing purposes just to show how the snapshots are stored on Mongo as well.
	 */
	@Bean
	fun mySnapshotTriggerDefinition(configuration: Configuration): SnapshotTriggerDefinition {
		return EventCountSnapshotTriggerDefinition(configuration.snapshotter(), 5)
	}
}

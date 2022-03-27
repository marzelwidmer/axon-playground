package ch.keepcalm

import org.springframework.boot.context.properties.ConfigurationPropertiesBinding
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component
import java.time.Instant

@Component
@ConfigurationPropertiesBinding
class InstantConverter: Converter<String, Instant> {

    override fun convert(source: String): Instant {
        return Instant.parse(source)
    }
}

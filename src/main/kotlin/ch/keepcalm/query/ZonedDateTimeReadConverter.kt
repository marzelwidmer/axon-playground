package ch.keepcalm.query

import org.springframework.core.convert.converter.Converter
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.*

class ZonedDateTimeReadConverter : Converter<Date?, ZonedDateTime?> {
    override fun convert(date: Date): ZonedDateTime? {
        return date.toInstant().atZone(ZoneOffset.UTC)
    }
}


class InstantConverter : Converter<Instant?, String?> {
    override fun convert(date: Instant): String? {
        return date.toString()
    }
}

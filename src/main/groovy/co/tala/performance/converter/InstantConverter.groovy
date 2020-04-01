package co.tala.performance.converter


import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer

import java.lang.reflect.Type
import java.time.Instant
import java.time.format.DateTimeFormatter

class InstantConverter implements JsonSerializer<Instant> {
    private static final FORMATTER = DateTimeFormatter.ISO_INSTANT

    @Override
    JsonElement serialize(Instant src, Type typeOfSrc, JsonSerializationContext context) {
        new JsonPrimitive(FORMATTER.format(src))
    }
}

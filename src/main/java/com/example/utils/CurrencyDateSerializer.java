package com.example.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Created by andrey on 06.10.15.
 */
public class CurrencyDateSerializer extends JsonSerializer<LocalDate> {

    private final DateTimeFormatter OUTPUT_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public void serialize(LocalDate value, JsonGenerator gen, SerializerProvider arg2) throws IOException {
        gen.writeString(value.format(OUTPUT_DATE_FORMAT));
    }
}

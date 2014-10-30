package com.sctrcd.qzr.web.json;

import java.io.IOException;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * When passing JSON around, it's good to use a standard text representation of
 * the date, rather than the full details of a Joda DateTime object. Therefore,
 * this will serialize the value to:
 * 
 * <pre>
 * yyyy-MM-dd
 * </pre>
 * 
 * This can then be parsed by a JavaScript library such as moment.js.
 * 
 * @author Stephen Masters
 */
public class JsonJodaLocalDateSerializer extends JsonSerializer<LocalDate> {

    private static DateTimeFormatter formatter = ISODateTimeFormat.yearMonthDay();

    @Override
    public void serialize(LocalDate value, JsonGenerator gen,
            SerializerProvider arg2) throws IOException,
            JsonProcessingException {

        gen.writeString(formatter.print(value));
    }

}

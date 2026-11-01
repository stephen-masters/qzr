package com.sctrcd.qzr.web.json;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

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
public class JsonJodaLocalDateSerializer extends ValueSerializer<LocalDate> {

    private static final DateTimeFormatter formatter = ISODateTimeFormat.yearMonthDay();

    @Override
    public void serialize(LocalDate value, JsonGenerator gen, SerializationContext context) {

        gen.writeString(formatter.print(value));
    }
}

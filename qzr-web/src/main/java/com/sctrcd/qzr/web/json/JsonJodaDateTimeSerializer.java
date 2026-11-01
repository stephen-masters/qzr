package com.sctrcd.qzr.web.json;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

/**
 * When passing JSON around, it's good to use a standard text representation of
 * the date, rather than the full details of a Joda DateTime object. Therefore,
 * this will serialize the value to the ISO-8601 standard:
 * <pre>yyyy-MM-dd'T'HH:mm:ss.SSSZ</pre>
 * This can then be parsed by a JavaScript library such as moment.js.
 *
 * @author Stephen Masters
 */
public class JsonJodaDateTimeSerializer extends ValueSerializer<DateTime> {

    private static final DateTimeFormatter formatter = ISODateTimeFormat.dateTime();

    @Override
    public void serialize(DateTime value, JsonGenerator gen, SerializationContext context) {

        gen.writeString(formatter.print(value));
    }
}

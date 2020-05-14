/*
 * Copyright 2017 Dr. Gueldener Firmengruppe
 */

package de.jfachwert.math.internal;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import de.jfachwert.math.AbstractNumber;

import java.io.IOException;

/**
 * Die Klasse serialisiert Zahlen, die von {@link AbstractNumber} abgeleitet
 * sind, nach und von JSON. Damit die Serialisierung funktioniert, muss
 * <pre>
 *       &lt;groupId&gt;com.fasterxml.jackson.core&lt;/groupId&gt;
 *       &lt;artifactId&gt;jackson-databind&lt;/artifactId&gt;
 * </pre>
 * als Abhaengigkeit eingebunden werden.
 * 
 * @author oboehm
 * @since 1.0
 */
public final class ToNumberSerializer extends StdSerializer<AbstractNumber> {

    public ToNumberSerializer() {
        this(AbstractNumber.class);
    }

    public ToNumberSerializer(Class<AbstractNumber> t) {
        super(t);
    }

    /**
     * Fuer die Serialisierung wird die uebergebene Nummer in eine
     * {@link java.math.BigDecimal} gewandelt.
     *
     * @param number uebergebene Nummer
     * @param jgen Json-Generator
     * @param provider Provider
     * @throws IOException sollte nicht auftreten
     */
    @Override
    public void serialize(AbstractNumber number, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeNumber(number.toBigDecimal());
    }

}

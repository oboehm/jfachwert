/*
 * Copyright (c) 2018 by Oliver Boehm
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express orimplied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * (c)reated 12.04.18 by oliver (ob@oasd.de)
 */
package de.jfachwert.math;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.jfachwert.math.internal.ToNumberSerializer;

import java.math.BigDecimal;

/**
 * In dieser Klasse sind die gemeinsame Implementierung der abstrakten
 * Methoden der {@link Number}-Klasse zusammengefasst. Diese Klasse
 * wurde eingezogen, um Code-Duplikate zu vermeiden.
 *
 * @author <a href="ob@aosd.de">oliver</a>
 * @since 0.7
 */
@JsonSerialize(using = ToNumberSerializer.class)
public abstract class AbstractNumber extends Number {

    /**
     * Diese Methode liefert die Zahl als BigDecimal zurueck und wird fuer
     * die Default-Implementierung der Number-Methoden benoetigt.
     *
     * @return die Zahl als {@link BigDecimal}
     */
    public abstract BigDecimal toBigDecimal();

    /**
     * Liefert die Zahl als ein {@code int} (gerundet) zurueck.
     *
     * @return den numerischen Wert als {@code int}
     * @since 0.7
     */
    @Override
    public int intValue() {
        return toBigDecimal().intValue();
    }

    /**
     * Liefert die Zahl als ein {@code long} (gerundet) zurueck.
     *
     * @return den numerischen Wert als {@code long}
     * @since 0.7
     */
    @Override
    public long longValue() {
        return toBigDecimal().longValue();
    }

    /**
     * Liefert die Zahl als ein {@code float} zurueck.
     *
     * @return den numerischen Wert als {@code float}
     * @since 0.7
     */
    @Override
    public float floatValue() {
        return toBigDecimal().floatValue();
    }

    /**
     * Liefert die Zahl als ein {@code double} zurueck.
     *
     * @return den numerischen Wert als {@code double}
     * @since 0.7
     */
    @Override
    public double doubleValue() {
        return toBigDecimal().doubleValue();
    }

}

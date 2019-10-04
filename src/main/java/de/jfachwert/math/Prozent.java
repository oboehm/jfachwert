/*
 * Copyright (c) 2019 by Oliver Boehm
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
 * (c)reated 01.10.2019 by oboehm (ob@jfachwert.de)
 */
package de.jfachwert.math;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import de.jfachwert.Fachwert;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.WeakHashMap;

/**
 * Die Klasse Prozent steht fuer den Hundersten Teil einer Zahl.
 * Sie kann wie jede andere Zahl fuer Berechnungen eingesetzt werden,
 * weswegen sie auch von {@link java.lang.Number} abgeleitet ist.
 * <p>
 * Soweit moeglich und sinnvoll wurden die mathematischen Operationen
 * von BigDecimal uebernommen. So gibt es fuer die Multiplikation eine
 * {@link #multiply(BigDecimal)}-Methode. Auch gibt es Konstanten
 * ZERO, ONE und TEN.
 * </p>
 *
 * @author oboehm
 * @since 3.0 (01.10.2019)
 */
@JsonSerialize(using = ToStringSerializer.class)
public class Prozent extends AbstractNumber implements Fachwert {

    private static final WeakHashMap<BigDecimal, Prozent> WEAK_CACHE = new WeakHashMap<>();

    /** Konstante fuer "0%". */
    public static final Prozent ZERO = Prozent.of(BigDecimal.ZERO);

    /** Konstante fuer "1%". */
    public static final Prozent ONE = Prozent.of(BigDecimal.ONE);

    /** Konstante fuer "10%". */
    public static final Prozent TEN = Prozent.of(BigDecimal.TEN);

    private final BigDecimal wert;

    /**
     * Legt ein Prozent-Objekt an.
     *
     * @param wert Prozentwert, z.B. "10" fuer 10 %
     */
    public Prozent(String wert) {
        this(toNumber(wert));
    }

    private static BigDecimal toNumber(String wert) {
        String number = wert.split("%")[0].trim();
        return new BigDecimal(number);
    }

    /**
     * Legt ein Prozent-Objekt an.
     *
     * @param wert Prozentwert, z.B. 10 fuer 10 %
     */
    public Prozent(long wert) {
        this(BigDecimal.valueOf(wert));
    }

    /**
     * Legt ein Prozent-Objekt an.
     *
     * @param wert Prozentwert, z.B. 10 fuer 10 %
     */
    public Prozent(BigDecimal wert) {
        this.wert = wert;
    }

    /**
     * Die of-Methode liefert fuer dieselbe Zahl immer dasselbe Objekt zurueck.
     * Diese Methode lohnt sich daher, wenn man immer denselben Prozent-Wert
     * erzeugen will, um die Anzahl der Objekte gering zu halten.
     *
     * @param wert z.B. "19%"
     * @return "19%" als Prozent-Objekt
     */
    public static Prozent of(String wert) {
        return Prozent.of(toNumber(wert));
    }

    /**
     * Die of-Methode liefert fuer dieselbe Zahl immer dasselbe Objekt zurueck.
     * Diese Methode lohnt sich daher, wenn man immer denselben Prozent-Wert
     * erzeugen will, um die Anzahl der Objekte gering zu halten.
     *
     * @param wert z.B. "19%"
     * @return "19%" als Prozent-Objekt
     */
    public static Prozent of(long wert) {
        return Prozent.of(BigDecimal.valueOf(wert));
    }

    /**
     * Die of-Methode liefert fuer dieselbe Zahl immer dasselbe Objekt zurueck.
     * Diese Methode lohnt sich daher, wenn man immer denselben Prozent-Wert
     * erzeugen will, um die Anzahl der Objekte gering zu halten.
     *
     * @param wert z.B. 19
     * @return "19%" als Prozent-Objekt
     */
    public static Prozent of(BigDecimal wert) {
        return WEAK_CACHE.computeIfAbsent(wert, Prozent::new);
    }

    /**
     * Liefert den eigentlichen Prozentwert, der vor dem Prozentzeichen steht.
     *
     * @return z.B. 19 fuer 19% MwSt.
     */
    public BigDecimal getWert() {
        return this.wert;
    }

    /**
     * Diese Methode liefert den mathematischen Wert als BigDecimal zurueck,
     * mit dem dann weitergerechnet werden kann. D.h. 19% wird dann als '0.19'
     * zurueckgegeben.
     *
     * @return die Zahl als {@link BigDecimal}
     */
    @Override
    public BigDecimal toBigDecimal() {
        return this.wert.divide(BigDecimal.valueOf(100));
    }

    /**
     * Fuehrt eine einfache Prozent-Rechnung aus. D.h. '10% * 42 = 4.2'.
     *
     * @param x Multiplikant
     * @return x * Prozentwert / 100
     */
    public BigDecimal multiply(BigDecimal x) {
        return x.multiply(toBigDecimal());
    }

    /**
     * Fuehrt eine einfache Prozent-Rechnung aus. D.h. '10% * 42 = 4.2'.
     *
     * @param x Multiplikant
     * @return x * Prozentwert / 100
     */
    public BigDecimal multiply(long x) {
        return multiply(BigDecimal.valueOf(x));
    }

    @Override
    public String toString() {
        return this.wert + "%";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Prozent)) {
            return false;
        }
        Prozent other = (Prozent) obj;
        return Objects.equals(this.wert, other.wert);
    }

    @Override
    public int hashCode() {
        return this.wert.hashCode();
    }

}

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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * (c)reated 24.01.2018 by oboehm (ob@oasd.de)
 */
package de.jfachwert.math;

import de.jfachwert.Fachwert;
import de.jfachwert.pruefung.exception.InvalidValueException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

/**
 * Die Klasse Nummer dient zum Abspeichern einer beliebigen Nummer. Eine Nummer
 * ist eine positive Ganzzahl und beginnt ueblicherweise mit 1. Dabei kann es
 * sich um eine laufende Nummer, Start-Nummer, Trikot-Nummer, ... handeln.
 * <p>
 * Die Klasse ist Speicher-optimiert, um auch eine große Zahl von Nummern im
 * Speicher halten zu koennen. Und man kann damit auch Zahlen mit fuehrenden
 * Nullen (wie z.B. PLZ) abbilden.
 * </p>
 * <p>
 * Urspruenglich war diese Klasse als Ergaenzung zur {@link de.jfachwert.Text}-
 * Klasse gedacht.
 * </p>
 *
 * @author oboehm
 * @since 0.6 (24.01.2018)
 */
public class Nummer extends AbstractNumber implements Fachwert {

    private static final Nummer[] CACHE = new Nummer[11];
    private final PackedDecimal code;

    static {
        for (int i = 0; i < CACHE.length; i++) {
            CACHE[i] = new Nummer(i);
        }
    }

    /**
     * Erzeugt eine Nummer als positive Ganzzahl.
     * 
     * @param code eine Zahl, z.B. 42
     */
    public Nummer(long code) {
        this(BigInteger.valueOf(code));
    }

    /**
     * Wandelt den angegebene String in eine Zahl um.
     * 
     * @param code z.B. "42"
     */
    public Nummer(String code) {
        this.code = PackedDecimal.valueOf(code);
    }

    /**
     * Erzeugt eine beliebige Gleitkomma- oder Ganzzahl.
     * 
     * @param code eine beliebige Zahl
     */
    public Nummer(BigInteger code) {
        this(code.toString());
    }

    /**
     * Die of-Methode liefert fuer kleine Nummer immer dasselbe Objekt zurueck.
     * Vor allem wenn man nur kleinere Nummern hat, lohnt sich der Aufruf
     * dieser Methode.
     *
     * @param code Nummer
     * @return eine (evtl. bereits instanziierte) Nummer
     */
    public static Nummer of(long code) {
        if ((code >= 0) && (code < CACHE.length)) {
            return CACHE[(int) code];
        } else {
            return new Nummer(code);
        }
    }

    /**
     * Die of-Methode liefert fuer kleine Nummer immer dasselbe Objekt zurueck.
     * Vor allem wenn man nur kleinere Nummern hat, lohnt sich der Aufruf
     * dieser Methode.
     *
     * @param code Nummer
     * @return eine (evtl. bereits instanziierte) Nummer
     */
    public static Nummer of(String code) {
        return of(new Nummer(code));
    }

    /**
     * Die of-Methode liefert fuer kleine Nummer immer dasselbe Objekt zurueck.
     * Vor allem wenn man nur kleinere Nummern hat, lohnt sich der Aufruf
     * dieser Methode.
     *
     * @param code Nummer
     * @return eine (evtl. bereits instanziierte) Nummer
     */
    public static Nummer of(BigInteger code) {
        return of(code.longValue());
    }

    /**
     * Die of-Methode liefert fuer kleine Nummer immer dasselbe Objekt zurueck.
     * Vor allem wenn man nur kleinere Nummern hat, lohnt sich der Aufruf
     * dieser Methode.
     * <p>
     * Diese Methode dient dazu, um ein "ueberfluessige" Nummern, die
     * durch Aufruf anderer Methoden entstanden sind, dem Garbage Collector
     * zum Aufraeumen zur Verfuegung zu stellen.
     * </p>
     *
     * @param other andere Nummer
     * @return eine (evtl. bereits instanziierte) Nummer
     */
    public static Nummer of(Nummer other) {
        return of(other.longValue());
    }

    /**
     * Ueberprueft, ob der uebergebene String auch tatsaechlich eine Zahl ist.
     * 
     * @param nummer z.B. "4711"
     * @return validierter String zur Weiterverarbeitung
     */
    public static String validate(String nummer) {
        try {
            return new BigInteger(nummer).toString();
        } catch (NumberFormatException nfe) {
            throw new InvalidValueException(nummer, "number");
        }
    }

    /**
     * Diese Methode liefert die Zahl als BigDecimal zurueck und wird fuer
     * die Default-Implementierung der Number-Methoden benoetigt.
     *
     * @return die Zahl als {@link BigDecimal}
     */
    @Override
    public BigDecimal toBigDecimal() {
        return code.toBigDecimal();
    }

    /**
     * Liefert die Zahl als Integer zurueck.
     *
     * @return z.B. 42
     */
    public int intValue() {
        return code.intValue();
    }

    /**
     * Liefert die Zahl als 'long' zurueck.
     *
     * @return z.B. 42L
     */
    public long longValue() {
        return code.longValue();
    }

    /**
     * Zwei Nummer sind dann gleich, wenn sie exact gleich geschrieben werden.
     * D.h. fuehrende Nullen werden beim Vergleich nicht ignoriert.
     * 
     * @param obj Vergleichsobjekt
     * @return true oder false
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Nummer)) {
            return false;
        }
        Nummer other = (Nummer) obj;
        return this.toString().equals(other.toString());
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    /**
     * Hier wird die Nummer inklusive fuehrende Null (falls vorhanden)
     * ausgegeben.
     * 
     * @return z.B. "0711"
     */
    @Override
    public String toString() {
        return this.code.toString();
    }

}

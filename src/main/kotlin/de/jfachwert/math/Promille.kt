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
 * (c)reated 02.10.2019 by oboehm (ob@jfachwert.de)
 */
package de.jfachwert.math;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.WeakHashMap;

/**
 * Die Klasse Prozent steht fuer den Tausendsten Teil einer Zahl.
 * Sie kann wie die {@link Prozent}-Klasse fuer Berechnungen eingesetzt
 * werden.
 *
 * @author oboehm
 * @since 3.0 (02.10.2019)
 */
public class Promille extends Prozent {

    private static final WeakHashMap<BigDecimal, Promille> WEAK_CACHE = new WeakHashMap<>();
    private static final char PROMILLE_ZEICHEN = '\u2030';

    /** Konstante fuer "0 Promille". */
    public static final Promille ZERO = Promille.of(BigDecimal.ZERO);

    /** Konstante fuer "1 Promille". */
    public static final Promille ONE = Promille.of(BigDecimal.ONE);

    /** Konstante fuer "10 Promillle". */
    public static final Promille TEN = Promille.of(BigDecimal.TEN);

    /**
     * Legt ein Promille-Objekt an.
     *
     * @param wert Promille-Wert, z.B. "10" fuer 10 °/oo
     */
    public Promille(String wert) {
        this(toNumber(wert));
    }

    /**
     * Legt ein Promille-Objekt an.
     *
     * @param wert Promille-Wert, z.B. 10 fuer 10 °/oo
     */
    public Promille(long wert) {
        super(wert);
    }

    /**
     * Legt ein Promille-Objekt an.
     *
     * @param wert Promille-Wert, z.B. 10 fuer 10 °/oo
     */
    public Promille(BigDecimal wert) {
        super(wert);
    }

    /**
     * Die of-Methode liefert fuer dieselbe Zahl immer dasselbe Objekt zurueck.
     * Diese Methode lohnt sich daher, wenn man immer denselben Promille-Wert
     * erzeugen will, um die Anzahl der Objekte gering zu halten.
     *
     * @param wert z.B. "0.8"
     * @return "0.8 °/oo" als Promille-Objekt
     */
    public static Promille of(String wert) {
        return Promille.of(toNumber(wert));
    }

    private static BigDecimal toNumber(String s) {
        String number = StringUtils.replaceChars(s, "°/o" + PROMILLE_ZEICHEN, "").trim();
        return new BigDecimal(number);
    }

    /**
     * Die of-Methode liefert fuer dieselbe Zahl immer dasselbe Objekt zurueck.
     * Diese Methode lohnt sich daher, wenn man immer denselben Promille-Wert
     * erzeugen will, um die Anzahl der Objekte gering zu halten.
     *
     * @param wert z.B. 2
     * @return "2 °/oo" als Promille-Objekt
     */
    public static Promille of(long wert) {
        return Promille.of(BigDecimal.valueOf(wert));
    }

    /**
     * Die of-Methode liefert fuer dieselbe Zahl immer dasselbe Objekt zurueck.
     * Diese Methode lohnt sich daher, wenn man immer denselben Promille-Wert
     * erzeugen will, um die Anzahl der Objekte gering zu halten.
     *
     * @param wert z.B. 0.8
     * @return "0.8 °/oo" als Promille-Objekt
     */
    public static Promille of(BigDecimal wert) {
        return WEAK_CACHE.computeIfAbsent(wert, Promille::new);
    }

    /**
     * Diese Methode liefert den mathematischen Wert als BigDecimal zurueck,
     * mit dem dann weitergerechnet werden kann. D.h. 1 Promille wird dann als
     * '0.001' zurueckgegeben.
     *
     * @return die Zahl als {@link BigDecimal}
     */
    @Override
    public BigDecimal toBigDecimal() {
        return getWert().divide(BigDecimal.valueOf(1000));
    }

    @Override
    public String toString() {
        return this.getWert() + Character.toString(PROMILLE_ZEICHEN);
    }

}

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
 * (c)reated 04.04.2018 by oboehm (ob@oasd.de)
 */
package de.jfachwert.math;

import de.jfachwert.Fachwert;

import java.lang.ref.SoftReference;
import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Eine Primzahl ist eine natuerliche Zahl, die nur durch 1 und durch sich
 * selbst teilbar ist. Die kleinste Primzahl ist 2.
 * <p>
 * Die groesste Primzahl, die mit dieser Klasse dargestellt werden kann, ist
 * 9223372036854775783, da intern long verwendet wird. Ein BigInteger macht
 * aktuell keinen Sinn, da die Ermittlung einer beliebig grossen Zahl recht
 * lange dauert.
 * </p>
 *
 * @author oboehm
 * @since 0.6.1 (04.04.2018)
 */
public class Primzahl implements Fachwert {

    /** Zwei ist die kleinste Primzahl. */
    public static final Primzahl ZWEI = new Primzahl(2);

    /** Drei ist die naechste Primzahl. */
    public static final Primzahl DREI = new Primzahl(3);

    private static SoftReference<List<Primzahl>> refPrimzahlen = new SoftReference<>(initPrimzahlen());
    private final long value;

    private static List<Primzahl> initPrimzahlen() {
        List<Primzahl> primzahlen = new CopyOnWriteArrayList<>();
        primzahlen.add(DREI);
        return primzahlen;
    }

    private Primzahl(long n) {
        this.value = n;
    }

    /**
     * Liefert die erste Primzahl.
     * 
     * @return #ZWEI
     */
    public static Primzahl first() {
        return ZWEI;
    }

    /**
     * Liefert den numerischen Wert der Primzahl. Der Name der Methode
     * orientiert sich dabei an die Number-Klasse aus Java.
     * 
     * @return numrischer Wert
     */
    public long longValue() {
        return value;
    }

    /**
     * Liefert den numerischen Wert der Primzahl als {@link BigInteger}. Der 
     * Name der Methode orientiert sich dabei an die BigDecimal-Klasse aus
     * Java.
     *
     * @return numrischer Wert
     */
    public BigInteger toBigInteger() {
        return BigInteger.valueOf(longValue());
    }

    /**
     * Liefert die naechste Primzahl.
     * 
     * @return naechste Primzahl
     */
    public Primzahl next() {
        return after(longValue());
    }

    /**
     * Liefert die naechste Primzahl nach der angegebenen Zahl.
     *
     * @param zahl Zahl
     * @return naechste Primzahl > zahl
     */
    public static Primzahl after(long zahl) {
        List<Primzahl> primzahlen = refPrimzahlen.get();
        if (primzahlen == null) {
            primzahlen = initPrimzahlen();
            refPrimzahlen = new SoftReference<>(primzahlen);
        }
        for (Primzahl p : primzahlen) {
            if (zahl < p.longValue()) {
                return p;
            }
        }
        for (long n = primzahlen.get(primzahlen.size() - 1).longValue() + 2; n <= zahl; n += 2) {
            if (!hasTeiler(n)) {
                primzahlen.add(new Primzahl(n));
            }
        }
        long n = primzahlen.get(primzahlen.size() - 1).longValue() + 2;
        while (hasTeiler(n)) {
            n += 2;
        }
        Primzahl nextPrimzahl = new Primzahl(n);
        primzahlen.add(nextPrimzahl);
        return nextPrimzahl;
    }

    private static boolean hasTeiler(long n) {
        for (Primzahl p : refPrimzahlen.get()) {
            long teiler = p.longValue();
            if (n % teiler == 0) {
                return true;
            }
            if (teiler * teiler > n) {
                break;
            }
        }
        return false;
    }

    /**
     * Als Ausgabe nehmen wir die Zahl selbst.
     * 
     * @return die Zahl selbst
     */
    @Override
    public String toString() {
        return Long.toString(longValue());
    }

}

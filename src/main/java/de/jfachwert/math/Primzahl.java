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
 * Intern wird 'int' zur Speicherung der Primzahl verwendet, da dies fuer den
 * Standard-Fall ausreichend ist. So benoetigt bereits die Ermittlung einer
 * 8-stelligen Primzahl (&gt; 10 Mio.) ca. 3 Minuten. Die Emittlung einer
 * 10-stelligen Primzahl (&lt; 2 Mrd.) dürfte damit im Stunden, wenn nicht gar
 * Tage-Bereich liegen.
 * </p>
 * <p>
 * Die groesste Primzahl, die mit einem long dargestellt werden kann, ist
 * 9223372036854775783.
 * </p>
 *
 * @author oboehm
 * @since 0.6.1 (04.04.2018)
 */
public class Primzahl extends Number implements Fachwert {

    /** Zwei ist die kleinste Primzahl. */
    public static final Primzahl ZWEI = new Primzahl(2);

    /** Drei ist die naechste Primzahl. */
    public static final Primzahl DREI = new Primzahl(3);

    private static SoftReference<List<Primzahl>> refPrimzahlen = new SoftReference<>(initPrimzahlen());
    private final int value;

    private static List<Primzahl> initPrimzahlen() {
        List<Primzahl> primzahlen = new CopyOnWriteArrayList<>();
        primzahlen.add(DREI);
        return primzahlen;
    }

    private Primzahl(int n) {
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
     * @return numerischer Wert
     */
    public long longValue() {
        return value;
    }

    /**
     * Liefert den numerischen Wert der Primzahl. Der Name der Methode
     * orientiert sich dabei an die Number-Klasse aus Java.
     *
     * @return numerischer Wert
     */
    public int intValue() {
        return value;
    }

    /**
     * Liefert die Zahl als ein {@code float} zurueck.
     *
     * @return den numerischen Wert als {@code float}
     * @since 0.6.2
     */
    @Override
    public float floatValue() {
        return toBigInteger().floatValue();
    }

    /**
     * Liefert die Zahl als ein {@code double} zurueck.
     *
     * @return den numerischen Wert als {@code double}
     * @since 0.6.2
     */
    @Override
    public double doubleValue() {
        return toBigInteger().doubleValue();
    }
    
    /**
     * Liefert den numerischen Wert der Primzahl als {@link BigInteger}. Der 
     * Name der Methode orientiert sich dabei an die BigDecimal-Klasse aus
     * Java.
     *
     * @return numerischer Wert
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
        return after(intValue());
    }

    /**
     * Liefert die naechste Primzahl nach der angegebenen Zahl.
     *
     * @param zahl Zahl
     * @return naechste Primzahl &gt; zahl
     */
    public static Primzahl after(int zahl) {
        List<Primzahl> primzahlen = getPrimzahlen();
        for (Primzahl p : primzahlen) {
            if (zahl < p.intValue()) {
                return p;
            }
        }
        for (int n = primzahlen.get(primzahlen.size() - 1).intValue() + 2; n <= zahl; n += 2) {
            if (!hasTeiler(n)) {
                primzahlen.add(new Primzahl(n));
            }
        }
        int n = primzahlen.get(primzahlen.size() - 1).intValue() + 2;
        while (hasTeiler(n)) {
            n += 2;
        }
        Primzahl nextPrimzahl = new Primzahl(n);
        primzahlen.add(nextPrimzahl);
        return nextPrimzahl;
    }

    /**
     * Ermittelt, ob die uebergebene Zahl einen Teiler hat.
     * Alternative Implementierung mit Streams zeigten ein deutlich
     * schlechteres Zeitverhalten (ca. Faktor 10 langsamer). Eine weitere
     * Implementierung mit ParallelStreams war noch langsamer - vermutlich
     * ist der Overhaed einfach zu gross.
     * 
     * @param n Zahl, die nach einem Teiler unterscuht wird
     * @return true, falls Zahl einen Teiler hat (d.h. keine Primzahl ist)
     */
    private static boolean hasTeiler(int n) {
        for (Primzahl p : getPrimzahlen()) {
            int teiler = p.intValue();
            if (n % teiler == 0) {
                return true;
            }
            if (teiler * teiler > n) {
                break;
            }
        }
        return false;
    }

    private static List<Primzahl> getPrimzahlen() {
        List<Primzahl> primzahlen = refPrimzahlen.get();
        if (primzahlen == null) {
            primzahlen = initPrimzahlen();
            refPrimzahlen = new SoftReference<>(primzahlen);
        }
        return primzahlen;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Primzahl primzahl = (Primzahl) o;
        return value == primzahl.value;
    }

    @Override
    public int hashCode() {
        return value;
    }

    /**
     * Als Ausgabe nehmen wir die Zahl selbst.
     * 
     * @return die Zahl selbst
     */
    @Override
    public String toString() {
        return Integer.toString(value);
    }

}

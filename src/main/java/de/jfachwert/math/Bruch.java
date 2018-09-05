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
 * (c)reated 02.04.18 by oliver (ob@oasd.de)
 */
package de.jfachwert.math;

import de.jfachwert.Fachwert;
import de.jfachwert.pruefung.exception.LocalizedIllegalArgumentException;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Die Bruch-Klasse repraesentiert eine mathematischen Bruch mit Zaehler und
 * Nenner. Als Zaehler und Nenner werden dabei nur ganzzahlige Werte
 * akzeptiert, da sich Gleitkommazahlen auch immer als Brueche darstellen
 * lassen.
 * <p>
 * Die Namen der Methoden orientieren sich teilweise an den Methodennamen von
 * BigInteger und BigDecimal und sind daher auf englisch. Andere Namen wie
 * {@link #kuerzen()} sind dagegen auf deutsch.
 * </p>
 *
 * @author <a href="ob@aosd.de">oliver</a>
 * @since 0.6
 */
public class Bruch extends AbstractNumber implements Fachwert, Comparable<Bruch> {

    private final BigInteger zaehler;
    private final BigInteger nenner;

    /**
     * Legt einen Bruch mit dem angegeben Zaehler und Nenner an. Brueche
     * koennen dabei mit Bruchstrich ("1/2") oder als Dezimalzahl ("0.5")
     * angegeben werden.
     *
     * @param bruch Zeichenkette, z.B. "1/2" ocer "0.5"
     */
    public Bruch(String bruch) {
        this(toNumbers(bruch));
    }

    private static BigInteger[] toNumbers(String bruch) {
        BigInteger[] numbers = new BigInteger[2];
        String[] parts = StringUtils.split(bruch, "/");
        switch (parts.length) {
            case 1:
                Bruch dezimalBruch = toBruch(new BigDecimal(parts[0]));
                numbers[0] = dezimalBruch.getZaehler();
                numbers[1] = dezimalBruch.getNenner();
                break;
            case 2:
                numbers[0] = new BigInteger(parts[0]);
                numbers[1] = new BigInteger(parts[1]);
                break;
            default:
                throw new LocalizedIllegalArgumentException(bruch, "fraction");
        }
        return numbers;
    }

    /**
     * Legt die uebergebene Gleitkommazahl als Bruch an.
     *
     * @param number Dezimalzahl, z.B. 0.5
     */
    public Bruch(double number) {
        this(BigDecimal.valueOf(number));
    }

    /**
     * Legt die uebergebene Dezimalzahl als Bruch an.
     *
     * @param decimal Dezimalzahl, z.B. 0.5
     */
    public Bruch(BigDecimal decimal) {
        this(toNumbers(decimal));
    }

    private static BigInteger[] toNumbers(BigDecimal decimal) {
        BigInteger[] numbers = new BigInteger[2];
        Bruch dezimalBruch = toBruch(decimal);
        numbers[0] = dezimalBruch.getZaehler();
        numbers[1] = dezimalBruch.getNenner();
        return numbers;
    }

    private static Bruch toBruch(BigDecimal decimal) {
        int scale = decimal.scale();
        BigInteger z = decimal.movePointRight(scale).toBigInteger();
        BigInteger n = BigDecimal.ONE.movePointRight(scale).toBigInteger();
        return Bruch.of(z, n).kuerzen();
    }

    private Bruch(BigInteger[] number) {
        this(number[0], number[1]);
    }

    /**
     * Legt einen Bruch mit dem angegeben Zaehler und Nenner an.
     *
     * @param zaehler Zaehler
     * @param nenner Nenner
     */
    public Bruch(long zaehler, long nenner) {
        this(BigInteger.valueOf(zaehler), BigInteger.valueOf(nenner));
    }

    /**
     * Legt einen Bruch mit dem angegeben Zaehler und Nenner an.
     *
     * @param zaehler Zaehler
     * @param nenner Nenner
     */
    public Bruch(BigInteger zaehler, BigInteger nenner) {
        this.zaehler = zaehler;
        this.nenner = nenner;
    }

    /**
     * Liefert einen Bruch mit dem angegeben Zaehler und Nenner an.
     *
     * @param bruch Zeichenkette, z.B. "1/2"
     * @return Bruch
     */
    public static Bruch of(String bruch) {
        return new Bruch(bruch);
    }

    /**
     * Liefert einen Bruch mit dem angegeben Zaehler und Nenner an.
     *
     * @param zaehler Zaehler
     * @param nenner Nenner
     * @return Bruch
     */
    public static Bruch of(long zaehler, long nenner) {
        return new Bruch(zaehler, nenner);
    }

    /**
     * Liefert einen Bruch mit dem angegeben Zaehler und Nenner an.
     *
     * @param zaehler Zaehler
     * @param nenner Nenner
     * @return Bruch
     */
    public static Bruch of(BigInteger zaehler, BigInteger nenner) {
        return new Bruch(zaehler, nenner);
    }

    /**
     * Rueckgabe des Zahlers.
     *
     * @return Zahl
     */
    public BigInteger getZaehler() {
        return zaehler;
    }

    /**
     * Rueckgabe des Nenners.
     *
     * @return Zahl
     */
    public BigInteger getNenner() {
        return nenner;
    }

    /**
     * Liefert einen gekuerzten Bruch zurueck. So wird z.B. der Bruch "2/4" als
     * "1/2" zurueckgegeben.
     *
     * @return gekuerzter Bruch
     */
    public Bruch kuerzen() {
        BigInteger z = getZaehler();
        BigInteger n = getNenner();
        for (Primzahl p = Primzahl.first(); p.toBigInteger().compareTo(n) < 0; p = p.next()) {
            BigInteger teiler = p.toBigInteger();
            while (z.mod(teiler).equals(BigInteger.ZERO) && (n.mod(teiler).equals(BigInteger.ZERO))) {
                z = z.divide(teiler);
                n = n.divide(teiler);
            }
        }
        return Bruch.of(z, n);
    }

    /**
     * Liefert den (ungekuerzten) Kehrwert des Bruches.
     *
     * @return Kehrwert
     */
    public Bruch kehrwert() {
        return Bruch.of(getNenner(), getZaehler());
    }

    /**
     * Liefert einen (ungekuerzten) negierten Kehrwert zurueck.
     *
     * @return negierter Bruch
     */
    public Bruch negate() {
        return Bruch.of(getZaehler().negate(), getNenner());
    }

    /**
     * Multiplikation zweier Brueche.
     *
     * @param operand der zweite Bruch, mit dem multipliziert wird.
     * @return mulitiplizierter Bruch, evtl. gekuerzt
     */
    public AbstractNumber multiply(Bruch operand) {
        BigInteger z = getZaehler().multiply(operand.getZaehler());
        BigInteger n = getNenner().multiply(operand.getNenner());
        return Bruch.of(z, n).kuerzen();
    }

    /**
     * Die Division zweier Brueche laesst sich bekanntlich auf die
     * Multiplikation des Bruches mit dem Kehrwert zurueckfuehren.
     * Dies wird hier fuer die Division ausgenutzt.
     *
     * @param operand der zweite Bruch, durch den geteilt wird
     * @return dividierter Bruch, evtl. gekuerzt
     */
    public AbstractNumber divide(Bruch operand) {
        return multiply(operand.kehrwert());
    }

    /**
     * Addition zweier Brueche.
     *
     * @param operand der zweite Bruch, der addiert wird.
     * @return addierter Bruch, evtl. gekuerzt
     */
    public AbstractNumber add(Bruch operand) {
        BigInteger n = getNenner().multiply(operand.getNenner());
        BigInteger z1 = getZaehler().multiply(operand.getNenner());
        BigInteger z2 = operand.getZaehler().multiply(getNenner());
        return Bruch.of(z1.add(z2), n).kuerzen();
    }

    /**
     * Subtraktion zweier Brueche.
     *
     * @param operand der zweite Bruch, der subtrahiert wird.
     * @return subtrahierter Bruch, evtl. gekuerzt
     */
    public AbstractNumber subtract(Bruch operand) {
        return add(operand.negate());
    }

    @Override
    public String toString() {
        return getZaehler() + "/" + getNenner();
    }

    /**
     * Fuer die equals-Implementierung gilt, dass der Wert verglichen wird.
     * D.h. bei "1/2" und "2/4" handelt es sich um die gleichen Brueche.
     *
     * @param obj der andere Bruch
     * @return true oder false
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Bruch)) {
            return false;
        }
        Bruch other = ((Bruch) obj).kuerzen();
        Bruch gekuerzt = this.kuerzen();
        return gekuerzt.getZaehler().equals(other.getZaehler()) && gekuerzt.getNenner().equals(other.getNenner());
    }

    /**
     * Eine einfache Hashcode-Implementierung, die sich auf toString() abstuetzt.
     *
     * @return hashcode
     */
    @Override
    public int hashCode() {
        return this.kuerzen().toString().hashCode();
    }

    /**
     * Vergleicht den anderen Bruch mit dem aktuellen Bruch.
     *
     * @param other der andere Bruch, der verglichen wird.
     * @return negtive Zahl, falls this &lt; other, 0 bei Gleichheit, ansonsten
     * positive Zahl.
     */
    @Override
    public int compareTo(Bruch other) {
        BigInteger thisZaehlerErweitert = this.zaehler.multiply(other.nenner);
        BigInteger otherZaehlerErweitert = other.zaehler.multiply(this.nenner);
        return thisZaehlerErweitert.compareTo(otherZaehlerErweitert);
    }

    /**
     * Liefert die gepackte Dezimalzahl wieder als {@link BigDecimal} zurueck.
     *
     * @return gepackte Dezimalzahl als {@link BigDecimal}
     * @since 0.7
     */
    public BigDecimal toBigDecimal() {
        return new BigDecimal(this.zaehler).divide(new BigDecimal(this.nenner));
    }

}

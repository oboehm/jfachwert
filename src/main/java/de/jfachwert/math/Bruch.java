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

import java.math.BigInteger;
import java.util.Objects;

/**
 * Die Bruch-Klasse repraesentiert eine mathematischen Bruch mit Zaehler und
 * Nenner. Als Zaehler und Nenner werden dabei nur ganzzahlige Werte
 * akzeptiert, da sich Gleitkommazahlen auch immer als Brueche darstellen
 * lassen.
 *
 * @author <a href="ob@aosd.de">oliver</a>
 * @since 0.6
 */
public class Bruch implements Fachwert {

    private final BigInteger zaehler;
    private final BigInteger nenner;

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
     * @param zaehler Zaehler
     * @param nenner Nenner
     */
    public static Bruch of(long zaehler, long nenner) {
        return new Bruch(zaehler, nenner);
    }

    /**
     * Liefert einen Bruch mit dem angegeben Zaehler und Nenner an.
     *
     * @param zaehler Zaehler
     * @param nenner Nenner
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
        for (BigInteger teiler = BigInteger.valueOf(2); teiler.compareTo(n) < 0; teiler = teiler.add(BigInteger.ONE)) {
            while (z.mod(teiler).equals(BigInteger.ZERO) && (n.mod(teiler).equals(BigInteger.ZERO))) {
                z = z.divide(teiler);
                n = n.divide(teiler);
            }
        }
        return Bruch.of(z, n);
    }

    @Override
    public String toString() {
        return getZaehler() + "/" + getNenner();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Bruch)) {
            return false;
        }
        Bruch other = (Bruch) obj;
        return Objects.equals(zaehler, other.zaehler) &&
                Objects.equals(nenner, other.nenner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(zaehler, nenner);
    }

}

/*
 * Copyright (c) 2017 by Oliver Boehm
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
 * (c)reated 21.02.2017 by oboehm (ob@oasd.de)
 */
package de.jfachwert.post;

import de.jfachwert.*;
import de.jfachwert.pruefung.*;

import java.math.*;

/**
 * Ein Postfach besteht aus einer Nummer ohne fuehrende Nullen und einer
 * Postleitzahl mit Ortsangabe. Die Nummer selbst ist optional, wenn die
 * durch die Postleitzahl bereits das Postfach abgebildet wird.
 *
 * @author oboehm
 * @since 0.2 (19.06.2017)
 */
public class Postfach implements Fachwert {

    private final BigInteger nummer;
    private final Ort ort;

    /**
     * Erzeugt ein Postfach.
     *
     * @param nummer positive Zahl ohne fuehrende Null
     * @param ort gueltiger Ort mit PLZ
     */
    public Postfach(long nummer, Ort ort) {
        this(BigInteger.valueOf(nummer), ort);
    }

    /**
     * Erzeugt ein Postfach.
     *
     * @param nummer positive Zahl ohne fuehrende Null
     * @param ort gueltiger Ort mit PLZ
     */
    public Postfach(BigInteger nummer, Ort ort) {
        this.nummer = nummer;
        this.ort = ort;
        validate(nummer, ort);
    }

    /**
     * Validiert das uebergebene Postfach auf moegliche Fehler.
     *
     * @param nummer    Postfach-Nummer (muss positiv sein)
     * @param ort       Ort mit PLZ
     */
    public static void validate(BigInteger nummer, Ort ort) {
        if (nummer.compareTo(BigInteger.ONE) < 0) {
            throw new InvalidValueException(nummer, "number");
        }
        if (!ort.getPLZ().isPresent()) {
            throw new InvalidValueException(ort, "postal_code");
        }
    }

    /**
     * Liefert die Postfach-Nummer als normale Zahl.
     *
     * @return z.B. 815
     */
    public BigInteger getNummer() {
        return this.nummer;
    }

    /**
     * Liefert die Postfach-Nummer als formattierte Zahl.
     *
     * @return z.B. "8 15"
     */
    public String getNummerFormatted() {
        BigInteger hundert = BigInteger.valueOf(100);
        StringBuilder formatted = new StringBuilder();
        for (BigInteger i = this.getNummer(); i.compareTo(BigInteger.ONE) > 0; i = i.divide(hundert)) {
            formatted.insert(0, " " + i.remainder(hundert));
        }
        return formatted.toString().trim();
    }

    /**
     * Liefert die Postleitzahl.
     *
     * @return z.B. 09876
     */
    public PLZ getPlz() {
        return this.ort.getPLZ().get();
    }

    /**
     * Liefert den Ort.
     *
     * @return Ort
     */
    public Ort getOrt() {
        return this.ort;
    }

    /**
     * Zwei Postfaecher sind gleich, wenn sie die gleiche Attribute haben.
     *
     * @param obj das andere Postfach
     * @return true bei Gleichheit
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Postfach)) {
            return false;
        }
        Postfach other = (Postfach) obj;
        return this.nummer.equals(other.nummer) && this.ort.equals(other.ort);
    }

    /**
     * Da die PLZ meistens bereits ein Postfach adressiert, nehmen dies als
     * Basis fuer die Hashcode-Implementierung.
     *
     * @return hashCode
     */
    @Override
    public int hashCode() {
        return this.getOrt().hashCode();
    }

    /**
     * Hierueber wird das Postfach einzeilig ausgegeben.
     *
     * @return z.B. "Postfach 8 15, 09876 Nirwana"
     */
    @Override
    public String toString() {
        return "Postfach " + this.getNummerFormatted() + ", " + this.getOrt();
    }

}

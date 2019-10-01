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

import de.jfachwert.Fachwert;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Die Klasse Prozent steht fuer den Hundersten Teil einer Zahl.
 * Sie kann wie jede andere Zahl fuer Berechnungen eingesetzt werden,
 * weswegen sie auch von {@link java.lang.Number} abgeleitet ist.
 *
 * @author oboehm
 * @since 3.0 (01.10.2019)
 */
public class Prozent extends AbstractNumber implements Fachwert {

    private final BigDecimal wert;

    /**
     * Legt ein Prozent-Objekt an.
     *
     * @param wert Prozentwert, z.B. "10" fuer 10 %
     */
    public Prozent(String wert) {
        this(new BigDecimal(wert));
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
     * Diese Methode liefert den Prozentwert als BigDecimal zurueck.
     *
     * @return die Zahl als {@link BigDecimal}
     */
    @Override
    public BigDecimal toBigDecimal() {
        return this.wert;
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

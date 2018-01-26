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
package de.jfachwert;

import de.jfachwert.pruefung.exception.InvalidValueException;

import java.math.BigDecimal;

/**
 * Die Klasse Nummer dient zum Abspeichern einer beliebigen Nummer. Sie ist
 * die Ergaenzung zur {@link Text}-Klasse.
 *
 * @author oboehm
 * @since x.6 (24.01.2018)
 */
public class Nummer extends AbstractFachwert<BigDecimal> {

    /**
     * Erzeugt eine Nummer als positive oder negative Ganzzahl.
     * 
     * @param code eine Zahl, z.B. 42
     */
    public Nummer(int code) {
        this(BigDecimal.valueOf(code));
    }

    /**
     * Wandelt den angegebene String in eine Zahl um.
     * 
     * @param code z.B. "42"
     */
    public Nummer(String code) {
        this(new BigDecimal(validate(code)));
    }

    /**
     * Erzeugt eine beliebige Gleitkomma- oder Ganzzahl.
     * 
     * @param code eine beliebige Zahl
     */
    public Nummer(BigDecimal code) {
        super(code);
    }

    /**
     * Ueberprueft, ob der uebergebene String auch tatsaechlich eine Zahl ist.
     * 
     * @param nummer z.B. "4711"
     * @return validierter String zur Weiterverarbeitung
     */
    public static String validate(String nummer) {
        try {
            return new BigDecimal(nummer).toString();
        } catch (NumberFormatException nfe) {
            throw new InvalidValueException(nummer, "number");
        }
    }

    /**
     * Liefert die Zahl als Integer zurueck.
     * 
     * @return z.B. 42
     */
    public int intValue() {
        return this.getCode().intValue();
    }
    
}

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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express orimplied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * (c)reated 24.03.2017 by oboehm (ob@jfachwert.de)
 */
package de.jfachwert.steuer;

import de.jfachwert.SimpleValidator;
import de.jfachwert.math.PackedDecimal;
import de.jfachwert.pruefung.LengthValidator;
import de.jfachwert.pruefung.NullValidator;

import java.util.WeakHashMap;

/**
 * Die steuerliche Identifikationsnummer (SteuerIdNr) ist eine
 * bundeseinheitliche und dauerhafte Identifikationsnummer von in
 * Deutschland gemeldeten Buergern für Steuerzwecke. Die SteuerIdNr
 * besteht aus insgesamt 11 Ziffern, wobei die letzte Ziffer eine
 * Pruefziffer ist.
 *
 * @author oboehm
 * @since 0.1.0
 */
public class SteuerIdNr extends Steuernummer {

    private static final Validator VALIDATOR = new Validator();
    private static final WeakHashMap<String, SteuerIdNr> WEAK_CACHE = new WeakHashMap<>();

    /** Null-Konstante fuer Initialisierungen. */
    public static final SteuerIdNr NULL = new SteuerIdNr("", new NullValidator<>());

    /**
     * Die SteuerIdNr ist eine 11-stellige Zahl mit einer Pruefziffer.
     *
     * @param idNr 11-stellige Zahl
     */
    public SteuerIdNr(String idNr) {
        this(idNr, VALIDATOR);
    }

    /**
     * Die SteuerIdNr ist eine 11-stellige Zahl mit einer Pruefziffer.
     *
     * @param idNr      11-stellige Zahl
     * @param validator zur Pruefung
     */
    public SteuerIdNr(String idNr, SimpleValidator<PackedDecimal> validator) {
        super(idNr, validator);
    }

    /**
     * Die SteuerIdNr ist eine 11-stellige Zahl mit einer Pruefziffer.
     *
     * @param idNr 11-stellige Zahl
     * @return SteuerIdNr
     */
    public static SteuerIdNr of(String idNr) {
        return WEAK_CACHE.computeIfAbsent(idNr, SteuerIdNr::new);
    }

    /**
     * Eine SteuerId muss genau 11 Stellen besitzen.
     *
     * @param nr the nr
     * @return the string
     */
    public static String validate(String nr) {
        return VALIDATOR.validate(nr);
    }



    /**
     * Eigener Validator fuer die SteuerIdNr-Validierung.
     *
     * @since 2.2
     */
    public static class Validator extends Steuernummer.Validator {

        @Override
        public String validate(String nr) {
            LengthValidator.validate(nr, 11);
            return super.validate(nr);
        }

    }

}

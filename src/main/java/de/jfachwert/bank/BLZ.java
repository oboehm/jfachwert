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
 * (c)reated 16.03.2017 by oboehm (ob@jfachwert.de)
 */
package de.jfachwert.bank;

import de.jfachwert.AbstractFachwert;
import de.jfachwert.SimpleValidator;
import de.jfachwert.math.PackedDecimal;
import de.jfachwert.pruefung.NullValidator;
import de.jfachwert.pruefung.NumberValidator;
import org.apache.commons.lang3.RegExUtils;

import java.util.WeakHashMap;

/**
 * Die BLZ (Bankleitzahl) ist eine eindeutige Kennziffer, die in Deutschland
 * und Oesterreich eindeutig ein Kreditinstitut identifiziert. In Deutschland
 * ist die BLZ eine 8-stellige, in Oesterreich eine 5-stellige Zahl (mit
 * Ausnahme der Oesterreichischen Nationalbank mit 3 Stellen).
 * <p>
 * Zur Reduzierung des internen Speicherverbrauchs wird die BLZ als
 * {@link PackedDecimal} abgelegt.
 * </p>
 *
 * @author oboehm
 * @since 16.03.2017
 */
public class BLZ extends AbstractFachwert<PackedDecimal, BLZ> {

    private static final WeakHashMap<String, BLZ> WEAK_CACHE = new WeakHashMap<>();
    private static final Validator VALIDATOR = new Validator();

    /** Null-Konstante fuer Initialisierungen. */
    public static final BLZ NULL = new BLZ("", new NullValidator<>());

    /**
     * Hierueber wird eine neue BLZ angelegt.
     *
     * @param code eine 5- oder 8-stellige Zahl
     */
    public BLZ(String code) {
        this(code, VALIDATOR);
    }

    /**
     * Hierueber wird eine neue BLZ angelegt.
     *
     * @param code      eine 5- oder 8-stellige Zahl
     * @param validator fuer die Ueberpruefung
     */
    public BLZ(String code, SimpleValidator<PackedDecimal> validator) {
        super(PackedDecimal.valueOf(code), validator);
    }

    /**
     * Hierueber wird eine neue BLZ angelegt.
     *
     * @param code eine 5- oder 8-stellige Zahl
     */
    public BLZ(int code) {
        this(Integer.toString(code));
    }

    /**
     * Eine BLZ darf maximal 8-stellig sein.
     *
     * @param blz die Bankleitzahl
     * @return die Bankleitzahl zur Weitervarabeitung
     * @deprecated bitte {@link Validator#validate(String)} verwenden
     */
    @Deprecated
    public static int validate(int blz) {
        VALIDATOR.validate(PackedDecimal.of(blz));
        return blz;
    }

    /**
     * Eine BLZ darf maximal 8-stellig sein.
     *
     * @param blz die Bankleitzahl
     * @return die Bankleitzahl zur Weiterverabeitung
     * @deprecated bitte {@link Validator#validate(String)} verwenden
     */
    @Deprecated
    public static String validate(String blz) {
        return VALIDATOR.validate(PackedDecimal.of(blz)).toString();
    }

    /**
     * Liefert eine BLZ zurueck.
     *
     * @param code maximal 8-stellige Nummer
     * @return die BLZ
     */
    public static BLZ of(int code) {
        return of(Integer.toString(code));
    }

    /**
     * Liefert eine BLZ zurueck.
     *
     * @param code maximal 8-stellige Nummer
     * @return die BLZ
     */
    public static BLZ of(String code) {
        return WEAK_CACHE.computeIfAbsent(code, BLZ::new);
    }

    /**
     * Liefert die unformattierte BLZ.
     *
     * @return unformattierte BLZ, z.B. "64090100"
     */
    public String getUnformatted() {
        return this.getCode().toString();
    }
    
    /**
     * Liefert die BLZ in 3er-Gruppen formattiert.
     *
     * @return formatierte LBZ, z.B. "640 901 00"
     */
    public String getFormatted() {
        String input = this.getUnformatted() + "   ";
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < this.getUnformatted().length(); i+= 3) {
            buf.append(input, i, i+3);
            buf.append(' ');
        }
        return buf.toString().trim();
    }


    /**
     * Dieser Validator ist fuer die Ueberpruefung von BLZs vorgesehen.
     *
     * @since 2.2
     */
    public static class Validator implements SimpleValidator<PackedDecimal> {

        private static final NumberValidator NUMBER_VALIDATOR = new NumberValidator(100, 99_999_999);

        /**
         * Eine BLZ darf maximal 8-stellig sein.
         *
         * @param blz die Bankleitzahl
         * @return die Bankleitzahl zur Weiterverabeitung
         */
        @Override
        public PackedDecimal validate(PackedDecimal blz) {
            String normalized = validate(blz.toString());
            return PackedDecimal.of(normalized);
        }

        /**
         * Eine BLZ darf maximal 8-stellig sein.
         *
         * @param blz die Bankleitzahl
         * @return die Bankleitzahl zur Weiterverabeitung
         */
        public String validate(String blz) {
            String normalized = RegExUtils.replaceAll(blz, "\\s", "");
            return NUMBER_VALIDATOR.validate(normalized);
        }

        /**
         * Eine BLZ darf maximal 8-stellig sein.
         *
         * @param blz die Bankleitzahl
         * @return die Bankleitzahl zur Weiterverabeitung
         */
        public int validate(int blz) {
            validate(Integer.toString(blz));
            return blz;
        }

    }

}

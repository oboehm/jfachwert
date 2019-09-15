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
 * (c)reated 13.03.2017 by oboehm (ob@jfachwert.de)
 */
package de.jfachwert.steuer;

import de.jfachwert.AbstractFachwert;
import de.jfachwert.PruefzifferVerfahren;
import de.jfachwert.SimpleValidator;
import de.jfachwert.math.PackedDecimal;
import de.jfachwert.pruefung.LengthValidator;
import de.jfachwert.pruefung.Mod11Verfahren;
import de.jfachwert.pruefung.NullValidator;

import java.util.WeakHashMap;

/**
 * Die Steuernummer oder Steuer-Identnummer ist eine eindeutige Nummer, die vom
 * Finanzamt vergeben wird. Die Nummer ist eindeutig einem Steuerpflichtigen
 * zugeordnet.
 *
 * Die Laenge der Steuernummer variierte beim Standardschema der Laender
 * zwischen 10 und 11 Ziffern und hatte für das Bundesschema einheitlich 13
 * Ziffern.
 *
 * Seit 2008 ist die Steuernummer durch die Steuer-Identifikationsnummer
 * abgeloest, die aus 10 Ziffer + Pruefziffer besteht. Diese Unterscheidung
 * wird in dieser Klasse aber (noch) nicht vorgenommen.
 * <p>
 * Zur Reduzierung des internen Speicherverbrauchs wird die BLZ als
 * {@link PackedDecimal} abgelegt.
 * </p>
 *
 * @author oboehm
 * @since 0.0.2
 */
public class Steuernummer extends AbstractFachwert<PackedDecimal, Steuernummer> {

    private static final Validator VALIDATOR = new Validator();
    private static final WeakHashMap<String, Steuernummer> WEAK_CACHE = new WeakHashMap<>();

    /** Null-Konstante fuer Initialisierungen. */
    public static final Steuernummer NULL = new Steuernummer("", new NullValidator<>());

    /**
     * Hierueber wird eine neue Steuernummer angelegt.
     *
     * @param nr eine 10- bis 13-stellige Steuernummer.
     */
    public Steuernummer(String nr) {
        this(nr, VALIDATOR);
    }

    /**
     * Dieser Konstruktor ist hauptsaechlich fuer abgeleitete Klassen gedacht,
     * damit diese das {@link PruefzifferVerfahren} ueberschreiben koennen.
     * Man kann es auch verwenden, um das PruefzifferVerfahren abzuschalten,
     * indem man das {@link de.jfachwert.pruefung.NoopVerfahren} verwendet.
     *
     * @param nr          die Steuernummer
     * @param pzVerfahren das verwendete PruefzifferVerfahren
     */
    public Steuernummer(String nr, SimpleValidator<PackedDecimal> pzVerfahren) {
        super(PackedDecimal.of(nr), pzVerfahren);
    }

    /**
     * Hierueber wird eine neue Steuernummer angelegt.
     *
     * @param nr eine 10- bis 13-stellige Steuernummer.
     * @return Steuernummer
     */
    public static Steuernummer of(String nr) {
        return WEAK_CACHE.computeIfAbsent(nr, Steuernummer::new);
    }

    /**
     * Die Steuernummer muss zwischen 10 und 13 Stellen lang sein und die
     * Pruefziffer muss stimmen (falls sie bekannt ist).
     *
     * @param nr die Steuernummer
     * @return die validierte Steuernummer zur Weiterverarbeitung
     */
    public static String validate(String nr) {
        return VALIDATOR.validate(nr);
    }

    /**
     * Die letzte Ziffer ist die Pruefziffer, die hierueber abgefragt werden
     * kann.
     *
     * @return Wert zwischen 0 und 9
     */
    public int getPruefziffer() {
        return VALIDATOR.getPruefziffer(this.getCode());
    }


    /**
     * Eigener Validator fuer die Steuernummern-Validierung.
     *
     * @since 2.2
     */
    public static class Validator implements SimpleValidator<PackedDecimal> {

        private static final PruefzifferVerfahren<String> MOD11 = new Mod11Verfahren(10);

        /**
         * Die Steuernummer muss zwischen 10 und 13 Stellen lang sein und die
         * Pruefziffer muss stimmen (falls sie bekannt ist).
         *
         * @param nr die Steuernummer
         * @return die validierte Steuernummer zur Weiterverarbeitung
         */
        @Override
        public PackedDecimal validate(PackedDecimal nr) {
            validate(nr.toString());
            return nr;
        }

        public String validate(String nr) {
            LengthValidator.validate(nr, 10, 13);
            if (nr.length() == 11) {
                return MOD11.verify(nr);
            }
            return nr;
        }

        /**
         * Die letzte Ziffer ist die Pruefziffer, die hierueber abgefragt werden
         * kann.
         *
         * @param nr Steuernummer
         * @return Wert zwischen 0 und 9
         */
        public int getPruefziffer(PackedDecimal nr) {
            return Integer.parseInt(MOD11.getPruefziffer(nr.toString()));
        }

    }
    
}

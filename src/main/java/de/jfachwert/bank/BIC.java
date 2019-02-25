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
 * (c)reated 11.03.17 by oliver (ob@oasd.de)
 */
package de.jfachwert.bank;

import de.jfachwert.SimpleValidator;
import de.jfachwert.Text;
import de.jfachwert.pruefung.NullValidator;
import de.jfachwert.pruefung.exception.InvalidLengthException;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.WeakHashMap;

/**
 * BIC steht fuer Bank (oder auch Businiess) Identifier Code und kennzeichnet
 * weltweit Kreditinstitute, Broker oder aehnliche Unternehmen. Im Allegemeinen
 * wird die BIC im Zahlungsverkehr zusammen mit der IBAN verwendet.
 * <p>
 *     Der BIC hat eine Laenge von 11 oder 14 alphanumerischen Zeichen mit
 *     folgendem Aufbau: BBBBCCLLbbb
 * </p>
 * <ul>
 *     <li>
 *         BBBB: 4-stelliger Bankcode, vom Geldinstitut frei waehlbar
 *         (nur Buchstaben)
 *     </li>
 *     <li>
 *         CC: 2-stelliger Laendercode nach ISO 3166-1 (nur Buchstaben)
 *     </li>
 *     <li>
 *         LL: 2-stellige Codierung des Ortes (Buchstaben/Ziffern)
 *     </li>
 *     <li>
 *         bbb: 3-stellige Kennzeichnung (Branche-Code) der Filiale oder
 *         Abteilung. Kann um "XXX" auf 6-stellig ergaenzt werden
 *         (Buchstaben/Ziffern)
 *     </li>
 * </ul>
 *
 * @author <a href="ob@aosd.de">oliver</a>
 */
public class BIC extends Text {

    private static final WeakHashMap<String, BIC> WEAK_CACHE = new WeakHashMap<>();
    private static final SimpleValidator<String> VALIDATOR = new Validator();

    /** Null-Konstante fuer Initialisierungen. */
    public static final BIC NULL = new BIC("", new NullValidator<>());

    /**
     * Hierueber wird eine neue BIC angelegt.
     *
     * @param code eine 11- oder 14-stellige BIC
     */
    public BIC(String code) {
        this(code, VALIDATOR);
    }

    /**
     * Hierueber wird eine neue BIC angelegt.
     *
     * @param code      eine 11- oder 14-stellige BIC
     * @param validator zum Pruefen der BIC
     */
    public BIC(String code, SimpleValidator<String> validator) {
        super(code, validator);
    }

    /**
     * Hierueber kann man eine BIC ohne den Umweg ueber den Konstruktor
     * validieren.
     *
     * @param bic die BIC (11- oder 14-stellig)
     * @return die validierte BIC (zur Weiterverarbeitung)
     * @deprecated bitte {@link Validator#validate(String)} verwenden
     */
    @Deprecated
    public static String validate(String bic) {
        return VALIDATOR.validate(bic);
    }

    /**
     * Liefert eine BIC zurueck.
     *
     * @param code eine 11- oder 14-stellige BIC
     * @return Text
     */
    public static BIC of(String code) {
        return WEAK_CACHE.computeIfAbsent(code, BIC::new);
    }


    /**
     * Dieser Validator ist fuer die Ueberpruefung von BICs vorgesehen.
     *
     * @since 2.2
     */
    public static class Validator implements SimpleValidator<String> {

        /**
         * Hierueber kann man eine BIC validieren.
         *
         * @param bic die BIC (11- oder 14-stellig)
         * @return die validierte BIC (zur Weiterverarbeitung)
         */
        @Override
        public String validate(String bic) {
            String normalized = StringUtils.trim(bic);
            List<Integer> allowedLengths = Arrays.asList(8, 11, 14);
            if (!allowedLengths.contains(normalized.length())) {
                throw new InvalidLengthException(normalized, allowedLengths);
            }
            return normalized;
        }

    }
    
}

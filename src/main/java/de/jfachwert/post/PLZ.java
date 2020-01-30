/*
 * Copyright (c) 2017-2020 by Oliver Boehm
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

import de.jfachwert.SimpleValidator;
import de.jfachwert.Text;
import de.jfachwert.pruefung.LengthValidator;
import de.jfachwert.pruefung.NullValidator;
import de.jfachwert.pruefung.NumberValidator;
import de.jfachwert.pruefung.exception.InvalidValueException;
import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.WeakHashMap;

/**
 * Eine Postleitzahl (PLZ) kennzeichnet den Zustellort auf Briefen, Paketten
 * oder Paeckchen. In Deutschland ist es eine 5-stellige Zahl, wobei auch
 * "0" als fuehrende Ziffer erlaubt ist.
 *
 * @author oboehm
 * @since 0.2.0 (10.04.2017)
 */
public class PLZ extends Text {

    private static final SimpleValidator<String> VALIDATOR = new Validator();
    private static final WeakHashMap<String, PLZ> WEAK_CACHE = new WeakHashMap<>();

    /** Null-Wert fuer Initialisierung. */
    public static final PLZ NULL = new PLZ("00000", new NullValidator<>());

    /**
     * Hierueber wird eine Postleitzahl angelegt.
     *
     * @param plz z.B. "70839" oder "D-70839"
     */
    public PLZ(String plz) {
        super(plz, VALIDATOR);
    }

    /**
     * Hierueber wird eine Postleitzahl angelegt.
     *
     * @param plz z.B. "70839" oder "D-70839"
     * @param validator fuer die Ueberpruefung
     */
    public PLZ(String plz, SimpleValidator<String> validator) {
        super(plz, validator);
    }

    /**
     * Ueber diesen Konstruktor kann die Landeskennung als extra Parameter
     * angegeben werden.
     *
     * @param landeskennung z.B. "D"
     * @param plz z.B. "70839" (fuer Gerlingen)
     */
    public PLZ(String landeskennung, String plz) {
        this(landeskennung + plz);
    }

    /**
     * Ueber diesen Konstruktor kann die Landeskennung als extra Parameter
     * angegeben werden.
     *
     * @param land z.B. "de_DE"
     * @param plz z.B. "70839" (fuer Gerlingen)
     */
    public PLZ(Locale land, String plz) {
        this(toLandeskennung(land) + plz);
    }

    private static String toLandeskennung(Locale locale) {
        String country = locale.getCountry().toUpperCase();
        switch (country) {
            case "AT":
            case "DE":
                return country.substring(0, 1);
            default:
                return country;
        }
    }

    /**
     * Liefert eine PLZ zurueck.
     * 
     * @param plz PLZ als String
     * @return PLZ
     */
    public static PLZ of(String plz) {
        return WEAK_CACHE.computeIfAbsent(plz, PLZ::new);
    }

    /**
     * Hierueber kann man abfragen, ob der Postleitzahl eine Landeskennung
     * vorangestellt ist.
     *
     * @return true, falls PLZ eine Kennung besitzt
     */
    public boolean hasLandeskennung() {
        return hasLandeskennung(this.getCode());
    }

    private static boolean hasLandeskennung(String plz) {
        char kennung = plz.charAt(0);
        return Character.isLetter(kennung);
    }

    /**
     * Liefert die Landeskennung als String. Wenn keine Landeskennung
     * angegeben wurde, wird eine Exception geworfen.
     *
     * @return z.B. "D" fuer Deutschland
     */
    public String getLandeskennung() {
        if (!this.hasLandeskennung()) {
            throw new IllegalStateException("keine Landeskennung angegeben");
        }
        return getLandeskennung(this.getCode());
    }

    private static String getLandeskennung(String plz) {
        return StringUtils.substringBefore(toLongString(plz), "-");
    }

    /**
     * Liefert das Land, die der Landeskennung entspricht.
     *
     * @return z.B. "de_CH" (fuer die Schweiz)
     */
    public Locale getLand() {
        String kennung = this.getLandeskennung();
        switch (kennung) {
            case "D":   return new Locale("de", "DE");
            case "A":   return new Locale("de", "AT");
            case "CH":  return new Locale("de", "CH");
            default:    throw new UnsupportedOperationException("unbekannte Landeskennung '" + kennung + "'");
        }
    }

    /**
     * Liefert die eigentliche Postleitzahl ohne Landeskennung.
     *
     * @return z,B. "01001"
     */
    public String getPostleitZahl() {
        return getPostleitZahl(this.getCode());
    }

    private static String getPostleitZahl(String plz) {
        if (!hasLandeskennung(plz)) {
            return plz;
        }
        return StringUtils.substringAfter(toLongString(plz), "-");
    }

    /**
     * Liefert die PLZ in kompakter Schreibweise (ohne Trennzeichen zwischen
     * Landeskennung und eigentlicher PLZ) zurueck.
     *
     * @return z.B. "D70839"
     */
    public String toShortString() {
        return this.getCode();
    }

    /**
     * Liefert die PLZ in mit Trennzeichen zwischen Landeskennung (falls
     * vorhanden) und eigentlicher PLZ zurueck.
     *
     * @return z.B. "D-70839"
     */
    public String toLongString() {
        String plz = this.getCode();
        if (this.hasLandeskennung()) {
            plz = toLongString(plz);
        }
        return plz;
    }

    private static String toLongString(String plz) {
        int i = StringUtils.indexOfAny(plz, "0123456789");
        if (i < 0) {
            throw new InvalidValueException(plz, "postal_code");
        }
        return plz.substring(0, i) + "-" + plz.substring(i);
    }

    /**
     * Aus Lesbarkeitsgruenden wird zwischen Landeskennung und eigentlicher PLZ
     * ein Trennzeichen mit ausgegeben.
     *
     * @return z.B. "D-70839"
     */
    @Override
    public String toString() {
        return this.toLongString();
    }



    /**
     * In dieser Klasse sind die Validierungsregeln der diversen
     * PLZ-Validierungen zusammengefasst. Fuer Deutschland gilt z.B.:
     * <ul>
     * <li>Eine PLZ besteht aus 5 Ziffern.</li>
     * <li>Steht an erster Stelle eine 0, folgt darauf eine Zahl zwischen
     * 1 und 9.</li>
     * <li>Steht an erster Stelle eine Zahl zwischen 1 und 9, folgt darauf
     * eine Zahl zwischen 0 und 9.</li>
     * <li>An den letzten drei Stellen steht eine Zahl zwischen 0 und 9.</li>
     * </ul>
     */
    public static class Validator implements SimpleValidator<String> {

        /**
         * Eine Postleitahl muss zwischen 3 und 10 Ziffern lang sein. Eventuell
         * kann noch die Laenderkennung vorangestellt werden. Dies wird hier
         * ueberprueft.
         *
         * @param code die PLZ
         * @return die validierte PLZ (zur Weiterverarbeitung)
         */
        @Override
        public String validate(String code) {
            String plz = normalize(code);
            if (hasLandeskennung(plz)) {
                validateNumberOf(plz);
            } else {
                plz = LengthValidator.validate(plz, 3, 10);
                if (plz.length() == 5) {
                    validateNumberFuenfstellig(plz);
                }
            }
            return plz;
        }

        private static void validateNumberOf(String plz) {
            String kennung = getLandeskennung(plz);
            String zahl = getPostleitZahl(plz);
            switch (kennung) {
                case "D":
                    validateNumberFuenfstellig(zahl);
                case "CH":
                    validateNumberWith(plz, 6, zahl);
                    break;
                case "A":
                    validateNumberWith(plz, 5, zahl);
                    break;
                default:
                    LengthValidator.validate(zahl, 3, 10);
                    break;
            }
        }

        private static void validateNumberWith(String plz, int length, String zahl) {
            LengthValidator.validate(plz, length);
            new NumberValidator(BigDecimal.ZERO, BigDecimal.TEN.pow(length)).validate(zahl);
        }

        private static void validateNumberFuenfstellig(String plz) {
            int n = Integer.parseInt(plz);
            if (n < 1000) {
                throw new InvalidValueException(plz, "postal_code", Range.between("01000", "99999"));
            }
        }

        private static String normalize(String plz) {
            return StringUtils.replaceChars(plz, " -", "").toUpperCase();
        }

    }

}

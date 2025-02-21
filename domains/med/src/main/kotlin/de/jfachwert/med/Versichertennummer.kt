/*
 * Copyright (c) 2023-2024 by Oli B.
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
 * (c)reated 22.12.23 by oboehm
 */
package de.jfachwert.med

import de.jfachwert.AbstractFachwert
import de.jfachwert.KSimpleValidator
import de.jfachwert.PruefzifferVerfahren
import de.jfachwert.pruefung.LengthValidator
import de.jfachwert.pruefung.LuhnVerfahren
import de.jfachwert.pruefung.NullValidator
import de.jfachwert.pruefung.exception.InvalidValueException
import java.util.*

/**
 * Seit der Einfuehrung der elektronischen Gesundheitskarte im Jahr 2012 werden
 * die ersten zehn Stellen der Krankenversichertennummer ebenso wie die
 * Rentenversicherungsnummer einmalig vergeben und bleiben lebenslang gleich
 * (aus https://de.wikipedia.org/wiki/Krankenversichertennummer).
 *
 * Die Versichertennummer ist dabei die 10-stellige Nummer am Anfang der
 * Krankenverichertennummer.
 *
 * @author oboehm
 * @since 5.1 (22.12.23)
 */
open class Versichertennummer
/**
 * Dieser Konstruktor ist hauptsaechlich fuer abgeleitete Klassen gedacht,
 * damit diese das [PruefzifferVerfahren] ueberschreiben koennen.
 * Man kann es auch verwenden, um das PruefzifferVerfahren abzuschalten,
 * indem man das [de.jfachwert.pruefung.NoopVerfahren] verwendet.
 *
 * @param code        die Versichertennummer
 * @param pzVerfahren das verwendete PruefzifferVerfahren (optional)
 */
@JvmOverloads constructor(code: String, pzVerfahren: KSimpleValidator<String> = VALIDATOR) : AbstractFachwert<String, Versichertennummer>(code, pzVerfahren) {

    /**
     * Diese Methode liefert immer 'true' zurueck. Es sei denn, nan hat den
     * Default-Validator beim Anlegen deaktiviert.
     *
     * @return true oder false
     */
    override fun isValid(): Boolean {
        return VALIDATOR.isValid(code)
    }



    /**
     * Dieser Validator ist fuer die Ueberpruefung einer Versichertennummer
     * vorgesehen.
     *
     * @since 5.1
     */
    class Validator : KSimpleValidator<String> {
        /**
         * Die erste Stelle der Krankenversichertennummer ist ein zufaellig
         * vergebener Grossbuchstabe (kein Umlaut!), es folgen acht zufaellige
         * Zahlen, die zehnte Stelle ist eine Pruefziffer, die mit dem im
         * Folgenden beschriebenen Modulo-10-Verfahren mit den Gewichten
         * 1-2-1-2-1-2-1-2-1-2 berechnet wird:
         * Der Buchstabe wird durch eine zweistellige Zahl entsprechend
         * seiner Stelle im Alphabet ersetzt (A = 01, B = 02, …, Z = 26).
         * Die – zusammen mit den acht Zufallszahlen – resultierenden zehn
         * Ziffern werden nun von links nach rechts abwechselnd mit 1 und 2
         * multipliziert. Danach erfolgt eine Quersummenbildung der einzelnen
         * Produkte mit anschliessender Summenbildung ueber die zehn Quersummen
         *
         * @param value die 22-stellige IBAN
         * @return die IBAN in normalisierter Form (ohne Leerzeichen)
         */
        override fun validate(value: String): String {
            val normalized = value.trim()
            LengthValidator.validate(normalized, 10)
            val regex = Regex("[A-Z]\\d{9}")
            if (!regex.matches(normalized)) {
                throw InvalidValueException(normalized, "policy_number", regex)
            }
            LuhnVerfahren().validate(normalized)
            return normalized
        }
    }

    companion object {
        private val WEAK_CACHE = WeakHashMap<String, Versichertennummer>()
        private val VALIDATOR: KSimpleValidator<String> = Validator()
        /** Null-Konstante.  */
        @JvmField
        val NULL = Versichertennummer("", NullValidator())

        /**
         * Liefert eine Versichertennummer.
         *
         * @param code gueltige Versichertennummer
         * @return IBAN
         */
        @JvmStatic
        fun of(code: String): Versichertennummer {
            return WEAK_CACHE.computeIfAbsent(code) { c: String -> Versichertennummer(c) }
        }
    }

}
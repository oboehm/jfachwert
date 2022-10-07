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
 * (c)reated 23.06.2017 by oboehm (ob@oasd.de)
 */
package de.jfachwert.net

import de.jfachwert.KSimpleValidator
import de.jfachwert.Text
import de.jfachwert.post.Name
import de.jfachwert.pruefung.NullValidator
import de.jfachwert.pruefung.exception.InvalidValueException
import org.apache.commons.lang3.StringUtils
import java.util.*
import java.util.regex.Pattern

/**
 * Eine E-Mail-Adresse ist die eindeutige Absender- und Empfaengeradresse im
 * E-Mail-Verkehr. Sie besteht aus zwei Teilen, die durch ein @-Zeichen
 * voneinander getrennt sind:
 *  * Der lokale Teil, im Englischen local-part genannt,
 *    steht vor dem @-Zeichen.
 *  * Der globale Teil, im Englischen domain-part genannt,
 *    steht nach dem @-Zeichen.
 *
 * Bei der E-Mail-Adresse "email@example.com" ist "email" der lokale Teil
 * und "example.com" der globale Teil.
 *
 * @author oboehm
 * @since 0.3 (23.06.2017)
 */
open class EMailAdresse
/**
 * Legt eine Instanz einer EMailAdresse an. Der Validator ist
 * hauptsaechlich fuer abgeleitete Klassen gedacht, die ihre eigene
 * Validierung mit einbringen wollen oder aus Performance-Gruenden
 * abschalten wollen.
 *
 * @param emailAdresse eine gueltige Adresse, z.B. "max@mustermann.de"
 * @param validator    SimpleValidator zur Adressen-Validierung
 */
@JvmOverloads constructor(emailAdresse: String, validator: KSimpleValidator<String> = VALIDATOR) : Text(validator.verify(emailAdresse)) {

    /**
     * Als Local Part wird der Teil einer E-Mail-Adresse bezeichnet, der die
     * Adresse innerhalb der Domain des E-Mail-Providers eindeutig bezeichnet.
     * Typischerweise entspricht der Lokalteil dem Benutzernamen (haeufig ein
     * Pseudonym) des Besitzers des E-Mail-Kontos.
     *
     * @return z.B. "Max.Mustermann"
     */
    val localPart: String
        get() = StringUtils.substringBeforeLast(code, "@")

    /**
     * Der Domain Part, der hinter dem @-Zeichen steht und fuer den die
     * Syntaxregeln des Domain Name Systems gelten, besteht mindestens aus drei
     * Teilen: einem Hostnamen (z. B. ein Firmenname), einem Punkt und einer
     * Top-Level-Domain.
     *
     * @return z.B. "fachwert.de"
     */
    val domainPart: Domainname
        get() = Domainname(StringUtils.substringAfterLast(code, "@"))

    /**
     * Liefert den Namensanteil der Email-Adresse als [Name] zurueck.
     * Kann dann eingesetzt werden, wenn die Email-Adresse nach dem Schema
     * "vorname.nachname@firma.de" aufgebaut ist.
     *
     * @return z.B. "O. Boehm" als Name
     * @since 2.3
     */
    val name: Name
        get() {
            val name = capitalize(localPart, '.')
            return Name.of(name)
        }

    private fun capitalize(word: String, delimiter: Char): String {
        val parts = word.split(delimiter)
        var capitalized = ""
        for (s in parts) {
            capitalized += capitalize(s) + ' '
        }
        return capitalized.trim()
    }

    private fun capitalize(word: String): String {
        return word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }



    /**
     * Die Klasse EMailValidator validiert vornehmlich E-Mail-Adressen.
     * Urspruenglich war er eine separate Klasse, mit v2.2 wurde er anolog
     * zu den anderen Validatoren zur entsprechenden Klasse als innere Klasse
     * verschoben.
     *
     * @author oboehm
     * @since 0.3 (27.06.2017)
     */
    class Validator

        /**
         * Dieser Konstruktor ist fuer abgeleitete Klassen gedacht, die das Pattern
         * fuer die Adress-Validierung ueberschreiben moechten.
         *
         * @param addressPattern Pattern fuer die Adress-Validerung
         */
        protected constructor(private val addressPattern: Pattern) : KSimpleValidator<String> {

        /**
         * Hier wird der E-Mail-SimpleValidator mit einerm Pattern von
         * https://www.mkyong.com/regular-expressions/how-to-validate-email-address-with-regular-expression/
         * aufgesetzt.
         */
        constructor() : this(Pattern
                .compile("^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")) {
        }

        /**
         * Fuehrt ein Pattern-basierte Pruefung der uebegebenen E-Mail-Adresse
         * durch. Schlaegt die Pruefung fehl, wird eine
         * [javax.validation.ValidationException] geworfen.
         *
         * @param value zu pruefende E-Mail-Adresse
         * @return die validierte E-Mail-Adresse (zur Weiterverarbeitung)
         */
        override fun validate(value: String): String {
            val matcher = addressPattern.matcher(value)
            if (matcher.matches()) {
                return value
            }
            throw InvalidValueException(value, "email_address")
        }

    }

    companion object {
        private val VALIDATOR: KSimpleValidator<String> = Validator()
        private val WEAK_CACHE = WeakHashMap<String, EMailAdresse>()
        /** Null-Konstante fuer Initialisierungen.  */
        @JvmField
        val NULL = EMailAdresse("", NullValidator())

        /**
         * Liefert einen EmailAdresse.
         *
         * @param name gueltige Email-Adresse
         * @return EMailAdresse
         */
        @JvmStatic
        fun of(name: String): EMailAdresse {
            return WEAK_CACHE.computeIfAbsent(name) { emailAdresse: String -> EMailAdresse(emailAdresse) }
        }
    }

}
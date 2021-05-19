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
 * (c)reated 04.09.2017 by oboehm (ob@oasd.de)
 */
package de.jfachwert.net

import de.jfachwert.KSimpleValidator
import de.jfachwert.Text
import de.jfachwert.pruefung.LengthValidator
import de.jfachwert.pruefung.NullValidator
import de.jfachwert.pruefung.exception.InvalidValueException
import org.apache.commons.lang3.RegExUtils
import org.apache.commons.lang3.StringUtils
import java.net.URI
import java.util.*
import java.util.regex.Pattern

/**
 * Die Klasse Telefonnummer steht fuer alle Arten von Rufnummern im
 * Telefon-Netz wie Fesetnetznummer, Faxnummer oder Mobilfunknummer.
 * Ueblicherweise bestehen Telefonnummern aus
 *
 *  * Laenderkennzahl (LKz),
 *  * Ortsnetzkennzahl (ONKz) bzw. die eigentliche Vorwahl
 *  * Teilnehmerrufnummer (RufNr),
 *  * Durchwahl (optional).
 *
 * Die Telefonnummer +49 30 12345-67 hat die Laenderkennzahl 49 (Deutschland),
 * die Vorwahl 030 (Berlin), die Teilnehmerrufnummer 12345 und die Durchwahl
 * 67.
 *
 * Frueher waren Telefon-Netz und Computer-Netzwerke strikt getrennt.
 * Inwischen wachsen diese beiden Netze immer mehr zusammen und unterscheiden
 * sich nur noch durch das Netzwerkprotokoll. Deswegen ist diese Klasse
 * im gleichen Package 'net' wie die EMailAdresse zu finden und nicht in
 * 'comm' (fuer Kommunikation), wie urspruenglich geplant.
 *
 * TODO: Fuer die Speicherung kann auf PackedDecimal umstellen (statt String).
 * Damit laesst sich der Speicherbedarf fuer Telefonnummern halbieren.
 *
 * @author oboehm
 * @since 0.5 (04.09.2017)
 */
open class Telefonnummer
/**
 * Legt eine Instanz einer Telefonnummer an. Dieser Konstruktor ist
 * hauptsaechlich fuer abgeleitete Klassen gedacht, die ihre eigene
 * Validierung mit einbringen wollen oder aus Performance-Gruenden
 * abschalten wollen.
 *
 * @param nummer    eine gueltige Telefonnummer, z.B. "+49 30 12345-67"
 * @param validator SimpleValidator zur Adressen-Validierung
 */
@JvmOverloads constructor(nummer: String, validator: KSimpleValidator<String> = VALIDATOR) : Text(normalize(nummer), validator) {

    /**
     * Eine Telefonnummer lasesst sich auch ueber eine URI kreieren. Der
     * RFC 3966 schlaegt dabei "tel:" als Schema vor.
     *
     * @param uri z.B. "tel:+49-30-1234567"
     */
    constructor(uri: URI) : this(uri.schemeSpecificPart) {}

    /**
     * Liefert die Telefonnummer ohne Laenderkennzahl, dafuer mit Vorwahl
     * inklusive fuehrender Null.
     *
     * @return z.B. 0811/32168
     */
    val inlandsnummer: Telefonnummer
        get() = if (laenderkennzahl.isPresent) {
            var nummer = code.substring(3).trim { it <= ' ' }
            if (StringUtils.startsWithAny(nummer, "1", "2", "3", "4", "5", "6", "7", "8", "9")) {
                nummer = "0$nummer"
            }
            Telefonnummer(nummer)
        } else {
            this
        }

    /**
     * Die Laenderkennzahl (LKZ) ist die Vorwahl, die man fuer Telefonate ins
     * Ausland waehlen muss. Fuer Deutschland ist die LKZ "+49*, d.h. wenn
     * man von Oesterreich nach Deutschland waehlen muss, muss man "0049"
     * vorwaehlen.
     *
     * Da die Laenderkennzahl optional ist, wird sie als [Optional]
     * zurueckgegeben.
     *
     * @return z.B. "+49"
     */
    val laenderkennzahl: Optional<String>
        get() {
            val laenderkennzahl = code.substring(0, 3)
            return if (laenderkennzahl.startsWith("+")) {
                Optional.of(laenderkennzahl)
            } else {
                Optional.empty()
            }
        }

    /**
     * Liefert die Vorwahl oder auch Ortskennzahl (ONKz).
     *
     * @return z.B. "0711" fuer Stuttgart
     */
    val vorwahl: String
        get() {
            val parts = code.trim { it <= ' ' }.split("[ /-]|(\\(0\\))".toRegex()).toTypedArray()
            var vorwahl = parts[0]
            if (vorwahl.startsWith("+")) {
                vorwahl = if (StringUtils.isBlank(parts[1])) parts[2] else parts[1]
            }
            vorwahl = RegExUtils.removeAll(vorwahl, "[ \t+-/(\\(\\))]")
            return if (vorwahl.startsWith("0")) {
                vorwahl
            } else "0$vorwahl"
        }

    /**
     * Liefert die Nummer der Ortsvermittlungsstelle, d.h. die Telefonnummer
     * ohne Vorwahl und Laenderkennzahl.
     *
     * @return z.B. "32168"
     */
    val rufnummer: Telefonnummer
        get() {
            val inlandsnummer = RegExUtils.replaceAll(inlandsnummer.toString(), "[ /]+", " ")
            return Telefonnummer(StringUtils.substringAfter(inlandsnummer, " ").replace(" ".toRegex(), ""))
        }

    /**
     * Wenn zwei Telefonnummern gleich sind, muessen sie auch den gleichen
     * Hashcode liefern.
     *
     * @return Hashcode, der nur aus den Ziffern ermittelt wird
     */
    override fun hashCode(): Int {
        return toShortString().hashCode()
    }

    /**
     * Beim Vergleich zweier Telefonnummern spielen Trennzeichen keine Rolle.
     * Hier sind nur die Nummern relevant.
     *
     * @param other zu vergleichende Telefonnummer
     * @return true bei Gleichheit
     */
    override fun equals(other: Any?): Boolean {
        if (other !is Telefonnummer) {
            return false
        }
        return toShortString() == other.toShortString()
    }

    /**
     * Stellt eine Telefonnummer in verkuerzter Schreibweise ohne Leerzeichen
     * und Trennzeichen dar.
     *
     * @return z.B. "+49301234567"
     */
    fun toShortString(): String {
        return RegExUtils.removeAll(code, "[ \t+-/]|(\\(0\\))")
    }

    /**
     * Gibt den String nach DIN 5008 aus. Die Nummern werden dabei
     * funktionsbezogen durch Leerzeichen und die Durchwahl mit Bindestrich
     * abgetrennt.
     *
     * @return z.B. "+49 30 12345-67" bzw. "030 12345-67"
     */
    fun toDinString(): String {
        val laenderkennzahl = laenderkennzahl
        return laenderkennzahl.map { s: String -> s + " " + vorwahl.substring(1) + " " + rufnummer }.orElseGet { "$vorwahl $rufnummer" }
    }

    /**
     * Die "E.123" ist eine Empfehlung der Internationalen Fernmeldeunion.
     * Dabei werden die einzelnen Bestandteile (Laenderkennzeichen, Vorwahl
     * und Rufnummer) durch Leerzeichen gruppiert.
     *
     * @return z.B. "+49 30 12345 67" oder "(030) 12345 67" (national)
     */
    fun toE123String(): String {
        return if (laenderkennzahl.isPresent) {
            toDinString().replace('-', ' ')
        } else {
            "(" + vorwahl + ") " + rufnummer.toString().replace('-', ' ')
        }
    }

    /**
     * Nach RFC 3966 wird die Telefonnummer wie E.123 dargestellt, jedoch mit
     * Bindestrich statt Leerzeichen.
     *
     * @return z.B. "tel:+49-30-12345-67"
     */
    fun toURI(): URI {
        return URI.create("tel:" + toDinString().replace(' ', '-'))
    }



    /**
     * Die Klasse Validator validiert die Schreibweise von Telefonnummern.
     * Urspruenglich war dieser Validator in einer eigenen Klasse
     * ('TelefonnummerValidator' ausgegegliedert. Mit v2.2 wurde der
     * Validator analog zu den anderen Validatoren zur betroffenen
     * Klasse als innere Klasse dazugesteckt.
     *
     * @author oboehm
     * @since 0.5 (05.09.2017)
     */
    class Validator constructor(private val pattern: Pattern) : KSimpleValidator<String> {

        private val lengthValidator = LengthValidator<String>(3, 15)

        /**
         * Hier wird der Telefon-SimpleValidator mit einerm Pattern von
         * https://www.mkyong.com/regular-expressions/how-to-validate-email-address-with-regular-expression/
         * aufgesetzt.
         */
        constructor() : this(Pattern.compile("[0-9-+/ ()]+")) {}

        /**
         * Ueberprueft die Telefonnummer, ob sie nur erlaubte Nummern (und
         * Sonderzeichen) enthaelt.
         *
         * @param nummer zu pruefende Telefonnummer
         * @return Wert selber, wenn er gueltig ist
         */
        override fun validate(nummer: String): String {
            val matcher = pattern.matcher(nummer)
            if (matcher.matches()) {
                val normalized = RegExUtils.removeAll(nummer, "[ \t+-/]|(\\(0\\))")
                lengthValidator.validate(normalized)
                return nummer
            }
            throw InvalidValueException(nummer, "phone_number")
        }

    }



    companion object {
        private val VALIDATOR: KSimpleValidator<String> = Validator()
        private val WEAK_CACHE = WeakHashMap<String, Telefonnummer>()
        /** Null-Konstante fuer Initialisierungen.  */
        val NULL = Telefonnummer("", NullValidator())

        /**
         * Liefert eine Telefonnummer zurueck.
         *
         * @param nummer eine gueltige Telefonnummer, z.B. "+49 30 12345-67"
         * @return Telefonnummer
         */
        @JvmStatic
        fun of(nummer: String): Telefonnummer {
            return WEAK_CACHE.computeIfAbsent(nummer) { n: String -> Telefonnummer(n) }
        }

        private fun normalize(nummer: String): String {
            var normalized = nummer.trim { it <= ' ' }
            if (normalized.startsWith("(0)")) {
                normalized = "0" + normalized.substring(3)
            }
            return normalized
        }
    }

}
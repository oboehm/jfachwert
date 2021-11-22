/*
 * Copyright (c) 2018-2020 by Oliver Boehm
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
 * (c)reated 17.01.2018 by oboehm (ob@oasd.de)
 */
package de.jfachwert

import de.jfachwert.pruefung.NullValidator
import org.apache.commons.lang3.StringUtils
import java.nio.CharBuffer
import java.util.*

/**
 * Die Klasse Text ist der einfachste Fachwerte, der eigentlich nur ein
 * Wrapper um die String-Klasse ist. Allerdings mit dem Unterschied, dass
 * man keinen Null-Text oder leeren Text anlegen kann.
 *
 * Diese Klasse wurde mit der [FachwertFactory] eingefuehrt. Sie dient
 * dort als Fallback, wenn kein Fachwert erzeugt werden kann, auf den der
 * uebergebene Name passt.
 *
 * @author oboehm
 * @since 0.5 (17.01.2018)
 */
open class Text
/**
 * Erzeugt einen Text, der mit dem uebergebenen Validator vor ueberprueft
 * wird.
 *
 * @param text      Text
 * @param validator Validator fuer die Ueberpruefung
 */
@JvmOverloads constructor(text: String, validator: KSimpleValidator<String> = VALIDATOR) : AbstractFachwert<String, Text>(VALIDATOR.verify(text).intern(), validator), Comparable<Text> {
    /**
     * Berechnet die Levenshtein-Distanz.
     *
     * @param other anderer Text
     * @return Levenshtein-Distanz
     * @since 2.0
     */
    fun getDistanz(other: Text): Int {
        return getDistanz(other.code)
    }

    /**
     * Berechnet die Levenshtein-Distanz. Der Algorithmus dazu stammt aus
     * http://rosettacode.org/wiki/Levenshtein_distance#Java.
     *
     * @param other anderer Text
     * @return Levenshtein-Distanz
     * @since 2.0
     */
    fun getDistanz(other: String): Int {
        return distance(code, other)
    }

    /**
     * Ersetzt Umlaute und scharfes 'S'.
     *
     * @return Text ohne Umlaut und scharfem 's'
     * @since 2.1
     */
    fun replaceUmlaute(): Text {
        return of(replaceUmlaute(code))
    }

    /**
     * Dient zur Abfrage, ob ein Text nur gueltige (druckbare) Zeichen
     * enthaelt. Ist dies nicht der Fall, koennte ein Encoding-Problem
     * vorliegen.
     *
     * @return true oder false
     * @since 4.1
     */
    fun isPrintable(): Boolean {
        if (StringUtils.isAsciiPrintable(replaceUmlaute(code))) {
            return true
        }
        val printable = {}.javaClass.getResource("printable.txt").readText()
        for (c in code) {
            if (!(c.isLetterOrDigit() || c.isWhitespace() || printable.contains(c))) {
                return false
            }
        }
        return true
    }

    /**
     * Liefert einen Text mit Kleinbuchstaben.
     *
     * @return Text mit Kleinbuchstaben
     * @since 2.1
     */
    fun toLowerCase(): Text {
        return of(code.lowercase())
    }

    /**
     * Liefert einen Text mit Grossbuchstaben.
     *
     * @return Text mit Grossbuchstaben
     * @since 2.1
     */
    fun toUpperCase(): Text {
        return of(code.uppercase())
    }

    /**
     * Ignoriert beim Vergleich Gross- und Kleinschreibung.
     *
     * @param other der anderer Text
     * @return true bei Gleichheit
     * @since 2.1
     */
    fun equalsIgnoreCase(other: Text): Boolean {
        return code.equals(other.code, ignoreCase = true)
    }

    /**
     * Ignoriert beim Vergleich die Umlaute.
     *
     * @param other der anderer Text
     * @return true bei Gleichheit
     * @since 2.2
     */
    fun equalsIgnoreUmlaute(other: Text): Boolean {
        return this.replaceUmlaute() == other.replaceUmlaute()
    }

    /**
     * Ignoriert beim Vergleich die Umlaute sowie Gross- und Kleinschreibung.
     *
     * @param other der anderer Text
     * @return true bei Gleichheit
     * @since 2.2
     */
    fun equalsIgnoreCaseAndUmlaute(other: Text): Boolean {
        return this.replaceUmlaute().equalsIgnoreCase(other.replaceUmlaute())
    }

    /**
     * Dient zum (alphabetischen) Vergleich zweier Texte.
     *
     * @param other der andere Text
     * @return negtive Zahl, falls this &lt; other, 0 bei Gleichheit, ansonsten
     * positive Zahl.
     * @since 3.0
     */
    override fun compareTo(other: Text): Int {
        return code.compareTo(other.code)
    }



    companion object {

        private val VALIDATOR: KSimpleValidator<String> = NullValidator()
        private val WEAK_CACHE = WeakHashMap<String, Text>()

        /** Null-Konstante fuer Initialisierungen .  */
        @JvmField
        val NULL = Text("")

        /**
         * Liefert einen Text zurueck.
         *
         * @param text darf nicht null sein
         * @return Text
         */
        @JvmStatic
        fun of(text: String): Text {
            return WEAK_CACHE.computeIfAbsent(text) { s: String -> Text(s) }
        }

        /**
         * Ueberprueft den uebergebenen Text.
         *
         * @param text Text
         * @return den validierten Text zur Weiterverabeitung
         */
        fun validate(text: String): String {
            return VALIDATOR.validate(text)
        }

        private fun distance(s1: String, s2: String): Int {
            val a = s1.lowercase()
            val b = s2.lowercase()
            // i == 0
            val costs = IntArray(b.length + 1)
            for (j in costs.indices) {
                costs[j] = j
            }
            for (i in 1..a.length) { // j == 0; nw = lev(i - 1, j)
                costs[0] = i
                var nw = i - 1
                for (j in 1..b.length) {
                    val cj = Math.min(1 + Math.min(costs[j], costs[j - 1]),
                            if (a[i - 1] == b[j - 1]) nw else nw + 1)
                    nw = costs[j]
                    costs[j] = cj
                }
            }
            return costs[b.length]
        }

        /**
         * Ersetzt Umlaute und scharfes 'S'. Diese Methode wurde als statische
         * Methode herausgezogen, da sie an anderen Stellen benoetigt werden.
         *
         * Mit v2.2.2 wurde die Methode optimiert, da der alte Ansatz mit
         * [String.replace] sich als Flaschenhals
         * beim Vergleich grosser Datenmenge herausstellte. Durch die Umstellung
         * auf zeichenweises Mapping ist diese Methode jetzt ca. 4 x schneller.
         *
         * @param text Text (mit Umlaute)
         * @return Text ohne Umlaut und scharfem 's'
         * @since 2.1
         */
        @JvmStatic
        fun replaceUmlaute(text: String): String {
            val zeichen = text.toCharArray()
            val buffer = CharBuffer.allocate(zeichen.size * 2)
            for (c in zeichen) {
                when (c) {
                    '\u00e4' -> buffer.put("ae")
                    '\u00f6' -> buffer.put("oe")
                    '\u00fc' -> buffer.put("ue")
                    '\u00df' -> buffer.put("ss")
                    '\u00c4' -> buffer.put("Ae")
                    '\u00d6' -> buffer.put("Oe")
                    '\u00dc' -> buffer.put("Ue")
                    '\u00e1', '\u00e0', '\u00e2' -> buffer.put('a')
                    '\u00e9', '\u00e8', '\u00ea', '\u00eb' -> buffer.put('e')
                    '\u00f3', '\u00f2', '\u00f4' -> buffer.put('o')
                    '\u00fa', '\u00f9', '\u00fb' -> buffer.put('u')
                    '\u00c1', '\u00c0', '\u00c2' -> buffer.put('A')
                    '\u00c9', '\u00c8', '\u00ca' -> buffer.put('E')
                    '\u00d3', '\u00d2', '\u00d4' -> buffer.put('O')
                    '\u00da', '\u00d9', '\u00db' -> buffer.put('U')
                    else -> buffer.put(c)
                }
            }
            buffer.rewind()
            return buffer.toString().trim { it <= ' ' }
        }

    }

}
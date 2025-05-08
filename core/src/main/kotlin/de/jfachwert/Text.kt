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
import java.nio.Buffer
import java.nio.CharBuffer
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.logging.Logger

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
     * Diese trim-Methode beruecksichtigt auch geschuetzte Leerzeichen
     * (non-braking spaces).
     *
     * @return Text ohne Leerzeichen am Anfang und Ende
     * @since 6.2
     */
    fun trim(): Text {
        return of(trim(code))
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
        return Companion.isPrintable(code)
    }

    /**
     * Wandelt einen Text in druckbare Zeichen, indem nicht druckbare Zeichen
     * ausgefiltert werden.
     *
     * @return Text nur mit druckbaren Zeichen
     * @since 4.6
     */
    fun toPrintable(): Text {
        val buf = StringBuilder()
        for (c in code) {
            if (of(c).isPrintable()) {
                buf.append(c)
            }
        }
        return of(buf.toString())
    }

    /**
     * Erkennt das Encoding eines Textes. Die Idee dahinter ist, dass wir
     * einen Text nach UTF-8 und wieder zurueck konvertieren. Dies ist ein
     * einfacher Ansatz und stammt aus <a
     * href="https://www.turro.org/publications?item=114&page=0">Detect the
     * charset in Java strings</a>, reicht aber fuer einfache Faelle aus.
     *
     * Wer es genauer will, kann z.B. auf
     * <a href="https://tika.apache.org/">Tika</a>
     * zurueckgreifen.
     *
     * @return Encoding
     * @since 4.2
     */
    fun detectCharset(): Charset {
        return Companion.detectCharset(code)
    }

    /**
     * Erkennt das Encoding eines Textes. Im Unterschied zu Text#detectCharset
     * werden hier alle erkannte Charsets zurueckgegeben
     *
     * @return Liste mit Encodings, die in Frage kommen
     * @since 4.2
     */
    fun detectCharsets(): Collection<Charset> {
        return Companion.detectCharsets(code)
    }

    /**
     * Testet, ob das Encoding fuer den uergebenen Text stimmen kann.
     * Die Idee dahinter ist, dass wir einen Text nach UTF-8 und wieder
     * zurueck konvertieren. Falls es klappt, wird 'true' zurueckgegeben.
     *
     * @param cs vermutetes Encoding
     * @return true, wenn das Encoding stimmen koennte
     * @since 4.2
     */
    fun isCharset(cs: Charset) : Boolean {
        return Companion.isCharset(code, cs)
    }

    /**
     * Konvertiert mit JDK-Bordmittel einen Text in ein gewuenschtes
     * Encoding. Allerdings kann je nach Konvertierung das Ergebnis
     * verlustbehaftet sein.
     *
     * @param toEncoding gewuenschtes Encoding
     * @param fromEncoding aktuelles Encoding des Textes
     * @since 4.2
     */
    fun convertTo(toEncoding: Charset, fromEncoding: Charset = detectCharset()) : Text {
        return of(convert(code, toEncoding, fromEncoding))
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

        private val LOG = Logger.getLogger(Text::class.java.name)
        private val VALIDATOR: KSimpleValidator<String> = NullValidator()
        private val WEAK_CACHE = WeakHashMap<String, Text>()
        private val PRINTABLE = {}.javaClass.getResource("printable.txt").readText()

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
         * Liefert ein einzelnes Zeichen als zurueck.
         *
         * @param c einzelnes Zeichen
         * @return Text
         * @since 4.6
         */
        @JvmStatic
        fun of(c: Char): Text {
            return of(c.toString())
        }

        /**
         * Liefert einen Text fuer den gewuenschten Zeichensatz (Encoding)
         * zurueck. Dabei werden Zeichen, die nicht in diesem Zeichensatz
         * vorhanden sind, durch eine Ersatzdarstellung ersetzt.
         *
         * @param text darf nicht null sein
         * @param encoding z.B. ISO-8859-1
         * @return Text
         * @since 4.3
         */
        @JvmStatic
        fun of(text: String, encoding: Charset): Text {
            val encoded = replaceSpecialChars(text, encoding)
            return of(encoded)
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
         * Im Gegensatz zur normalen trim-Methode werden hier auch geschuetzte
         * Leerzeichen (non-braking spaces) beruecksichtigt.
         *
         * @param text Text mit Leerzeichen am Anfaung und Ende
         * @return Text ohne Leerzeichen
         * @since 6.2
         */
        @JvmStatic
        fun trim(text: String): String {
            return text.trim(' ', '\t', '\r', '\n', '\u00A0')
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
                buffer.put(replaceUmlaut(c))
            }
            rewind(buffer)
            return buffer.toString().trim { it <= ' ' }
        }

        private fun replaceUmlaut(c: Char) : String {
            when (c) {
                '\u00e4' -> return "ae"
                '\u00f6' -> return "oe"
                '\u00fc' -> return "ue"
                '\u00df' -> return "ss"
                '\u00c4' -> return "Ae"
                '\u00d6' -> return "Oe"
                '\u00dc' -> return "Ue"
                '\u00e1', '\u00e0', '\u00e2' -> return "a"
                '\u00e9', '\u00e8', '\u00ea', '\u00eb' -> return "e"
                '\u00f3', '\u00f2', '\u00f4' -> return "o"
                '\u00fa', '\u00f9', '\u00fb' -> return "u"
                '\u00c1', '\u00c0', '\u00c2' -> return "A"
                '\u00c9', '\u00c8', '\u00ca' -> return "E"
                '\u00d3', '\u00d2', '\u00d4' -> return "O"
                '\u00da', '\u00d9', '\u00db' -> return "U"
                '\u00a1' -> return "!"
                '\u00a3' -> return "GBP"
                '\u00a5' -> return "JPY"
                '\u00bf' -> return "?"
                else -> return replaceSpecialChar(c, StandardCharsets.ISO_8859_1)
            }
        }

        private fun rewind(buffer: CharBuffer?) {
            // buffer.rewind() bereitet unter Java 8 Problem wegen inkompatibler API-Änderung zwischen Java 8 und 9
            // https://stackoverflow.com/questions/61267495/exception-in-thread-main-java-lang-nosuchmethoderror-java-nio-bytebuffer-flip
            // Daher jetzt der Workaround ueber Cast auf Oberklasse
            val b: Buffer = buffer as Buffer
            b.rewind()
        }

        /**
         * Dient zur Abfrage, ob ein Text nur gueltige (druckbare) Zeichen
         * enthaelt. Ist dies nicht der Fall, koennte ein Encoding-Problem
         * vorliegen.
         *
         * @param text Text
         * @return true oder false
         * @since 4.1
         */
        @JvmStatic
        fun isPrintable(text: String): Boolean {
            for (c in text) {
                if (!(c.isLetterOrDigit() || c.isWhitespace() || PRINTABLE.contains(c))) {
                    return false
                }
            }
            return true
        }

        /**
         * Filtert nicht druckbare Zeichen aus dem uebergebenen String aus.
         *
         * @param text Text
         * @return String nur mit druckbaren Zeichen
         * @since 4.6
         */
        @JvmStatic
        fun toPrintable(text: String): String {
            return Text(text).toPrintable().toString()
        }

        /**
         * Erkennt das Encoding eines Textes. Die Idee dahinter ist, dass wir
         * einen Text nach UTF-8 und wieder zurueck konvertieren. Dies ist ein
         * einfacher Ansatz und stammt aus <a
         * href="https://www.turro.org/publications?item=114&page=0">Detect the
         * charset in Java strings</a>, reicht aber fuer einfache Faelle aus.
         *
         * Wer es genauer will, kann z.B. auf
         * <a href="https://tika.apache.org/">Tika</a>
         * zurueckgreifen.
         *
         * @param value Text mit unbekanntem Encoding
         * @return Encoding
         * @since 4.2
         */
        @JvmStatic
        fun detectCharset(value: String): Charset {
            val charsets = mutableListOf<Charset>(StandardCharsets.ISO_8859_1, StandardCharsets.UTF_8)
            charsets.addAll(Charset.availableCharsets().values)
            for (cs in charsets) {
                if (isCharset(value, cs)) {
                    return cs
                }
            }
            return StandardCharsets.ISO_8859_1
        }

        /**
         * Erkennt das Encoding eines Textes. Die Idee dahinter ist, dass wir
         * einen Text nach UTF-8 und wieder zurueck konvertieren. Dies ist ein
         * einfacher Ansatz und stammt aus <a
         * href="https://www.turro.org/publications?item=114&page=0">Detect the
         * charset in Java strings</a>, reicht aber fuer einfache Faelle aus.
         *
         * Im Gegensatz zu Text#detectCharset wird hier nicht ein einzelnes
         * Charset zurueckgegeben, sondern alle, die in Frage kommen.
         *
         * @param value Text mit unbekanntem Encoding
         * @return Liste mit Encodings, die in Frage kommen
         * @since 4.2
         */
        @JvmStatic
        fun detectCharsets(value: String): Collection<Charset> {
            val charsets = mutableListOf<Charset>()
            for (cs in Charset.availableCharsets().values) {
                if (isCharset(value, cs)) {
                    charsets.add(cs)
                }
            }
            return charsets
        }

        /**
         * Testet, ob das Encoding fuer den uergebenen Text stimmen kann.
         * Die Idee dahinter ist, dass wir
         * einen Text nach UTF-8 und wieder zurueck konvertieren. Dies ist ein
         * einfacher Ansatz und stammt aus <a
         * href="https://www.turro.org/publications?item=114&page=0">Detect the
         * charset in Java strings</a>, reicht aber fuer einfache Faelle aus.
         *
         * @param value Text
         * @param cs    vermutetes Encoding
         * @return true, wenn das Encoding stimmen koennte
         * @since 4.2
         */
        @JvmStatic
        fun isCharset(value: String, cs: Charset) : Boolean {
            val probe = StandardCharsets.UTF_8
            try {
                return value == String(String(value.toByteArray(cs), probe).toByteArray(probe), cs)
            } catch (ex : UnsupportedOperationException) {
                LOG.fine("$cs wird nicht unterstuetzt: $ex")
                return false
            }
        }

        /**
         * Konvertiert mit JDK-Bordmittel einen Text in ein gewuenschtes
         * Encoding. Allerdings kann je nach Konvertierung das Ergebnis
         * verlustbehaftet sein.
         *
         * @param value Text
         * @param toEncoding gewuenschtes Encoding
         * @since 4.2
         */
        @JvmStatic
        fun convert(value: String, toEncoding: Charset): String {
            val fromEncoding = detectCharset(value)
            return convert(value, toEncoding, fromEncoding)
        }

        /**
         * Konvertiert mit JDK-Bordmittel einen Text in ein gewuenschtes
         * Encoding. Allerdings kann je nach Konvertierung das Ergebnis
         * verlustbehaftet sein.
         *
         * @param value Text
         * @param toEncoding gewuenschtes Encoding
         * @param fromEncoding aktuelles Encoding des Textes
         * @since 4.2
         */
        @JvmStatic
        fun convert(value: String, toEncoding: Charset, fromEncoding: Charset): String {
            var x = value
            if (fromEncoding == StandardCharsets.UTF_8) {
                x = replaceSpecialChars(value, toEncoding)
            }
            return String(x.toByteArray(fromEncoding), toEncoding)
        }

        @JvmStatic
        fun replaceSpecialChars(value: String, encoding: Charset): String {
            val zeichen = value.toCharArray()
            val buffer = CharBuffer.allocate(zeichen.size * 2)
            for (c in zeichen) {
                buffer.put(replaceSpecialChar(c, encoding))
            }
            rewind(buffer)
            return buffer.toString().trim { it <= ' ' }
        }

        private fun replaceSpecialChar(c: Char, encoding: Charset): String {
            if (c.isWhitespace()) {
                return " "
            }
            when (encoding.name()) {
                StandardCharsets.US_ASCII.name() -> return replaceUmlaut(c)
                "IBM850",
                StandardCharsets.ISO_8859_1.name() ->
                    when (c) {
                        '\u20ac' -> return "EUR"
                        else -> return replaceSpecialCharLatin15(c)
                    }
                "ISO-8859-15" -> return replaceSpecialCharLatin15(c)
                else -> return c.toString()
            }
        }

        private fun replaceSpecialCharLatin15(c: Char): String {
            when (c) {
                '\u011b' -> return "e"
                '\u015b', '\u0161' -> return "s"
                '\u0107', '\u010d' -> return "c"
                '\u0141', '\u0142' -> return "l"
                '\u0144' -> return "n"
                '\u017e', '\u017a' -> return "z"
                '\u0e3f' -> return "THB"
                '\u20a9' -> return "KRW"
                '\u20aa' -> return "ILS"
                '\u20ab' -> return "VND"
                '\u20b9' -> return "INR"
                else -> return c.toString()
            }
        }

    }

}
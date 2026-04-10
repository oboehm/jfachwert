/*
 * Copyright (c) 2018-2026 by Oliver Boehm
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
     * Ersetzt Sonderzeichen durch ihr Gegenstueck oder eine Ersatzdarstellung..
     *
     * @return Text ohne Sonderzeichen
     * @since 6.6
     */
    fun replaceSonderzeichen(): Text {
        return of(replaceSonderzeichen(code))
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
        return isPrintable(code)
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
        return detectCharset(code)
    }

    /**
     * Erkennt das Encoding eines Textes. Im Unterschied zu Text#detectCharset
     * werden hier alle erkannte Charsets zurueckgegeben
     *
     * @return Liste mit Encodings, die in Frage kommen
     * @since 4.2
     */
    fun detectCharsets(): Collection<Charset> {
        return detectCharsets(code)
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
        return isCharset(code, cs)
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
     * Limitiert einen String auf das gewuenschte Encoding.
     * Im Gegensatz zu convert(..) wird hier der String nicht in ein
     * anderes Encoding umgewandelt, sondern nur auf die Zeichen
     * beschraaenkt, die das gewuenschte Encoding aufnehmen kann.
     *
     * @param encoding gewuenschtes Encoding
     * @since 6.7
     */
    fun limitTo(encoding: Charset): Text {
        return of(limitTo(code, encoding))
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
         * <p>
         * Anmerkung: der uebergebene Text wird kopiert, weil sonst die
         * verwendete WeakHashMap eine StrongReference daraus macht
         * (s. Issue #29).
         * </p>
         *
         * @param text darf nicht null sein
         * @return Text
         */
        @JvmStatic
        fun of(text: String): Text {
            val copy = String(text.toCharArray())
            return WEAK_CACHE.computeIfAbsent(copy) { s: String -> Text(s) }
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
            return text.trim(' ', '\t', '\r', '\n', '\u00a0', '\u2000', '\u2002', '\u200b', '\u202f')
        }

        /**
         * Ersetzt Umlaute und scharfes 'S'. Auch Accents von Umlauten werden
         * entfernt und einige Sonderzeichen, die nicht in allen gaengigen
         * Zeichensaetzen wie Latin15 vorhanden sind, werden durch ihr
         * Gegeenstueck ersetzt (z.B. die verschiendenen Arten von
         * Gaensefusschen durch '"').
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

        /**
         * Ersetzt einige Sonderzeichen, die nicht in allen gaengigen
         * Zeichensaetzen wie Latin15 vorhanden sind. So werden aus
         * den verschiedenen Arten von Gaensefuesschen ein '"'.
         *
         * @param text Text (mit Sonderzeichen)
         * @return Text ohne Sonderzeichen
         * @since 6.6
         */
        @JvmStatic
        fun replaceSonderzeichen(text: String): String {
            val zeichen = text.toCharArray()
            val buffer = CharBuffer.allocate(zeichen.size * 2)
            for (c in zeichen) {
                //buffer.put(replaceSpecialChar(c))
                buffer.put(replaceNonAscii(c))
            }
            rewind(buffer)
            return buffer.toString().trim { it <= ' ' }
        }

        // Hier werden 8-Bit-Sonderzeichen ersetzt, dessen erstes Bit
        // gesetzt ist.
        private fun replaceNonAscii(c: Char): String {
            when (c) {
                '\u00a1' -> return "!"
                '\u00a3' -> return "GBP"
                '\u00a5' -> return "JPY"
                '\u00e7' -> return "c"
                '\u00a0' -> return " "
                '\u00a4' -> return "XXX"
                '\u00ae' -> return "(R)"
                '\u00bf' -> return "?"
                '\u00f1' -> return "n"
                else -> {
                    val latin = replaceSpecialCharLatin15(c)
                    val buffer = StringBuilder()
                    for (ch in latin) {
                        buffer.append(replaceUmlaut(ch))
                    }
                    return buffer.toString()
                }
            }
        }

        private fun replaceUmlaut(c: Char) : String {
            when (c) {
                '\u00e4' -> return "ae"
                '\u00f6', '\u0151' -> return "oe"
                '\u00fc', '\u0171' -> return "ue"
                '\u00df' -> return "ss"
                '\u00c4' -> return "Ae"
                '\u00d6' -> return "Oe"
                '\u00dc' -> return "Ue"
                '\u00e1', '\u00e0', '\u00e2', '\u00e3' -> return "a"
                '\u00e9', '\u00e8', '\u00ea', '\u00eb' -> return "e"
                '\u00ed' -> return "i"
                '\u00f3', '\u00f2', '\u00f4' -> return "o"
                '\u00fa', '\u00f9', '\u00fb' -> return "u"
                '\u00c1', '\u00c0', '\u00c2', '\u00c6' -> return "A"
                '\u00c9', '\u00c8', '\u00ca' -> return "E"
                '\u00d3', '\u00d2', '\u00d4' -> return "O"
                '\u00da', '\u00d9', '\u00db' -> return "U"
                '\u0152' -> return "OE"
                '\u01fc' -> return "AE"
                '\u016f' -> return "u"
                '\u0300', '\u0301', '\u0303', '\u0308', '\u030b' -> return ""
                //else -> return replaceSpecialChar(c)
                else -> return c.toString()
            }
        }

        // hier behandeln wir das Mormonenalphabet
        // s.a. https://de.wikipedia.org/wiki/Unicodeblock_Mormonenalphabet
        private fun replaceDeseretChar(c: Char) : String {
            when (c) {
                // grosses Mormonen-Alphabet
                '\udc00' -> return "I"
                '\udc01' -> return "E"
                '\udc02' -> return "A"
                '\udc03' -> return "Ah"
                '\udc04' -> return "O"
                '\udc05' -> return "Oo"
                '\udc06' -> return "I"
                '\udc07' -> return "E"
                '\udc08' -> return "A"
                '\udc09' -> return "Ah"
                '\udc0a' -> return "O"
                '\udc0b' -> return "Oo"
                '\udc0c' -> return "Ay"
                '\udc0d' -> return "Ow"
                '\udc0e' -> return "Wu"
                '\udc0f' -> return "Yee"
                '\udc10' -> return "H"
                '\udc11' -> return "Pee"
                '\udc12' -> return "Bee"
                '\udc13' -> return "Tee"
                '\udc14' -> return "Dee"
                '\udc15' -> return "Chee"
                '\udc16' -> return "Jee"
                '\udc17' -> return "Kay"
                '\udc18' -> return "Gay"
                '\udc19' -> return "Ef"
                '\udc1a' -> return "Vee"
                '\udc1b' -> return "Eth"
                '\udc1c' -> return "Thee"
                '\udc1d' -> return "Es"
                '\udc1e' -> return "Zee"
                '\udc1f' -> return "Esh"
                '\udc20' -> return "Zhee"
                '\udc21' -> return "Er"
                '\udc22' -> return "El"
                '\udc23' -> return "Em"
                '\udc24' -> return "En"
                '\udc25' -> return "Eng"
                '\udc26' -> return "Oi"
                '\udc27' -> return "Ew"
                // kleines Mormonen-Alphabet
                '\udc28' -> return "i"
                '\udc29' -> return "e"
                '\udc2a' -> return "a"
                '\udc2b' -> return "ah"
                '\udc2c' -> return "o"
                '\udc2d' -> return "oo"
                '\udc2e' -> return "i"
                '\udc2f' -> return "e"
                '\udc30' -> return "a"
                '\udc31' -> return "ah"
                '\udc32' -> return "o"
                '\udc33' -> return "oo"
                '\udc34' -> return "ay"
                '\udc35' -> return "ow"
                '\udc36' -> return "wu"
                '\udc37' -> return "yee"
                '\udc38' -> return "h"
                '\udc39' -> return "pee"
                '\udc3a' -> return "bee"
                '\udc3b' -> return "tee"
                '\udc3c' -> return "dee"
                '\udc3d' -> return "chee"
                '\udc3e' -> return "jee"
                '\udc3f' -> return "kay"
                '\udc40' -> return "gay"
                '\udc41' -> return "ef"
                '\udc42' -> return "vee"
                '\udc43' -> return "eth"
                '\udc44' -> return "thee"
                '\udc45' -> return "es"
                '\udc46' -> return "zee"
                '\udc47' -> return "esh"
                '\udc48' -> return "zhee"
                '\udc49' -> return "er"
                '\udc4a' -> return "el"
                '\udc4b' -> return "em"
                '\udc4c' -> return "en"
                '\udc4d' -> return "eng"
                '\udc4e' -> return "oi"
                '\udc4f' -> return "ew"
                else -> return "\ud801" + c
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

        /**
         * Limitiert einen String auf das gewuenschte Encoding.
         * Im Gegensatz zu convert(..) wird hier der String nicht in ein
         * anderes Encoding umgewandelt, sondern nur auf die Zeichen
         * beschraaenkt, die das gewuenschte Encoding aufnehmen kann.
         *
         * @param value Text
         * @param encoding gewuenschtes Encoding
         * @since 6.7
         */
        @JvmStatic
        fun limitTo(value: String, encoding: Charset): String {
            return convert(convert(value, encoding, StandardCharsets.UTF_8), StandardCharsets.UTF_8)
        }

        @JvmStatic
        fun replaceSpecialChars(value: String, encoding: Charset): String {
            val zeichen = value.toCharArray()
            val buffer = CharBuffer.allocate(zeichen.size * 2)
            var i = 0
            while (i < zeichen.size) {
                val c = zeichen[i]
                if (c == '\ud801') {
                    i++
                    buffer.put(replaceDeseretChar(zeichen[i]))
                } else {
                    buffer.put(replaceSpecialChar(c, encoding))
                }
                i++
            }
            rewind(buffer)
            return buffer.toString().trim { it <= ' ' }
        }

        private fun replaceSpecialChar(c: Char, encoding: Charset): String {
            when (encoding.name()) {
                StandardCharsets.US_ASCII.name() -> return replaceNonAscii(c)
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

        /**
         * Hier werden alle Zeichen > u00ff ersetzt.
         */
        private fun replaceSpecialCharLatin15(c: Char): String {
            return when (c) {
                // --------- Latin‑Extended‑A (U+0100 .. U+017F) ------ https://www.compart.com/en/unicode/block/U+0100
                '\u0100', '\u0102', '\u0104' -> "A"
                '\u0101', '\u0103', '\u0105' -> "a"
                '\u0106', '\u0108', '\u010A', '\u010C' -> "C"
                '\u0107', '\u0109', '\u010B', '\u010D' -> "c"
                '\u010E', '\u0110' -> "D"
                '\u010F', '\u0111' -> "d"
                '\u0112', '\u0114', '\u0116', '\u0118', '\u011A' -> "E"
                '\u0113', '\u0115', '\u0117', '\u0119', '\u011B' -> "e"
                '\u011C', '\u011E', '\u0120', '\u0122' -> "G"
                '\u011D', '\u011F', '\u0121', '\u0123' -> "g"
                '\u0124', '\u0126' -> "H"
                '\u0125', '\u0127' -> "h"
                '\u0128', '\u012A', '\u012C', '\u012E', '\u0130' -> "I"
                '\u0129', '\u012B', '\u012D', '\u012F', '\u0131' -> "i"
                '\u0132' -> "IJ"
                '\u0133' -> "ij"
                '\u0134' -> "J"
                '\u0135' -> "j"
                '\u0136' -> "K"
                '\u0137' -> "k"
                '\u0139', '\u013B', '\u013D', '\u013F', '\u0141', '\u023D' -> "L"
                '\u013A', '\u013C', '\u013E', '\u0140', '\u0142' -> "l"
                '\u0143', '\u0145', '\u0147', '\u014A' -> "N"
                '\u0144', '\u0146', '\u0148', '\u014B' -> "n"
                '\u0149' -> "n"
                '\u014C', '\u014E' -> "O"
                '\u014D', '\u014F' -> "o"
                '\u0150' -> "\u00d6" // Umlaut Oe
                '\u0151' -> "\u00f6" // Umlaut oe
                '\u0152' -> "OE"
                '\u0153' -> "oe"
                '\u0154', '\u0156', '\u0158' -> "R"
                '\u0155', '\u0157', '\u0159' -> "r"
                '\u015A', '\u015C', '\u015E', '\u0160' -> "S"
                '\u015B', '\u015D', '\u015F', '\u0161' -> "s"
                '\u0162', '\u0164', '\u0166' -> "T"
                '\u0163', '\u0165', '\u0167' -> "t"
                '\u0168', '\u016A', '\u016C', '\u016E', '\u0172' -> "U"
                '\u0169', '\u016B', '\u016D', '\u016F', '\u0173' -> "u"
                '\u0170' -> "\u00dc" // Umlaut Ue
                '\u0171' -> "\u00fc" // Umlaut ue
                '\u0174' -> "W"
                '\u0175' -> "w"
                '\u0176', '\u0178' -> "Y"
                '\u0177' -> "y"
                '\u0179', '\u017B', '\u017D' -> "Z"
                '\u017A', '\u017C', '\u017E' -> "z"
                '\u01fc' -> "AE"
                '\u01fd' -> "ae"
                // --------- Latin‑Extended‑B (U+0180 .. U+024F) ------ https://www.compart.com/en/unicode/block/U+0180
                '\u0181', '\u0182', '\u0243' -> "B"
                '\u0180', '\u0183', '\u0184', '\u0185' -> "b"
                '\u0186', '\u019F', '\u01A0', '\u01A2', '\u022A', '\u022C', '\u022E', '\u0230' -> "O"
                '\u0187', '\u023B' -> "C"
                '\u0188', '\u023C' -> "c"
                '\u0189', '\u018A', '\u018B' -> "D"
                '\u018C', '\u018D' -> "d"
                '\u018E', '\u0190', '\u0246' -> "E"
                '\u018F', '\u0247' -> "e"
                '\u0191' -> "F"
                '\u0192' -> "f"
                '\u0193', '\u0194' -> "G"
                '\u0195' -> "hv"
                '\u0196', '\u0197' -> "I"
                '\u0198' -> "K"
                '\u0199' -> "k"
                '\u019A', '\u019B', '\u0234' -> "l"
                '\u019C' -> "M"
                '\u019D', '\u0220' -> "N"
                '\u019E', '\u0235' -> "n"
                '\u01A1', '\u01A3', '\u022B', '\u022D', '\u022F', '\u0231' -> "o"
                '\u01A4' -> "P"
                '\u01A5' -> "p"
                '\u01A6', '\u024C' -> "R"
                '\u01A7', '\u01A9', '\u0218' -> "S"
                '\u01A8', '\u01AA', '\u0219', '\u023F' -> "s"
                '\u01AC', '\u01AE', '\u021A', '\u023E' -> "T"
                '\u01AD', '\u021B', '\u0236' -> "t"
                '\u01AF' -> "U"
                '\u01B0' -> "u"
                '\u01B2' -> "V"
                '\u01B3' -> "Y"
                '\u01B4' -> "y"
                '\u01B5', '\u01B7', '\u0224' -> "Z"
                '\u01B6', '\u01B8', '\u01B9', '\u01BA', '\u01BB', '\u01BC', '\u01BD', '\u01BE', '\u0225', '\u0240' -> "z"
                '\u01BF' -> "w"
                '\u01C4' -> "DZ"
                '\u01C5' -> "Dz"
                '\u01C6' -> "dz"
                '\u01C7' -> "LJ"
                '\u01C8' -> "Lj"
                '\u01C9' -> "lj"
                '\u01CA' -> "NJ"
                '\u01CB' -> "Nj"
                '\u01CC' -> "nj"
                '\u01CF' -> "I"
                '\u01D0' -> "i"
                '\u01D3', '\u01D5', '\u01D7', '\u01D9', '\u01DB', '\u0216', '\u0244' -> "U"
                '\u01D4', '\u01D6', '\u01D8', '\u01DA', '\u01DC', '\u0217' -> "u"
                '\u01F1' -> "DZ"
                '\u01F2' -> "Dz"
                '\u01F3' -> "dz"
                '\u0200' -> "\u00c4"    // Umlaut Ae
                '\u0202', '\u0204', '\u0206', '\u0208', '\u023A' -> "A"
                '\u0201' -> "\u00e4"    // Umlaut ae
                '\u0203', '\u0205', '\u0207', '\u0209' -> "a"
                '\u020A', '\u020C', '\u020E', '\u0210', '\u0212', '\u0228' -> "E"
                '\u020B', '\u020D', '\u020F', '\u0211', '\u0213', '\u0229' -> "e"
                '\u0214' -> "\u00dc"    // Umlaut Ue
                '\u0215' -> "\u00fc"    // Umlaut ue
                '\u021C', '\u021D' -> "3"
                '\u021E' -> "H"
                '\u021F' -> "h"
                '\u0221' -> "d"
                '\u0222' -> "Ou"
                '\u0223' -> "ou"
                '\u0232', '\u0233', '\u024E' -> "Y"
                '\u0237', '\u0249', '\u024F' -> "j"
                '\u0238' -> "db"
                '\u0239' -> "qp"
                '\u0248' -> "J"
                '\u024A' -> "Q"
                '\u024B' -> "q"
                '\u024D' -> "r"
                // --------- Waehrungen und Anderes
                '\u20ac' -> "EUR"
                '\u20b1' -> "PHP"
                '\u0300', '\u0301', '\u0303', '\u0308', '\u030b' -> ""
                '\u0e3f' -> "THB"
                '\u041c' -> "M"
                '\u0421' -> "C"
                '\u20a9' -> "KRW"
                '\u20aa' -> "ILS"
                '\u20ab' -> "VND"
                '\u20b9' -> "INR"
                '\u2010', '\u2011', '\u2012', '\u2013', '\u2014', '\u2212' -> "-"
                '\u201c', '\u201d', '\u201e', '\u00ab', '\u00bb' -> "\""
                '\u00b4', '\u2018', '\u201a' -> "'"
                '\u2000', '\u2002', '\u200b', '\u202f' -> " "
                '\u2122' -> "(TM)"
                '\u2260' -> "!="
                '\u2264' -> "<="
                '\u2265' -> ">="
                else -> c.toString()
            }
        }

    }

}
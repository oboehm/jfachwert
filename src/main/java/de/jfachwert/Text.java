/*
 * Copyright (c) 2018 by Oliver Boehm
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
package de.jfachwert;

import de.jfachwert.pruefung.NullValidator;

import java.nio.CharBuffer;
import java.util.WeakHashMap;

/**
 * Die Klasse Text ist der einfachste Fachwerte, der eigentlich nur ein
 * Wrapper um die String-Klasse ist. Allerdings mit dem Unterschied, dass
 * man keinen Null-Text oder leeren Text anlegen kann.
 * <p>
 * Diese Klasse wurde mit der {@link FachwertFactory} eingefuehrt. Sie dient
 * dort als Fallback, wenn kein Fachwert erzeugt werden kann, auf den der
 * uebergebene Name passt.
 * </p>
 *
 * @author oboehm
 * @since 0.5 (17.01.2018)
 */
public class Text extends AbstractFachwert<String, Text> implements Comparable<Text> {

    private static final SimpleValidator<String> VALIDATOR = new NullValidator<>();
    private static final WeakHashMap<String, Text> WEAK_CACHE = new WeakHashMap<>();

    /** Null-Konstante fuer Initialisierungen . */
    public static final Text NULL = new Text("");

    /**
     * Erzeugt einen Text.
     * 
     * @param text darf nicht null sein 
     */
    public Text(String text) {
        this(text, VALIDATOR);
    }

    /**
     * Erzeugt einen Text, der mit dem uebergebenen Validator vor ueberprueft
     * wird.
     *
     * @param text      Text
     * @param validator Validator fuer die Ueberpruefung
     */
    public Text(String text, SimpleValidator<String> validator) {
        super(VALIDATOR.verify(text).intern(), validator);
    }

    /**
     * Liefert einen Text zurueck.
     *
     * @param text darf nicht null sein
     * @return Text
     */
    public static Text of(String text) {
        return WEAK_CACHE.computeIfAbsent(text, Text::new);
    }

    /**
     * Ueberprueft den uebergebenen Text.
     * 
     * @param text Text
     * @return den validierten Text zur Weiterverabeitung
     */
    public static String validate(String text) {
        return VALIDATOR.validate(text);
    }

    /**
     * Berechnet die Levenshtein-Distanz.
     *
     * @param other anderer Text
     * @return Levenshtein-Distanz
     * @since 2.0
     */
    public int getDistanz(Text other) {
        return getDistanz(other.getCode());
    }

    /**
     * Berechnet die Levenshtein-Distanz. Der Algorithmus dazu stammt aus
     * http://rosettacode.org/wiki/Levenshtein_distance#Java.
     *
     * @param other anderer Text
     * @return Levenshtein-Distanz
     * @since 2.0
     */
    public int getDistanz(String other) {
        return distance(this.getCode(), other);
    }

    private static int distance(String a, String b) {
        a = a.toLowerCase();
        b = b.toLowerCase();
        // i == 0
        int[] costs = new int[b.length() + 1];
        for (int j = 0; j < costs.length; j++) {
            costs[j] = j;
        }
        for (int i = 1; i <= a.length(); i++) {
            // j == 0; nw = lev(i - 1, j)
            costs[0] = i;
            int nw = i - 1;
            for (int j = 1; j <= b.length(); j++) {
                int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]),
                        a.charAt(i - 1) == b.charAt(j - 1) ? nw : nw + 1);
                nw = costs[j];
                costs[j] = cj;
            }
        }
        return costs[b.length()];
    }

    /**
     * Ersetzt Umlaute und scharfes 'S'.
     *
     * @return Text ohne Umlaut und scharfem 's'
     * @since 2.1
     */
    public final Text replaceUmlaute() {
        return Text.of(replaceUmlaute(getCode()));
    }

    /**
     * Ersetzt Umlaute und scharfes 'S'. Diese Methode wurde als statische
     * Methode herausgezogen, da sie an anderen Stellen benoetigt werden.
     * <p>
     * Mit v2.2.2 wurde die Methode optimiert, da der alte Ansatz mit
     * {@link String#replace(CharSequence, CharSequence)} sich als Flaschenhals
     * beim Vergleich grosser Datenmenge herausstellte. Durch die Umstellung
     * auf zeichenweises Mapping ist diese Methode jetzt ca. 4 x schneller.
     * </p>
     *
     * @param text Text (mit Umlaute)
     * @return Text ohne Umlaut und scharfem 's'
     * @since 2.1
     */
    public static String replaceUmlaute(String text) {
        char[] zeichen = text.toCharArray();
        CharBuffer buffer = CharBuffer.allocate(zeichen.length * 2);
        for (char c : zeichen) {
            switch (c) {
                case '\u00e4':
                    buffer.put("ae");
                    break;
                case '\u00f6':
                    buffer.put("oe");
                    break;
                case '\u00fc':
                    buffer.put("ue");
                    break;
                case '\u00df':
                    buffer.put("ss");
                    break;
                case '\u00c4':
                    buffer.put("Ae");
                    break;
                case '\u00d6':
                    buffer.put("Oe");
                    break;
                case '\u00dc':
                    buffer.put("Ue");
                    break;
                case '\u00e1':
                case '\u00e0':
                case '\u00e2':
                    buffer.put('a');
                    break;
                case '\u00e9':
                case '\u00e8':
                case '\u00ea':
                case '\u00eb':
                    buffer.put('e');
                    break;
                case '\u00f3':
                case '\u00f2':
                case '\u00f4':
                    buffer.put('o');
                    break;
                case '\u00fa':
                case '\u00f9':
                case '\u00fb': 
                    buffer.put('u');
                    break;
                case '\u00c1':
                case '\u00c0':
                case '\u00c2':
                    buffer.put('A');
                    break;
                case '\u00c9':
                case '\u00c8':
                case '\u00ca':
                    buffer.put('E');
                    break;
                case '\u00d3':
                case '\u00d2':
                case '\u00d4':
                    buffer.put('O');
                    break;
                case '\u00da':
                case '\u00d9':
                case '\u00db':
                    buffer.put('U');
                    break;
                default:
                    buffer.put(c);
                    break;
            }
        }
        buffer.rewind();
        return buffer.toString().trim();
    }

    /**
     * Liefert einen Text mit Kleinbuchstaben.
     *
     * @return Text mit Kleinbuchstaben
     * @since 2.1
     */
    public Text toLowerCase() {
        return Text.of(getCode().toLowerCase());
    }

    /**
     * Liefert einen Text mit Grossbuchstaben.
     *
     * @return Text mit Grossbuchstaben
     * @since 2.1
     */
    public Text toUpperCase() {
        return Text.of(getCode().toUpperCase());
    }

    /**
     * Ignoriert beim Vergleich Gross- und Kleinschreibung.
     *
     * @param other der anderer Text
     * @return true bei Gleichheit
     * @since 2.1
     */
    public boolean equalsIgnoreCase(Text other) {
        return this.getCode().equalsIgnoreCase(other.getCode());
    }

    /**
     * Ignoriert beim Vergleich die Umlaute.
     *
     * @param other der anderer Text
     * @return true bei Gleichheit
     * @since 2.2
     */
    public boolean equalsIgnoreUmlaute(Text other) {
        return this.replaceUmlaute().equals(other.replaceUmlaute());
    }

    /**
     * Ignoriert beim Vergleich die Umlaute sowie Gross- und Kleinschreibung.
     *
     * @param other der anderer Text
     * @return true bei Gleichheit
     * @since 2.2
     */
    public boolean equalsIgnoreCaseAndUmlaute(Text other) {
        return this.replaceUmlaute().equalsIgnoreCase(other.replaceUmlaute());
    }

    /**
     * Dient zum (alphabetischen) Vergleich zweier Texte.
     *
     * @param other der andere Text
     * @return negtive Zahl, falls this &lt; other, 0 bei Gleichheit, ansonsten
     * positive Zahl.
     * @since 3.0
     */
    @Override
    public int compareTo(Text other) {
        return this.getCode().compareTo(other.getCode());
    }

}

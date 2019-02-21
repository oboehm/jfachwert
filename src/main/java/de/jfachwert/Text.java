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
import de.jfachwert.pruefung.exception.LocalizedIllegalArgumentException;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ValidationException;
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
public class Text extends AbstractFachwert<String> {

    private static final SimpleValidator<String> VALIDATOR = new NullValidator<>();
    private static final WeakHashMap<String, Text> WEAK_CACHE = new WeakHashMap<>();

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
        super(verify(text).intern(), validator);
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

    private static String verify(String text) {
        try {
            return validate(text);
        } catch (ValidationException ex) {
            throw new LocalizedIllegalArgumentException(ex);
        }
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
     *
     * @param text Text (mit Umlaute)
     * @return Text ohne Umlaut und scharfem 's'
     * @since 2.1
     */
    public static String replaceUmlaute(String text) {
        String s = text.replace("\u00fc", "ue").replace("\u00f6", "oe").replace("\u00e4", "ae")
                            .replace("\u00df", "ss").replaceAll("\u00dc(?=[a-z\u00e4\u00f6\u00fc\u00df ])", "Ue")
                            .replaceAll("\u00d6(?=[a-z\u00e4\u00f6\u00fc\u00df ])", "Oe")
                            .replaceAll("\u00c4(?=[a-z\u00e4\u00f6\u00fc\u00df ])", "Ae").replace("\u00dc", "UE")
                            .replace("\u00d6", "OE").replace("\u00c4", "AE");
        return StringUtils
                .replaceChars(s, "\u00e1\u00e0\u00e2\u00e9\u00e8\u00ea\u00eb\u00f3\u00f2\u00f4\u00fa\u00f9\u00fb" +
                        "\u00c1\u00c0\u00c2\u00c9\u00c8\u00ca\u00d3\u00d2\u00d4\u00da\u00d9\u00db",
                "aaaeeeeooouuuAAAEEEOOOUUU");
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

}

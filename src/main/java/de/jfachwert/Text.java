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

import javax.validation.ValidationException;

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

    /**
     * Erzeugt einen Text.
     * 
     * @param text darf nicht null sein 
     */
    public Text(String text) {
        super(verify(text).intern());
    }

    /**
     * Liefert einen Text zurueck.
     *
     * @param text darf nicht null sein
     * @return Text
     */
    public static Text of(String text) {
        return new Text(text);
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
     * @since 1.1.1
     */
    public int getDistanz(Text other) {
        return getDistanz(other.getCode());
    }

    /**
     * Berechnet die Levenshtein-Distanz.
     *
     * @param other anderer Text
     * @return Levenshtein-Distanz
     * @deprecated bitte {@link #getDistanz(Text)} verwenden
     */
    @Deprecated
    public int getDistance(Text other) {
        return getDistanz(other.getCode());
    }

    /**
     * Berechnet die Levenshtein-Distanz. Der Algorithmus dazu stammt aus
     * http://rosettacode.org/wiki/Levenshtein_distance#Java.
     *
     * @param other anderer Text
     * @return Levenshtein-Distanz
     * @since 1.1.1
     */
    public int getDistanz(String other) {
        return distance(this.getCode(), other);
    }

    /**
     * Berechnet die Levenshtein-Distanz. Der Algorithmus dazu stammt aus
     * http://rosettacode.org/wiki/Levenshtein_distance#Java.
     *
     * @param other anderer Text
     * @return Levenshtein-Distanz
     * @deprecated bitte {@link #getDistanz(String)} verwenden
     */
    @Deprecated
    public int getDistance(String other) {
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

}

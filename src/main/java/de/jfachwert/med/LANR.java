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
 * (c)reated 12.12.2018 by oboehm (ob@oasd.de)
 */
package de.jfachwert.med;

import de.jfachwert.AbstractFachwert;
import de.jfachwert.SimpleValidator;
import de.jfachwert.pruefung.LengthValidator;
import de.jfachwert.pruefung.Mod10Verfahren;
import de.jfachwert.pruefung.NullValidator;

import java.util.WeakHashMap;

/**
 * Die LANR ist die lebenslange Arztnummer. Sie ist eine neunstellige Nummer,
 * die bundesweit von der jeweiligen zustaendigen Kassenaerztlichen Vereinigung
 * an jeden Arzt und Psychotherapeuten vergeben wird, der an der
 * vertragsaertzlichen Versorgung teilnimmt.
 *
 * @author oboehm
 * @since 1.1 (12.12.2018)
 */
public class LANR extends AbstractFachwert<Integer> {

    private static final LengthValidator<Integer> VALIDATOR = new LengthValidator<>(4, 9);
    private static final WeakHashMap<Integer, LANR> WEAK_CACHE = new WeakHashMap<>();

    /** Null-Konstante fuer Initialisierungen. */
    public static final LANR NULL = new LANR(0, new NullValidator<>());

    /** Pseudonummer fuer Bundeswehraerzte, Zahnaerzte und Hebammen. */
    public static final LANR PSEUDO_NUMMER = LANR.of(999999900);

    /**
     * Erzeugt ein neues LANR-Objekt.
     *
     * @param code neunstellige Zahl
     */
    public LANR(String code) {
        this(Integer.parseInt(code));
    }

    /**
     * Erzeugt ein neues LANR-Objekt.
     *
     * @param code neunstellige Zahl
     */
    public LANR(int code) {
        this(code, VALIDATOR);
    }

    /**
     * Erzeugt ein neues LANR-Objekt.
     *
     * @param code      neunstellige Zahl
     * @param validator Validator zur Pruefung der Zahl
     */
    public LANR(int code, SimpleValidator<Integer> validator) {
        super(code, validator);
    }

    /**
     * Liefert eine LANR zurueck.
     *
     * @param code 9-stellige Nummer
     * @return die LANR
     */
    public static LANR of(int code) {
        return WEAK_CACHE.computeIfAbsent(code, LANR::new);
    }

    /**
     * Liefert eine LANR zurueck.
     *
     * @param code 9-stellige Nummer
     * @return die LANR
     */
    public static LANR of(String code) {
        return of(Integer.parseInt(code));
    }

    /**
     * Ueberprueft die uebergebenen Nummer, ob sie 9-stellig und eine
     * korrekte LANR darstellt. Die Pruefziffer wird nicht ueberprueft,
     * weil sie optional ist und nicht unbedingt stimmen muss.
     *
     * @param nummer 9-stellige Nummer
     * @return die Nummer selbst zur Weiterverarbeitung
     */
    public static int validate(int nummer) {
        return VALIDATOR.validate(nummer);
    }

    /**
     * Die ersten 7 Ziffern kennzeichnen die Arztnummer, die von der
     * Kassenaerztlichen Vereinigung generiert wird. Die 7. Stelle ist
     * dabei die Pruefziffer, die aber leider nicht einheitlich
     * generiert wird und daher zur Pruefung schlecht herangezogen werden
     * kann.
     *
     * @return 7-stellige Arztnummer (inkl. Pruefziffer)
     */
    public int getArztnummer() {
        return this.getCode() / 100;
    }

    /**
     * Stelle 7 ist die Pruefziffer, die aber wertlos ist, da sie nicht
     * einheitlich generiert wird.
     *
     * @return Zahl zwischen 0 und 9
     */
    public int getPruefziffer() {
        return this.getCode() / 100 % 10;
    }


    /**
     * Hier wird die 7-stellige Arztnummer ueberprueft, ob die Pruefziffer
     * gueltig ist. Diese wird nach dem Modulo10-Verfahren mit der Gewichtung
     * 4 und 9 ueberprueft. Allerdings wird die Pruefung von den
     * verschiedenen Instituten teilweise unterschiedlich interpretiert,
     * so dass das Ergebnis mit Vorsicht zu geniessen ist.
     *
     * @return true, wenn Pruefziffer uebereinstimmt
     */
    public boolean isValid() {
        Mod10Verfahren mod10Verfahren = new Mod10Verfahren(4, 9);
        return mod10Verfahren.isValid(Integer.toString(getArztnummer()));
    }

    /**
     * Die letzten beiden Ziffern der LANR bilden die Fachgruppe.
     *
     * @return Zahl zwischen 1 und 99
     */
    public int getFachgruppe() {
        return this.getCode() % 100;
    }

    /**
     * Neben "999999900" als Pseudo-Arztnummer gibt es noch weitere Nummern,
     * die als Pseudo-Nummer angesehen werden. So ist nach
     * https://www.aok-gesundheitspartner.de/imperia/md/gpp/nordost/heilberufe/datenaustausch/lieferbedingungen.pdf
     * die "3333333xx" und "4444444xx" eine Pseudo-Nummer, und nach
     * https://www.gkv-datenaustausch.de/media/dokumente/leistungserbringer_1/krankenhaeuser/fortschreibungen_1/20170522_14_fs.pdf
     * die "555555..." solch eine Pseudo-Nummer.
     *
     * @return true oder false
     * @since 2.3
     */
    public boolean isPseudoNummer() {
        int arztNr = getArztnummer();
        switch (arztNr) {
            case 3333333:
            case 4444444:
            case 9999999:
                return true;
            default:
                return (arztNr / 10) == 555555;
        }
    }

    /**
     * Die LANR ist 9-stellig und wird auch neunstellig ausgegeben.
     *
     * @return 9-stellige Zeichenkette, evtl. mit fuehrenden Nullen
     */
    @Override
    public String toString() {
        return String.format("%09d", this.getCode());
    }

}

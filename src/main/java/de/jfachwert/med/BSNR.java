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
 * (c)reated 16.12.2018 by oboehm (ob@oasd.de)
 */
package de.jfachwert.med;

import de.jfachwert.AbstractFachwert;
import de.jfachwert.SimpleValidator;
import de.jfachwert.pruefung.LengthValidator;
import de.jfachwert.pruefung.NullValidator;

import java.util.WeakHashMap;

/**
 * Die Betriebstaettennummer (BSNR) ist eine neunstellige Nummer, die im Rahmen
 * der vertragsaerztlichen Versorgung den Ort der Leistungserbringung
 * (Betriebsstaette) eindeutig identifiziert. Sie wurde zusammen mit der
 * {@link LANR} 2008 bundesweit eingefuehrt.
 *
 * @author oboehm
 * @since 1.1 (16.12.2018)
 */
public class BSNR extends AbstractFachwert<Integer> {

    private static final LengthValidator<Integer> VALIDATOR = new LengthValidator<>(2, 9);
    private static final WeakHashMap<Integer, BSNR> WEAK_CACHE = new WeakHashMap<>();

    /** Null-Konstante fuer Initialisierungen. */
    public static final BSNR NULL = new BSNR(0, new NullValidator<>());

    /** Pseudonummer. */
    public static final BSNR PSEUDO_NUMMER = BSNR.of(179999900);

    /**
     * Erzeugt ein neues BSNR-Objekt.
     *
     * @param code neunstellige Zahl
     */
    public BSNR(String code) {
        this(Integer.parseInt(code));
    }

    /**
     * Erzeugt ein neues BSNR-Objekt.
     *
     * @param code neunstellige Zahl
     */
    public BSNR(int code) {
        this(code, VALIDATOR);
    }

    /**
     * Erzeugt ein neues BSNR-Objekt.
     *
     * @param code neunstellige Zahl
     * @param validator Validator zur Pruefung der Zahl
     */
    public BSNR(int code, SimpleValidator<Integer> validator) {
        super(code, validator);
    }

    /**
     * Liefert eine BSNR zurueck.
     *
     * @param code 9-stellige Nummer
     * @return die BSNR
     */
    public static BSNR of(int code) {
        return WEAK_CACHE.computeIfAbsent(code, BSNR::new);
    }

    /**
     * Liefert eine BSNR zurueck.
     *
     * @param code 9-stellige Nummer
     * @return die BSNR
     */
    public static BSNR of(String code) {
        return of(Integer.parseInt(code));
    }

    /**
     * Ueberprueft die uebergebenen Nummer, ob sie 9-stellig und eine
     * korrekte BSNR darstellt. Die Pruefziffer wird nicht ueberprueft,
     * weil sie optional ist und nicht unbedingt stimmen muss.
     *
     * @param nummer 9-stellige Nummer
     * @return die Nummer selbst zur Weiterverarbeitung
     */
    public static int validate(int nummer) {
        return VALIDATOR.validate(nummer);
    }

    /**
     * Laut Wikipedia ist "179999900" eine Pseudo-Nummer. In diesem Fall gibt
     * diese Methode "true" zurueck.
     *
     * @return true oder false
     * @since 2.3
     */
    public boolean isPseudoNummer() {
        return getCode() == 179999900;
    }

    /**
     * Die BSNR ist 9-stellig und wird auch neunstellig ausgegeben.
     *
     * @return 9-stellige Zeichenkette, evtl. mit fuehrenden Nullen
     */
    @Override
    public String toString() {
        return String.format("%09d", this.getCode());
    }
    
}

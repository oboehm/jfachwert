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
import de.jfachwert.pruefung.LengthValidator;
import de.jfachwert.pruefung.exception.LocalizedIllegalArgumentException;

import javax.validation.ValidationException;

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

    private static final LengthValidator<Integer> VALIDATOR = new LengthValidator<>(9, 9);

    /** Pseudonummer fuer Bundeswehraerzte, Zahnaerzte und Hebammen. */
    public static final LANR PSEUDO_NUMMER = new LANR(999999900);

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
        super(verify(code));
    }

    /**
     * Liefert eine LANR zurueck.
     *
     * @param code 9-stellige Nummer
     * @return die LANR
     */
    public static LANR of(int code) {
        return new LANR(code);
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

    private static int verify(int nummer) {
        try {
            return validate(nummer);
        } catch (ValidationException ex) {
            throw new LocalizedIllegalArgumentException(ex);
        }
    }

}

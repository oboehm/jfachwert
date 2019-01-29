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
 * (c)reated 10.12.2018 by oboehm (ob@oasd.de)
 */
package de.jfachwert.med;

import de.jfachwert.AbstractFachwert;
import de.jfachwert.PruefzifferVerfahren;
import de.jfachwert.pruefung.LengthValidator;
import de.jfachwert.pruefung.LuhnVerfahren;
import de.jfachwert.pruefung.exception.LocalizedIllegalArgumentException;

import javax.validation.ValidationException;
import java.util.WeakHashMap;

/**
 * Die Klasse IK repraesentiert das neunstellige Instituionskennzeichen, das
 * bundesweit eindeutig ist. Es wird fuer Abrechnungen im Bereich der deutschen
 * Sozialversicherung verwendet.
 * <p>
 * Die IK selbst ist eine neunstellige Ziffernfolge, die wie folgt aufgabaut
 * ist:
 * </p>
 * <ul>
 *     <li>1+2: Klassifikation (beginnend bei 10)</li>
 *     <li>3+4: Regionalbereich</li>
 *     <li>5-8: Seriennummer</li>
 *     <li>9: Pruefziffer (aus den Stellen 3 bis 8)</li>
 * </ul>
 *
 * @author oboehm
 * @since 1.1 (10.12.2018)
 */
public class IK extends AbstractFachwert<Integer> {

    private static final PruefzifferVerfahren<String> MOD10 = new LuhnVerfahren();
    private static final LengthValidator<Integer> VALIDATOR = new LengthValidator<>(9, 9);
    private static final WeakHashMap<Integer, IK> WEAK_CACHE = new WeakHashMap<>();

    /**
     * Erzeugt ein neues IK-Objekt.
     *
     * @param code Institutionskennzeichen (mit Pruefziffer), z.B. "260326822"
     */
    public IK(String code) {
        this(Integer.parseInt(code));
    }
    
    /**
     * Erzeugt ein neues IK-Objekt.
     *
     * @param code Institutionskennzeichen (mit Pruefziffer), z.B. 260326822
     */
    public IK(int code) {
        super(verify(code));
    }

    /**
     * Liefert eine IK zurueck.
     *
     * @param ik 9-stelliges Insituionskennzeichen
     * @return eine gueltige IK
     */
    public static IK of(int ik) {
        return WEAK_CACHE.computeIfAbsent(ik, IK::new);
    }

    /**
     * Liefert eine IK zurueck.
     *
     * @param ik 11-stelliges Insituionskennzeichen
     * @return eine gueltige IK
     */
    public static IK of(String ik) {
        return of(Integer.parseInt(ik));
    }

    /**
     * Ueberprueft die uebergebenen Nummer, ob sie 9-stellig und eine
     * korrekte IK darstellt.
     *
     * @param nummer 9-stellige Nummer
     * @return die Nummer selbst zur Weiterverarbeitung
     */
    public static int validate(int nummer) {
        int n = VALIDATOR.validate(nummer);
        MOD10.validate(Integer.toString(n));
        return n;
    }

    private static int verify(int nummer) {
        try {
            return validate(nummer);
        } catch (ValidationException ex) {
            throw new LocalizedIllegalArgumentException(ex);
        }
    }

    /**
     * Die ersten beiden Ziffern bilden die Klassifikation, die hierueber
     * zurueckgegeben werden kann.
     *
     * @return Zahl zwischen 10 und 99
     */
    public int getKlassifikation() {
        return getCode() / 10_000_000;
    }

    /**
     * Ziffer 3 und 4 stehen fuer den Regionalbereich. So steht z.B. 91 fuer
     * die Region Schwaben.
     *
     * @return Zahl zwischen 0 und 99
     */
    public int getRegionalbereich() {
        return getCode() / 100_000 % 100;
    }

    /**
     * Die Seriennummer ermittelt sich aus Ziffer 5 bis 8.
     *
     * @return 4-stellige Zahl
     */
    public int getSeriennummer() {
        return getCode() / 10 % 10000;
    }

    /**
     * Die letzte Ziffer ist die Pruefziffer.
     *
     * @return Ziffer zwischen 0 und 9
     */
    public int getPruefziffer() {
        return getCode() / 100_000_000;
    }
    
}

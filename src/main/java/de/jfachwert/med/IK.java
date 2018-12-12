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
import de.jfachwert.pruefung.Mod10Verfahren;
import de.jfachwert.pruefung.exception.LocalizedIllegalArgumentException;

import javax.validation.ValidationException;

/**
 * Die Klasse IK repraesentiert das neunstellige Instituionskennzeichen, das
 * bundesweit eindeutig ist. Es wird fuer Abrechnungen im Bereich der deutschen
 * Sozialversicherung verwendet.
 * <p>
 * Die IK selbst ist eine neunstellige Ziffernfolge, die wie folgt aufgabaut
 * ist:
 * <ul>
 *     <li>1+2: Klassifikation (beginnend bei 10)</li>
 *     <li>3+4: Regionalbereich</li>
 *     <li>5-8: Seriennummer</li>
 *     <li>9: Pruefziffer (aus den Stellen 3 bis 8)</li>
 * </ul>
 * </p>
 *
 * @author oboehm
 * @since 1.1 (10.12.2018)
 */
public class IK extends AbstractFachwert<Integer> {

    private static final PruefzifferVerfahren<Integer> MOD10 = new Mod10Verfahren();
    private static final LengthValidator<Integer> VALIDATOR = new LengthValidator<>(9, 9);

    /**
     * Erzeugt ein neues IK-Objekt.
     *
     * @param code Institutionskennzeichen (mit Pruefziffer), z.B. "260326822"
     */
    public IK(String code) {
        this(Integer.parseInt(code));
    }

    /**
     * Dieser Konstruktor ist hauptsaechlich fuer abgeleitete Klassen gedacht,
     * damit diese das {@link PruefzifferVerfahren} ueberschreiben koennen.
     * Man kann es auch verwenden, um das PruefzifferVerfahren abzuschalten,
     * indem man das {@link de.jfachwert.pruefung.NoopVerfahren} verwendet.
     *
     * @param code        Instituionskennzeichen
     * @param pzVerfahren das verwendete PruefzifferVerfahren
     */
    public IK(int code, PruefzifferVerfahren<Integer> pzVerfahren) {
        super(verify(code), pzVerfahren);
    }

    /**
     * Erzeugt ein neues IK-Objekt.
     *
     * @param code Institutionskennzeichen (mit Pruefziffer), z.B. 260326822
     */
    public IK(int code) {
        this(code, MOD10);
    }

    /**
     * Liefert eine IK zurueck.
     *
     * @param ik 11-stelliges Insituionskennzeichen
     * @return eine gueltige IK
     */
    public static IK of(int ik) {
        return new IK(ik);
    }

    /**
     * Liefert eine IK zurueck.
     *
     * @param ik 11-stelliges Insituionskennzeichen
     * @return eine gueltige IK
     */
    public static IK of(String ik) {
        return new IK(ik);
    }

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

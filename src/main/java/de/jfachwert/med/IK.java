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
import de.jfachwert.pruefung.Mod10Verfahren;

/**
 * Die Klasse IK repraesentiert das neunstellige Instituionskennzeichen, das
 * bundesweit eindeutig ist. Es wird fuer Abrechnungen im Bereich der deutschen
 * Sozialversicherung verwendet.
 * <p>
 * Die IK selbst ist eine neunstellige Ziffernfolge, die wie folgt aufgabaut
 * ist:
 * <ul>
 *     <li>1+2: Kassifikation (beginnend bei 10)</li>
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

    /**
     * Erzeugt ein neues IK-Objekt.
     *
     * @param code Institutionskennzeichen (mit Pruefziffer), z.B. "260326822"
     */
    public IK(String code) {
        this(Integer.parseInt(code), MOD10);
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
        super(code, pzVerfahren);
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

}

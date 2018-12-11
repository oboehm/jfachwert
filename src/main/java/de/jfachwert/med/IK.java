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

/**
 * Die Klasse IK repraesentiert das neunstellige Instituionskennzeichen, das
 * bundesweit eindeutig ist. Es wird fuer Abrechnungen im Bereich der deutschen
 * Sozialversicherung verwendet.
 *
 * @author oboehm
 * @since 1.1 (10.12.2018)
 */
public class IK extends AbstractFachwert<Long> {

    /**
     * Erzeugt ein neues IK-Objekt.
     *
     * @param code Institutionskennzeichen (mit Pruefziffer), z.B. "260326822"
     */
    public IK(String code) {
        this(Long.parseLong(code));
    }

    /**
     * Erzeugt ein neues IK-Objekt.
     *
     * @param code Institutionskennzeichen (mit Pruefziffer), z.B. 260326822L
     */
    public IK(long code) {
        super(code);
    }

    /**
     * Liefert eine IK zurueck.
     *
     * @param ik 11-stelliges Insituionskennzeichen
     * @return eine gueltige IK
     */
    public static IK of(long ik) {
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

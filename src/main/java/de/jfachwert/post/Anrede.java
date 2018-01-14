/*
 * Copyright (c) 2017 by Oliver Boehm
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
 * (c)reated 28.08.2017 by oboehm (ob@oasd.de)
 */
package de.jfachwert.post;

import org.apache.commons.lang3.*;

/**
 * Anrede fuer "Herr/Frau". Da "Fraeulein" schon lange nicht mehr
 * zeitgemaess ist, taucht es in der Liste nicht auf.
 *
 * @deprecated wird es kuenftig unter de.fachwert.formular geben
 */
@Deprecated
public enum Anrede {

    /** Unbekannte Anrede. */
    UNBEKANNT,

    /** Maennliche Anrede. */
    HERR,

    /** Weibliche Anrede. */
    FRAU;

    /**
     * Als Ergebnis werden die einzelnen Elemente in normaler Schreibweise
     * ausgegeben und nicht in kompletter Grossschreibung.
     *
     * @return z.B. "Herr" oder "Frau"
     */
    @Override
    public String toString() {
        return StringUtils.capitalize(super.toString().toLowerCase());
    }

}

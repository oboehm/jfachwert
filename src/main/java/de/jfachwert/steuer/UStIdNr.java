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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express orimplied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * (c)reated 24.03.2017 by oboehm (ob@jfachwert.de)
 */
package de.jfachwert.steuer;

import de.jfachwert.AbstractFachwert;

/**
 * Die Umsatzsteuer-Identifikationsnummer (USt-IdNr)[1] ist eine eindeutige
 * Kennzeichnung eines Unternehmens innerhalb der Europaeischen Union im
 * umsatzsteuerlichen Sinne.
 *
 * @author oboehm
 * @since 0.1.0
 */
public class UStIdNr extends AbstractFachwert<String> {

    /**
     * Erzeugt eine Umsatzsteuer-IdNr. Die uebergebene Nummer besteht aus
     * einer 2-stelligen Laenderkennung, gefolgt von maximal alphanumerischen
     * Zeichen.
     *
     * @param nr, .B. "DE999999999"
     */
    public UStIdNr(String nr) {
        super(nr);
    }

}

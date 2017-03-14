package de.jfachwert;/*
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
 * (c)reated 14.03.2017 by oboehm (ob@jfachwert.de)
 */

/**
 * Die meisten Fachwerte sind nur ein ganz duenner Wrapper um ein Attribut vom
 * Typ 'String'. Fuer diese Fachwerte duerfte diese Implementierung ausreichen.
 *
 * @author oboehm
 * @since 14.03.2017
 * @since 0.0.2
 */
public class AbstractFachwert implements Fachwert {

    private final String code;

    protected AbstractFachwert(String code) {
        this.code = code;
    }

    /**
     * Liefert die interne Praesentation fuer die agbgeleiteten Klassen. Er
     * ist nicht fuer den direkten Aufruf vorgesehen, weswegen die Methode auch
     * 'final' ist.
     *
     * @return die interne Repraesentation
     */
    protected final String getCode() {
        return this.getCode();
    }

    /**
     * Fuer die meisten Fachwerte reicht es, einfach den internen Code als
     * String auszugeben.
     *
     * @return den internen code
     */
    @Override
    public String toString() {
        return code;
    }

}

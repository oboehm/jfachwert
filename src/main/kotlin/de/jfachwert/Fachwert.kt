/*
 * Copyright (c) 2017-2020 by Oliver Boehm
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
 * (c)reated 11.03.17 by oliver (ob@oasd.de)
 */
package de.jfachwert

import java.io.Serializable
import javax.annotation.concurrent.Immutable

/**
 * In diesem Interface fuer Fachwerte sind alle Eigenschaften zusammengefasst,
 * die sich in Form eines Interfaces ausdruecken lassen. Fachwerte sind:
 *
 *  * unveraenderlich (Immutable),
 *  * serialisierbar,
 *  * ...
 *
 * @author ob@aosd.de
 */
@Immutable
interface Fachwert : Serializable {

    /**
     * Liefert die einzelnen Attribute eines Fachwertes als Map. Diese Methode
     * wird fuer die Default-Serialisierung nach JSON benoetigt.
     *
     * @return Attribute als Map
     */
    fun toMap(): Map<String, Any> {
        throw UnsupportedOperationException("not yet implemented")
    }

}
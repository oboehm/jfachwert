/*
 * Copyright (c) 2017-2021 by Oliver Boehm
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
package de.jfachwert;

import javax.annotation.concurrent.Immutable;
import java.io.Serializable;
import java.util.Map;

/**
 * In diesem Interface fuer Fachwerte sind alle Eigenschaften zusammengefasst,
 * die sich in Form eines Interfaces ausdruecken lassen. Fachwerte sind:
 * <ul>
 *     <li>unveraenderlich (Immutable),</li>
 *     <li>serialisierbar,</li>
 *     <li>...</li>
 * </ul>
 * <p>
 * Mit v4 wurde dieses Interface anfangs ebenfalls nach Kotlin ueberfuehrt.
 * Allerdings hat Kotlin bei Interfaces mit Default-Implementierung noch
 * Kompatibiltaetsprobleme (https://youtrack.jetbrains.com/issue/KT-4779).
 * Daher wurde die urspruengliche Implementierung wieder restauriert und die
 * Kotlin-Variante nach KFachwert verschoben
 * </p>
 *
 * @author <a href="ob@aosd.de">oliver</a>
 */
@Immutable
public interface Fachwert extends Serializable {

    /**
     * Liefert die einzelnen Attribute eines Fachwertes als Map.
     * 
     * @return Attribute als Map
     */
    default Map<String, Object> toMap() {
        throw new UnsupportedOperationException("not yet implemented");
    }
    
}

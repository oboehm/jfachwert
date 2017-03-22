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
 * (c)reated 22.03.2017 by oboehm (ob@jfachwert.de)
 */

/**
 * In diesem Package sind die verschiedenen Pruefziffernverfahren versammelt,
 * die das Interface {@link de.jfachwert.PruefzifferVerfahren} implementieren.
 * Meist basieren diese Pruefziffernverfahren auf dem Modulo-Operator,
 * dementsprechend heissen die Klassen dazu z.B.
 * {@link de.jfachwert.pruefung.Mod11Verfahren}.
 * <p>
 * Anmerkung: da Modulo11 leicht mit Modul011 verwechselt werden kann, wurde
 * statt "Modulo" nur "Mod" als Prefix genommen.
 * </p>
 *
 * @author oboehm
 * @since 0.1.0
 */
package de.jfachwert.pruefung;

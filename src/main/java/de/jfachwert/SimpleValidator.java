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
 * (c)reated 20.08.17 by oliver (ob@oasd.de)
 */
package de.jfachwert;

/**
 * Um die verschiedenen Validatoren als allgemeines Attribut verwendenen
 * zu koennen, sind die Gemeinsamkeiten in diesem Interface zusammengefasst.
 *
 * Damit das Interface nicht mit dem Validator-Interface aus javax.validation
 * verwechselt wird, wurde es in SimpleValidator umbenannt.
 *
 * @since 0.4 (20.08.17)
 */
public interface SimpleValidator {

    /**
     * Wenn der uebergebene Wert gueltig ist, soll er unveraendert
     * zurueckgegeben werden, damit er anschliessend von der aufrufenden
     * Methode weiterverarbeitet werden kann. Ist der Wert nicht gueltig,
     * soll eine {@link javax.validation.ValidationException} geworfen
     * werden.
     *
     * @param value Wert, der validiert werden soll
     * @return Wert selber, wenn er gueltig ist
     */
    Object validate(Object value);

}

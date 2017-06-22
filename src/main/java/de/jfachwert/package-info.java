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
 * (c)reated 13.03.2017 by oboehm (ob@jfachwert.de)
 */

/**
 * Dies ist das oberste Package von jFachwert. Unterhalb diese Bereichs sind
 * die einzelne Fachwerte thematisch aufgeteilt.
 * <p>
 * Die meisten Implementierung verhindern, dass ungueltige Fachwerte angelegt
 * werden koennen, indem die Parameter im Konstruktor validiert werden. Dies
 * ist beim Aufruf des Konstruktors zu beachten.
 * Dazu besitzen die meisten {@link de.jfachwert.Fachwert}-Klassen eine
 * statische validate-Methode, um die Gueltigkeit der Konstruktor-Argumente
 * im Vorfeld ueberpruefen zu koennen.
 * </p>
 *
 * @author <a href="ob@aosd.de">oliver</a>
 * @since 0.0.1
 */
package de.jfachwert;

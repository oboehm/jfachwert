/*
 * Copyright (c) 2022 by Oli B.
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
 * (c)reated 14.10.22 by oboehm
 */
package de.jfachwert.pruefung.exception;

import java.util.logging.Logger;

/**
 * Die Klasse ValidationExceptionn wurde eingefuehrt, um die gleichnamige
 * Exception aus "javax.validation" abzuloesen. Dies geschah aus 2 Gruenden:
 * <ol>
 *     <li>
 *         Allgemein: Reduktion der Abhaengigkeiten
 *     </li>
 *     <li>
 *         Speziell: keine Abhaengigkeiten zu JavaEE bzw. Jakarta als
 *         Nachfolgeprojekt
 *     </li>
 * </ol>
 *
 * @author oboehm
 * @since 4.4 (14.10.22)
 */
public class ValidationException extends javax.validation.ValidationException {

    private static final Logger log = Logger.getLogger(ValidationException.class.getName());

    static {
        log.fine("Ab v5 wird javax.validation.ValidationException nicht mehr verwendet, sondern durch " +
                ValidationException.class + " ersetzt.");
    }

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

}

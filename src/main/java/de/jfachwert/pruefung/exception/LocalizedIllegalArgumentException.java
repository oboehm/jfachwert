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
 * (c)reated 21.02.2017 by oboehm (ob@oasd.de)
 */
package de.jfachwert.pruefung.exception;

import java.io.Serializable;

/**
 * Die InvalidValueException ist eine Exception fuer ungueltige Werte.
 *
 * @author oboehm
 * @since 0.2.0 (26.04.2017)
 */
@SuppressWarnings("squid:MaximumInheritanceDepth")
public class LocalizedIllegalArgumentException extends IllegalArgumentException implements LocalizedException {

    private final Throwable valueException;

    /**
     * Erzeugt eine {@link LocalizedIllegalArgumentException}.
     *
     * @param message Fehlermeldung
     */
    public LocalizedIllegalArgumentException(String message) {
        this(null, message);
    }

    /**
     * Erzeugt eine neue Exception fuer einen fehlerhaften Wert.
     *
     * @param value der fehlerhafte Wert
     * @param context Resource des fehlerhaften Wertes (z.B. "email_address")
     */
    public LocalizedIllegalArgumentException(Serializable value, String context) {
        this(value, context, new InvalidValueException(value, context));
    }

    /**
     * Erzeugt eine neue Exception fuer einen fehlerhaften Wert.
     *
     * @param value der fehlerhafte Wert
     * @param context Resource des fehlerhaften Wertes (z.B. "email_address")
     * @param cause Ursache
     */
    public LocalizedIllegalArgumentException(Serializable value, String context, Throwable cause) {
        this("invalid value for " + context.replace('_', ' ') + ": \"" + value + '"', cause);
    }

    /**
     * Erzeugt eine neue Exception fuer einen fehlerhaften Wert.
     *
     * @param cause eigentliche Ursache
     */
    public LocalizedIllegalArgumentException(Throwable cause) {
        this(cause.getMessage(), cause);
    }

    /**
     * Erzeugt eine neue Exception fuer einen fehlerhaften Wert.
     *
     * @param message Meldung
     * @param cause eigentliche Ursache
     */
    public LocalizedIllegalArgumentException(String message, Throwable cause) {
        super(message, cause);
        this.valueException = cause;
    }

    /**
     * Erzeugt eine lokalisiserte Fehlermeldung fuer diese Exception.
     *
     * @return lokalisierte Fehlermeldung
     */
    @Override
    public String getLocalizedMessage() {
        return valueException.getLocalizedMessage();
    }

}

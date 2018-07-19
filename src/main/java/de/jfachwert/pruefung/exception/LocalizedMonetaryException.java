/*
 * Copyright (c) 2018 by Oliver Boehm
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
 * (c)reated 19.07.2018 by oboehm (ob@oasd.de)
 */
package de.jfachwert.pruefung.exception;

import javax.money.MonetaryException;
import java.util.ResourceBundle;

/**
 * Im Gegensatz zur {@link MonetaryException} wurde hier
 * {@link MonetaryException#getLocalizedMessage()} ueberschrieben, um
 * eine lokalisierte Fehlermeldung zur Verfuegung stellen zu koennen.
 *
 * @author oboehm
 * @since 0.8 (19.07.2018)
 */
public class LocalizedMonetaryException extends MonetaryException {

    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("de.jfachwert.messages");
    //private final String key;

    public LocalizedMonetaryException(String message) {
        super(message);
    }

    public LocalizedMonetaryException(String message, Throwable cause) {
        super(message, cause);
    }
}

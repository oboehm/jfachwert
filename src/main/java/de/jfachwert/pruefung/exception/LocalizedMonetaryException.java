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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Im Gegensatz zur {@link MonetaryException} wurde hier
 * {@link MonetaryException#getLocalizedMessage()} ueberschrieben, um
 * eine lokalisierte Fehlermeldung zur Verfuegung stellen zu koennen.
 *
 * @author oboehm
 * @since 1.0 (19.07.2018)
 */
public class LocalizedMonetaryException extends MonetaryException implements LocalizedException {

    @SuppressWarnings("squid:S1948")
    private final List<Object> amounts = new ArrayList<>();

    /**
     * Diese Exception wird vervendet, wenn zwei Geldbetraege mit 
     * unterschiedlichen Waehrungen verglichen oder addiert werden.
     * 
     * @param message Meldung (z.B. "different currencies")
     * @param args die verschiedenen Geldbetraege oder Argumente
     */
    public LocalizedMonetaryException(String message, Object... args) {
        super(message);
        amounts.addAll(Arrays.asList(args));
    }

    /**
     * Diese Exception wird verwendet, wenn ein verwendeter Operator mit
     * einer Exception fehlschlaegt.
     *
     * @param message Meldung (z.B. "operator failed")
     * @param arg z.B. der Operator
     * @param cause die Ursache fuer die Exception
     */
    public LocalizedMonetaryException(String message, Object arg, Throwable cause) {
        super(message);
        amounts.add(arg);
    }

    /**
     * Returns the detail message string of this throwable.
     *
     * @return the detail message string of this {@code Throwable} instance
     * (which may be {@code null}).
     */
    @Override
    public String getMessage() {
        return super.getMessage() + ": " + amounts;
    }

    /**
     * Im Gegensatz {@code getMessage()} wird hier die Beschreibung auf deutsch
     * zurueckgegeben, wenn die Locale auf Deutsch steht.
     *
     * @return lokalisierte Beschreibung
     */
    @Override
    public String getLocalizedMessage() {
        return getLocalizedString(getMessageKey(super.getMessage())) + ": " + amounts;
    }

}

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

import javax.validation.ValidationException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Die Klasse InvalidLengthException ist fuer die Laengen-Validierung
 * von Argumenten vorgesehen. Sind diese zu kurz oder zu lang, sollte diese
 * Exception geworfen werden.
 *
 * @author oboehm
 * @since 0.2 (20.04.2017)
 */
@SuppressWarnings("squid:MaximumInheritanceDepth")
public class InvalidLengthException extends LocalizedValidationException {

    private final Serializable[] arguments;
    private final int min;
    private final int max;
    private final List<Integer> allowedLengths = new ArrayList<>();

    /**
     * Erzeugt eine {@link ValidationException} mit der Wertebereich-Verletzung
     * des uebergebenen Arguments. The errorCode and linkedException will
     * default to null.
     *
     * @param argument das fehlerhafte Argument
     * @param expected erwartete Laenge
     */
    public InvalidLengthException(String argument, int expected) {
        this(argument, Collections.singletonList(expected));
    }

    /**
     * Dieser Constructor kann bei Arrays mit falscher Groesse eingesetzt
     * werden.
     *
     * @param array fehlerhaftes Array
     * @param expected erwartete Array-Groesse
     */
    public InvalidLengthException(byte[] array, int expected) {
        super("array=" + Arrays.toString(array) + " has not length " + expected + " (but " + array.length + ")");
        this.min = expected;
        this.max = expected;
        this.arguments = new Serializable[array.length];
        for (int i = 0; i < array.length; i++) {
            this.arguments[i] = array[i];
        }
    }

    /**
     * Erzeugt eine {@link ValidationException} mit der Wertebereich-Verletzung
     * des uebergebenen Arguments. The errorCode and linkedException will
     * default to null.
     *
     * @param argument das fehlerhafte Argument
     * @param min      erwartete Mindest-Laenge
     * @param max      erwartete Maximal-Laenge
     */
    public InvalidLengthException(String argument, int min, int max) {
        super("'" + argument + "': length (" + argument.length() + ") is not between " + min + " and " + max);
        this.min = min;
        this.max = max;
        this.arguments = asArray(argument);
    }

    /**
     * Erzeugt eine {@link ValidationException} mit der Wertebereich-Verletzung
     * des uebergebenen Arguments. The errorCode and linkedException will
     * default to null.
     *
     * @param argument das fehlerhafte Argument
     * @param allowedLengths erlaubten Laengen
     */
    public InvalidLengthException(String argument, List<Integer> allowedLengths) {
        super("'" + argument + "': " + argument.length() + " is not in allowed lengths " + allowedLengths);
        this.min = 0;
        this.max = 0;
        this.arguments = asArray(argument);
        this.allowedLengths.addAll(allowedLengths);
    }

    private static Serializable[] asArray(String s) {
        Serializable[] a = new Serializable[1];
        a[0] = s;
        return a;
    }

    /**
     * Im Gegensatz {@code getMessage()} wird hier die Beschreibung auf deutsch
     * zurueckgegeben, wenn die Loacale auf Deutsch steht.
     *
     * @return lokalisierte Beschreibung
     */
    @Override
    public String getLocalizedMessage() {
        String arg = arguments[0].toString();
        if (this.allowedLengths.isEmpty()) {
            if (min < max) {
                return this.getLocalizedMessage("pruefung.illegallength.exception.message.range", arg, arg
                        .length(), min, max);
            } else {
                return this.getLocalizedMessage("pruefung.illegallength.exception.message.array",
                        Arrays.toString(arguments), arguments.length, min);
            }
        } else {
            return this.getLocalizedMessage("pruefung.illegallength.exception.message.values",
                        arg, arg.length(), allowedLengths);
        }
    }

}

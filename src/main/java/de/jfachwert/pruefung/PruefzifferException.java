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
package de.jfachwert.pruefung;

import de.jfachwert.PruefzifferVerfahren;

import javax.validation.ValidationException;
import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * Die PruefzifferException gibt neben dem Wert auch die fehlerhafte
 * Pruefziffer mit aus.
 *
 * @author oboehm
 * @since 0.1.0
 */
public class PruefzifferException extends ValidationException {

    private final Object wert;
    private final Object expected;
    private final Object pruefziffer;

    /**
     * Gibt neben dem Wert auch die erwartete Pruefziffer mit aus.
     *
     * @param wert fehlerhafter Wert
     * @param verfahren Verfahren zur Bestimmung der Pruefziffer
     */
    public <T> PruefzifferException(T wert, PruefzifferVerfahren<T> verfahren) {
        this(wert, verfahren.berechnePruefziffer(wert), verfahren.getPruefziffer(wert));
    }

    /**
     * Gibt neben dem Wert auch die erwartete Pruefziffer mit aus.
     *
     * @param wert        Wert
     * @param expected    erwartete Pruefziffer
     * @param pruefziffer tatsaechliche Pruefziffer
     */
    public <T> PruefzifferException(T wert, T expected, T pruefziffer) {
        super(wert + ": Pruefziffer=" + expected + " expected but got '" + pruefziffer + "'");
        this.wert = wert;
        this.expected = expected;
        this.pruefziffer = pruefziffer;
    }

    /**
     * Im Gegensatz {@code getMessage()} wird hier die Beschreibung auf deutsch
     * zurueckgegeben, wenn die Loacale auf Deutsch steht.
     *
     * @return lokalisierte Beschreibung
     */
    @Override
    public String getLocalizedMessage() {
        ResourceBundle bundle = ResourceBundle.getBundle("de.jfachwert.messages");
        return MessageFormat.format(bundle.getString("pruefung.pruefziffer.exception.message"), wert, expected,
                pruefziffer);
    }

}
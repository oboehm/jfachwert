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
 * (c)reated 05.10.17 by oliver (ob@oasd.de)
 */
package de.jfachwert.formular;

/**
 * Die Werte fuer das Geschlecht orientieren sich am Handbuch des
 * Gesamtverbands der Deutschen Versicherungswirtschaft (GDV). Auch
 * Reihenfolge entspricht diesem Hanbuch. Zusaetzlich wurde noch
 * {@link Geschlecht#UNBEKANNT} als Wert mit aufgenommen.
 *
 * @since 0.5
 */
public enum Geschlecht {

    /** Juristische Person (meist Firmen). */
    JURISTISCHE_PERSON("juristische Person"),

    /** Maennliches Geschlecht. */
    MAENNLICH("m\u00e4nnlich"),

    /** Weibliches Geschlecht. */
    WEIBLICH("weiblich"),

    /** Unbekanntes Geschlecht. */
    UNBEKANNT("?");

    private final String text;

    Geschlecht(String text) {
        this.text = text;
    }

    /**
     * Als Ergebnis werden die einzelnen Elemente in normaler Schreibweise
     * ausgegeben und nicht in kompletter Grossschreibung.
     *
     * @return z.B. "weiblich"
     */
    @Override
    public String toString() {
        return text;
    }

}

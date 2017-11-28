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
 * (c)reated 28.11.2017 by oboehm (ob@oasd.de)
 */
package de.jfachwert.formular;

/**
 * Der Familienstand gehoert in Deutschland zu den Personenstandsdaten einer
 * Person und gibt an, ob diese ledig, verheiratet, geschieden oder verwitwet
 * ist. Der Familienstand wird im Melderegister gespeichert und dabei wie folgt
 * verschluesselt:
 * <ul>
 *     <li> LD (ledig) </li>
 *     <li> VH (verheiratet) </li>
 *     <li> VW (verwitwet) </li>
 *     <li> GS (geschieden) </li>
 *     <li> EA (Ehe aufgehoben) </li>
 *     <li> LP (in eingetragener Lebenspartnerschaft) </li>
 *     <li> LV (durch Tod aufgelöste Lebenspartnerschaft) </li>
 *     <li> LA (aufgehobene Lebenspartnerschaft) </li>
 *     <li> LE (durch Todeserklärung aufgelöste Lebenspartnerschaft) </li>
 *     <li> NB (nicht bekannt) </li>
 * </ul>
 * <p>
 * aus: Koordinierungsstelle für IT-Standards (Hrsg.): Datensatz für das
 * Meldewesen. Einheitlicher Bundes-/Laenderteil (DSMeld),
 * Stand: 1. Mai 2015, Blatt 1401
 * </p>
 *
 * @since 0.5
 */
public enum Familienstand {

    /** Ledig. */
    LEDIG("LD", "ledig"),

    /** Verheiratet. */
    VERWITWET("VW", "verwitwet"),

    /** Geschieden - der Zustand nach verheiratet. */
    GESCHIEDEN("GS", "geschieden"),

    /** Ehe aufgehoben. */
    EHE_AUFGEHOBEN("EA", "Ehe aufgehoben"),

    /** In eingetragener Lebenspartnerschaft. */
    IN_EINGETRAGENER_LEBENSPARTNERSCHAFT("LP", "in eingetragener Lebenspartnerschaft"),

    /** Lebenspartnerschaft, die durch den Tod beendet wurde. */
    DURCH_TOD_AUFGELOESTE_LEBENSPARTNERSCHAFT("LV", "durch Tod aufgelöste Lebenspartnerschaft"),

    /** Aufgehobene Lebenspartnerschaft. */
    AUFGEHOBENE_LEBENSPARTNERSCHAFT("LA", "aufgehobene Lebenspartnerschaft"),

    /** Lebenspartnerschaft, die durch eine Todeserklaerung aufgeloest wurde. */
    DURCH_TODESERKLAERUNG_AUFGELOESTE_LEBENSPARTNERSCHAFT("LE", "durch Todeserklärung aufgelöste Lebenspartnerschaft"),

    /** Unbekannter Familienstand. */
    NICHT_BEKANNT("NB", "nicht bekannt");

    private final String schluessel;
    private final String text;

    Familienstand(String schluessel, String text) {
        this.schluessel = schluessel;
        this.text = text;
    }

    /**
     * Liefert den zweistelligen Schluessel gemaess Datensatz des Meldewesens.
     *
     * @return z.B. "VW" fuer "verwittwet"
     */
    public String getSchluessel() {
        return schluessel;
    }

    /**
     * Als Ergebnis werden die einzelnen Elemente in normaler Schreibweise
     * ausgegeben und nicht in kompletter Grossschreibung.
     *
     * @return z.B. "ledig"
     */
    @Override
    public String toString() {
        return text;
    }

}

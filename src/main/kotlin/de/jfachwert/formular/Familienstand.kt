/*
 * Copyright (c) 2017-2020 by Oliver Boehm
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
package de.jfachwert.formular

import org.apache.commons.lang3.StringUtils

/**
 * Der Familienstand gehoert in Deutschland zu den Personenstandsdaten einer
 * Person und gibt an, ob diese ledig, verheiratet, geschieden oder verwitwet
 * ist. Der Familienstand wird im Melderegister gespeichert und dabei wie folgt
 * verschluesselt:
 *
 *  *  LD (ledig)
 *  *  VH (verheiratet)
 *  *  VW (verwitwet)
 *  *  GS (geschieden)
 *  *  EA (Ehe aufgehoben)
 *  *  LP (in eingetragener Lebenspartnerschaft)
 *  *  LV (durch Tod aufgeloeste Lebenspartnerschaft)
 *  *  LA (aufgehobene Lebenspartnerschaft)
 *  *  LE (durch Todeserklärung aufgelöste Lebenspartnerschaft)
 *  *  NB (nicht bekannt)
 *
 * aus: Koordinierungsstelle für IT-Standards (Hrsg.): Datensatz für das
 * Meldewesen. Einheitlicher Bundes-/Laenderteil (DSMeld),
 * Stand: 1. Mai 2015, Blatt 1401
 *
 * @since 2.0
 */
enum class Familienstand(val schluessel: String, private val text: String) {

    /** Ledig.  */
    LEDIG("LD", "ledig"),

    /** Verheiratet.  */
    VERHEIRATET("VH", "verheiratet"),

    /** Verheiratet.  */
    VERWITWET("VW", "verwitwet"),

    /** Geschieden - der Zustand nach verheiratet.  */
    GESCHIEDEN("GS", "geschieden"),

    /** Ehe aufgehoben.  */
    EHE_AUFGEHOBEN("EA", "Ehe aufgehoben"),

    /** In eingetragener Lebenspartnerschaft.  */
    EINGETRAGENE_LEBENSPARTNERSCHAFT("LP", "eingetragene Lebenspartnerschaft"),

    /** Eheaehnliche Gemeinschaft.  */
    EHEAEHNLICHE_GEMEINSCHAFT("EG", "ehe\u00e4hnliche Gemeinschaft"),

    /** Lebenspartnerschaft, die durch den Tod beendet wurde.  */
    DURCH_TOD_AUFGELOESTE_LEBENSPARTNERSCHAFT("LV", "durch Tod aufgel\u00f6ste Lebenspartnerschaft"),

    /** Aufgehobene Lebenspartnerschaft.  */
    AUFGEHOBENE_LEBENSPARTNERSCHAFT("LA", "getrennt lebend"),

    /** Lebenspartnerschaft, die durch eine Todeserklaerung aufgeloest wurde.  */
    DURCH_TODESERKLAERUNG_AUFGELOESTE_LEBENSPARTNERSCHAFT("LE", "durch Todeserklärung aufgelöste Lebenspartnerschaft"),

    /** Unbekannter Familienstand.  */
    NICHT_BEKANNT("NB", "nicht bekannt");

    /**
     * Als Ergebnis werden die einzelnen Elemente in normaler Schreibweise
     * ausgegeben und nicht in kompletter Grossschreibung.
     *
     * @return z.B. "ledig"
     */
    override fun toString(): String {
        return text
    }



    companion object {

        /**
         * Liefert zu einem Schluessel den entsprechende Familienstand. Falls
         * nichts gefunden wird, wird NICHT_BEKANNT zurueckgeliefert.
         *
         * @param schluessel z.B. "LE"
         * @return Familienstand, z.B. LEDIG
         */
        @JvmStatic
        fun of(schluessel: String): Familienstand {
            val normalized = StringUtils.trimToEmpty(schluessel)
            return if (normalized.length == 2) {
                findSchluessel(normalized)
            } else {
                findText(normalized)
            }
        }

        private fun findSchluessel(schluessel: String): Familienstand {
            for (familienstand in values()) {
                if (familienstand.schluessel.equals(schluessel, ignoreCase = true)) {
                    return familienstand
                }
            }
            return NICHT_BEKANNT
        }

        private fun findText(text: String): Familienstand {
            for (familienstand in values()) {
                if (familienstand.text.equals(text, ignoreCase = true)) {
                    return familienstand
                }
            }
            return NICHT_BEKANNT
        }

    }

}
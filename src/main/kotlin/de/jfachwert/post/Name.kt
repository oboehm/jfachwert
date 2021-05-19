/*
 * Copyright (c) 2019, 2020 by Oliver Boehm
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
 * (c)reated 19.02.2019 by oboehm (ob@oasd.de)
 */
package de.jfachwert.post

import de.jfachwert.KSimpleValidator
import de.jfachwert.Text
import de.jfachwert.pruefung.LengthValidator
import de.jfachwert.pruefung.NullValidator
import org.apache.commons.lang3.StringUtils
import java.util.*

/**
 * Die Klasse Name steht fuer einen normalen Namen mit Vorname und Nachnamen,
 * kann aber auch fuer Firmennamen genutzt werden.
 *
 * @author oboehm
 * @since 2.1 (19.02.2019)
 */
open class Name
/**
 * Erzeugt einen Namen. Erwartet wird ein einzelner Name, oder
 * "Nachname, Vorname".
 *
 * @param name,     z.B. "Duck, Donald"
 * @param validator Validator fuer die Ueberpruefung
 */
@JvmOverloads constructor(name: String, validator: KSimpleValidator<String> = VALIDATOR) : Text(name, validator) {

    /**
     * Liefert den Nachnamen.
     *
     * @return z.B. "Duck"
     */
    val nachname: String
        get() {
            var nachname = code
            nachname = if (nachname.contains(",")) {
                StringUtils.substringBefore(code, ",").trim { it <= ' ' }
            } else {
                val namensliste = namensListe
                namensliste[namensliste.size - 1]
            }
            return nachname
        }

    /**
     * Liefert den oder die Vornamen als ein String.
     *
     * @return z.B. "Donald"
     */
    open val vorname: String
        get() {
            var vorname = code
            vorname = if (vorname.contains(",")) {
                StringUtils.substringAfter(code, ",").trim { it <= ' ' }
            } else {
                namensListe[0]
            }
            return vorname
        }

    /**
     * Liste die einzelnen Vornamen und Namen als Liste auf.
     *
     * @return Liste mit Namen (mit mind. 1 Namen)
     */
    val namensListe: List<String>
        get() {
            val namen = code.replace(".", ". ")
            return Arrays.asList(*namen.split("[\\s,]".toRegex()).toTypedArray())
        }

    /**
     * Falls mehr als ein Vornamen exisitiert, kann dies hierueber als Liste
     * von Vornamen angefragt werden.
     *
     * @return Liste mit Vornamen (kann auch leer sein)
     */
    val vornamenListe: List<String>
        get() = if (hasVorname()) {
            Arrays.asList(*vorname.split("\\s".toRegex()).toTypedArray())
        } else {
            ArrayList()
        }

    /**
     * Liefert 'true' zurueck, falls ein Vorname im abgespeicherten Namen
     * enthalten ist.
     *
     * @return true, false
     */
    fun hasVorname(): Boolean {
        return code.contains(",")
    }

    override fun hashCode(): Int {
        return replaceUmlaute(nachname).toLowerCase().hashCode()
    }

    /**
     * Hier werden Namen verglichen. Aktuell werden sie semantisch verglichen,
     * aber darauf sollte man sich nicht verlassen. Man sollte entweder
     * [.equalsSemantic] fuer den semantischen Vergleich und
     * [.equalsExact] fuer den exakten Vergleich verwenden, da
     * es sein kann, dass diese Implementierung kuenftig auf den exakten
     * Vergleich aufbaut.
     *
     * @param other zu vergleichender Name
     * @return true bei Gleichheit
     * @see Object.equals
     */
    override fun equals(other: Any?): Boolean {
        if (other !is Name || !this.javaClass.isAssignableFrom(other.javaClass)) {
            return false
        }
        return equalsSemantic(other)
    }

    /**
     * Hier werden Namen logisch (semantisch) verglichen. So werden Namen auch
     * dann als gleich angesehen, wenn sie mit oder ohne Umlaute geschrieben
     * werden.
     *
     * Braucht man einen noch "toleranteren" Vergleich, kann man auch auf
     * die [.getDistanz]-Methode aus der Oberklasse zurueckgreifen
     * und anhand der Distanz entscheiden, ob zwei Namen noch als gleich
     * angesehen werden sollen.
     *
     * @param other der zu vergleichende Name
     * @return true bei Gleichheit
     * @see Object.equals
     * @since 3.0
     */
    fun equalsSemantic(other: Name): Boolean {
        return isEquals(normalize(this), normalize(other))
    }

    /**
     * Im Gegensatz zur [.equals]-Methode muss hier der andere
     * Name exakt einstimmen, also auch in Gross- und Kleinschreibung.
     *
     * @param other der andere Name
     * @return true oder false
     * @since 2.1
     */
    fun equalsExact(other: Name?): Boolean {
        return super.equals(other)
    }



    companion object {

        private val WEAK_CACHE = WeakHashMap<String, Name>()
        private val VALIDATOR: KSimpleValidator<String> = LengthValidator.NOT_EMPTY_VALIDATOR

        /** Null-Wert fuer Initialisierung.  */
        val NULL = Name("", NullValidator())

        /**
         * Erzeugt einen neuen Namen, falls er noch nicht existiert. Falls er
         * bereits existiert, wird dieser zurueckgegeben, um Duplikate zu
         * vermeiden.
         *
         * @param name, z.B. "Duck, Donald"
         * @return Name
         */
        @JvmStatic
        fun of(name: String): Name {
            return WEAK_CACHE.computeIfAbsent(name) { s: String -> Name(s) }
        }

        private fun normalize(name: Name): Name {
            return of(name.replaceUmlaute().toString().replace("-", " ").trim { it <= ' ' })
        }

        private fun isEquals(a: Name, b: Name): Boolean {
            return a.nachname.equals(b.nachname, ignoreCase = true) &&
                    (shortenVorname(a).equals(shortenVorname(b), ignoreCase = true) ||
                            equalsVornamen(a.vornamenListe, b.vornamenListe))
        }

        private fun shortenVorname(x: Name): String {
            return StringUtils.deleteWhitespace(x.vorname.replace("-", ""))
        }

        private fun equalsVornamen(a: List<String>, b: List<String>): Boolean {
            val n = Math.min(a.size, b.size)
            for (i in 0 until n) {
                if (!a[i].equals(b[i], ignoreCase = true)) {
                    return false
                }
            }
            return true
        }
    }

}
/*
 * Copyright (c) 2023-2024 by Oli B.
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
 * (c)reated 27.12.23 by oboehm
 */
package de.jfachwert.med

import de.jfachwert.AbstractFachwert
import de.jfachwert.KSimpleValidator
import de.jfachwert.pruefung.LengthValidator
import de.jfachwert.pruefung.NullValidator
import java.util.*

/**
 * SNOMED (Systematized Nomenclature of Medicine) ist eine Familie
 * medizinischer Terminologiesysteme. Urspruenglich als Nomenklatur konzipiert
 * ist die neueste – und derzeit einzige weiter gepflegte – Version SNOMED CT
 * (Clinical Technology) die Grundlage dieser Implementierung.
 *
 * Zum Nachschlagen eines SNOMED-Werts kann https://browser.ihtsdotools.org/
 * verwendet werden. So liefert
 * - https://browser.ihtsdotools.org/?perspective=full&conceptId1=763158003
 * "Medicinal product" als Wert fuer den Code "763158003".
 *
 * @author oboehm
 * @since 5.1 (27.12.23)
 */
open class SNOMED
/**
 * Dieser Konstruktor ist hauptsaechlich fuer abgeleitete Klassen gedacht,
 * damit diese den Validator ueberschreiben koennen.
 * Man kann es auch verwenden, um den Validator abzuschalten,
 * indem man das [de.jfachwert.pruefung.NoopVerfahren] verwendet.
 *
 * @param code      der SNOMDE-Code
 * @param validator der verwendete Validator (optional)
 */
@JvmOverloads constructor(code: String, display: String = "", validator: KSimpleValidator<String> = VALIDATOR) : AbstractFachwert<String, SNOMED>(code, validator) {

    constructor(code: String, validator: KSimpleValidator<String>) : this(code, "", validator)

    /**
     * Diese Methode liefert immer 'true' zurueck. Es sei denn, nan hat den
     * Default-Validator beim Anlegen deaktiviert.
     *
     * @return true oder false
     */
    override fun isValid(): Boolean {
        return VALIDATOR.isValid(code)
    }

    val display: String = display
        get(): String {
            return if (field.isEmpty()) this.code else field
        }

    companion object {
        private val WEAK_CACHE = WeakHashMap<String, SNOMED>()
        private val VALIDATOR: KSimpleValidator<String> = LengthValidator(1)
        /** Null-Konstante.  */
        @JvmField
        val NULL = SNOMED("", NullValidator())

        /**
         * Liefert eine SNOMED-Instanz.
         *
         * @param code SNOMED-Code
         * @return SNOMED
         */
        @JvmStatic
        fun of(code: String): SNOMED {
            val copy = String(code.toCharArray())
            return WEAK_CACHE.computeIfAbsent(copy) { c: String -> SNOMED(String(c.toCharArray())) }
        }

        @JvmStatic
        fun of(code: String, display: String): SNOMED {
            var s = of(code)
            if (!display.equals(s.display)) {
                s = SNOMED(String(code.toCharArray()), display)
                WEAK_CACHE.put(code, s)
            }
            return s
        }
    }

}

/*
 * Copyright (c) 2023 by Oli B.
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
import de.jfachwert.pruefung.NullValidator
import java.util.*

/**
 * SNOMED (Systematized Nomenclature of Medicine) ist eine Familie
 * medizinischer Terminologiesysteme. Urspruenglich als Nomenklatur konzipiert
 * ist die neueste – und derzeit einzige weiter gepflegte – Version SNOMED CT
 * (Clinical Technology) die Grundlage dieser Implementierung ist.
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

    val display: String = display
        get(): String {
            return if (field.isEmpty()) this.code else field
        }

    companion object {
        private val WEAK_CACHE = WeakHashMap<String, SNOMED>()
        private val VALIDATOR: KSimpleValidator<String> = NullValidator()
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
            return WEAK_CACHE.computeIfAbsent(code) { c: String -> SNOMED(c) }
        }

        @JvmStatic
        fun of(code: String, display: String): SNOMED {
            return WEAK_CACHE.computeIfAbsent(code) { c: String -> SNOMED(c, display) }
        }
    }

}

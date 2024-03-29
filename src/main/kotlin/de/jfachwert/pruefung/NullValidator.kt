/*
 * Copyright (c) 2017-2022 by Oliver Boehm
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
 * (c)reated 20.08.17 by oliver (ob@oasd.de)
 */
package de.jfachwert.pruefung

import de.jfachwert.KSimpleValidator
import java.io.Serializable

/**
 * Der NullValidator verhindert, dass 'null' als valider Wert durchgereicht
 * wird. Er kann immer dann eingesetzt werden, wenn kein anderer SimpleValidator
 * passt. Er uebernimmt damit quasi die Rolle eines DummyValidators.
 *
 * @since 0.4
 */
open class NullValidator<T : Serializable> : KSimpleValidator<T> {

    /**
     * Wenn der uebergebene Wert nicht null ist, wird er unveraendert
     * zurueckgegeben. Ansonsten wird eine
     * [ValidationException] geworfen.
     *
     * @param value Wert, der validiert werden soll
     * @return Wert selber, wenn er nicht null ist
     */
    override fun validate(value: T): T {
        return value
    }

}
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
 * (c)reated 10.07.23 by oboehm
 */
package de.jfachwert.zeit

import de.jfachwert.KFachwert
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Die Zeitdauer repraesentiert den Zeitraum zwischen zwei Zeitpunkten.
 * Fehlt der zweite Zeitpunkt, repraesentiert diese Klasse den Zeitraum
 * zwischen Start und aktuellem Zeitpunkt.
 *
 * @author oboehm
 * @since 5.0 (10.07.2023)
 */
open class Zeitdauer(val code: Int, val unit: TimeUnit) : KFachwert {

    override fun toString(): String {
        return "$code " + BUNDLE.getString(unit.toString())
    }

    companion object {
        @JvmField
        val BUNDLE = ResourceBundle.getBundle("de.jfachwert.messages")
    }

}
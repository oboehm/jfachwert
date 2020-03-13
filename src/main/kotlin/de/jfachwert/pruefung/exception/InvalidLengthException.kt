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
 * (c)reated 21.02.2017 by oboehm (ob@oasd.de)
 */
package de.jfachwert.pruefung.exception

import java.io.Serializable
import java.util.*

/**
 * Die Klasse InvalidLengthException ist fuer die Laengen-Validierung
 * von Argumenten vorgesehen. Sind diese zu kurz oder zu lang, sollte diese
 * Exception geworfen werden.
 *
 * @author oboehm
 * @since 0.2 (20.04.2017)
 */
class InvalidLengthException : LocalizedValidationException {

    private val arguments: Array<Serializable?>
    private val min: Int
    private val max: Int
    private val allowedLengths: MutableList<Int> = ArrayList()

    /**
     * Erzeugt eine [LocalizedValidationException] mit der Wertebereich-Verletzung
     * des uebergebenen Arguments.
     *
     * @param argument das fehlerhafte Argument
     * @param expected erwartete Laenge
     */
    constructor(argument: String, expected: Int) : this(argument, listOf<Int>(expected)) {}

    /**
     * Dieser Constructor kann bei Arrays mit falscher Groesse eingesetzt
     * werden.
     *
     * @param array fehlerhaftes Array
     * @param expected erwartete Array-Groesse
     */
    constructor(array: ByteArray, expected: Int) : super("array=" + Arrays.toString(array) + " has not length " + expected + " (but " + array.size + ")") {
        min = expected
        max = expected
        arguments = arrayOfNulls(array.size)
        for (i in array.indices) {
            arguments[i] = array[i]
        }
    }

    /**
     * Erzeugt eine [LocalizedValidationException] mit der Wertebereich-Verletzung
     * des uebergebenen Arguments.
     *
     * @param argument das fehlerhafte Argument
     * @param min      erwartete Mindest-Laenge
     * @param max      erwartete Maximal-Laenge
     */
    constructor(argument: String, min: Int, max: Int) : super("'" + argument + "': length (" + argument.length + ") is not between " + min + " and " + max) {
        this.min = min
        this.max = max
        arguments = asArray(argument)
    }

    /**
     * Erzeugt eine [LocalizedValidationException] mit der Wertebereich-Verletzung
     * des uebergebenen Arguments.
     *
     * @param argument das fehlerhafte Argument
     * @param allowedLengths erlaubten Laengen
     */
    constructor(argument: String, allowedLengths: List<Int>) : super("'" + argument + "': " + argument.length + " is not in allowed lengths " + allowedLengths) {
        min = 0
        max = 0
        arguments = asArray(argument)
        this.allowedLengths.addAll(allowedLengths)
    }

    /**
     * Im Gegensatz `getMessage()` wird hier die Beschreibung auf deutsch
     * zurueckgegeben, wenn die Loacale auf Deutsch steht.
     *
     * @return lokalisierte Beschreibung
     */
    override fun getLocalizedMessage(): String {
        val arg = arguments[0].toString()
        return if (allowedLengths.isEmpty()) {
            if (min < max) {
                this.getLocalizedMessage("pruefung.illegallength.exception.message.range", arg, arg
                        .length, min, max)
            } else {
                this.getLocalizedMessage("pruefung.illegallength.exception.message.array",
                        Arrays.toString(arguments), arguments.size, min)
            }
        } else {
            this.getLocalizedMessage("pruefung.illegallength.exception.message.values",
                    arg, arg.length, allowedLengths)
        }
    }



    companion object {

        private fun asArray(s: String): Array<Serializable?> {
            val a = arrayOfNulls<Serializable>(1)
            a[0] = s
            return a
        }

    }

}

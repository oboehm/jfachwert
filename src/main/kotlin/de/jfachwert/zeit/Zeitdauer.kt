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
import java.math.BigInteger
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Die Zeitdauer repraesentiert den Zeitraum zwischen zwei Zeitpunkten.
 * Fehlt der zweite Zeitpunkt, repraesentiert diese Klasse den Zeitraum
 * zwischen Start und aktuellem Zeitpunkt.
 *
 * Die Zeitdauer-Klasse ist angelehnt an die Duraation-Klasse aus dem
 * JDK, nur dass sie ueber den Default-Constructor auch zur Zeitmessung
 * verwendet werden kann: einfach anlegen und ausgeben - schon hat man
 * die aktuelle Zeitdauer seit dem Start (Aufruf des Default-Constructors).
 *
 * @author oboehm
 * @since 5.0 (10.07.2023)
 */
open class Zeitdauer(val startTime: Zeitpunkt, val endTime : Zeitpunkt? = null) : KFachwert {

    constructor() : this(Zeitpunkt()) {}

    constructor(code: Long, unit: TimeUnit) : this(Zeitpunkt.ZERO, Zeitpunkt(toNanoseconds(code, unit))) {}

    fun getZaehler() : BigInteger {
        val t = getTimeInNanos()
        return when(getEinheit()) {
            TimeUnit.NANOSECONDS -> t
            TimeUnit.MICROSECONDS -> t.divide(MICROSECOND_IN_NANOS)
            TimeUnit.MILLISECONDS -> t.divide(MILLISECOND_IN_NANOS)
            TimeUnit.SECONDS -> t.divide(SECOND_IN_NANOS)
            TimeUnit.MINUTES -> t.divide(MINUTE_IN_NANOS)
            TimeUnit.HOURS -> t.divide(HOUR_IN_NANOS)
            TimeUnit.DAYS -> t.divide(DAY_IN_NANOS)
        }
    }

    fun getEinheit() : TimeUnit {
        val t = getTimeInNanos()
        return if (t.compareTo(DAY_IN_NANOS.multiply(BigInteger.valueOf(4L))) > 0) {
            TimeUnit.DAYS
        } else if (t.compareTo(HOUR_IN_NANOS.multiply(BigInteger.valueOf(5L))) > 0) {
            TimeUnit.HOURS
        } else if (t.compareTo(MINUTE_IN_NANOS.multiply(BigInteger.valueOf(5L))) > 0) {
            TimeUnit.MINUTES
        } else if (t.compareTo(SECOND_IN_NANOS.multiply(BigInteger.valueOf(5L))) > 0) {
            TimeUnit.SECONDS
        } else if (t.compareTo(MILLISECOND_IN_NANOS.multiply(BigInteger.valueOf(5L))) > 0) {
            TimeUnit.MILLISECONDS
        } else if (t.compareTo(MICROSECOND_IN_NANOS.multiply(BigInteger.valueOf(5L))) > 0) {
            TimeUnit.MICROSECONDS
        } else {
            TimeUnit.NANOSECONDS
        }
    }

    fun getTimeInNanos() : BigInteger {
        return (endTime?:Zeitpunkt()).minus(startTime).getTimeInNanos()
    }

    override fun toString(): String {
        return "${getZaehler()} " + BUNDLE.getString(getEinheit().toString())
    }



    companion object {

        @JvmField
        val BUNDLE = ResourceBundle.getBundle("de.jfachwert.messages")
        val MICROSECOND_IN_NANOS = BigInteger.valueOf(1_000L)
        val MILLISECOND_IN_NANOS = BigInteger.valueOf(1_000_000L)
        val SECOND_IN_NANOS = BigInteger.valueOf(1_000_000_000L)
        val MINUTE_IN_NANOS = BigInteger.valueOf(60_000_000_000L)
        val HOUR_IN_NANOS = BigInteger.valueOf(3_600_000_000_000L)
        val DAY_IN_NANOS = BigInteger.valueOf(86_400_000_000_000L)

        private fun toNanoseconds(code: Long, unit: TimeUnit): BigInteger {
            return when(unit) {
                TimeUnit.NANOSECONDS -> BigInteger.valueOf(code)
                TimeUnit.MICROSECONDS -> BigInteger.valueOf(code).multiply(MICROSECOND_IN_NANOS)
                TimeUnit.MILLISECONDS -> BigInteger.valueOf(code).multiply(MILLISECOND_IN_NANOS)
                TimeUnit.SECONDS -> BigInteger.valueOf(code).multiply(SECOND_IN_NANOS)
                TimeUnit.MINUTES -> BigInteger.valueOf(code).multiply(MINUTE_IN_NANOS)
                TimeUnit.HOURS -> BigInteger.valueOf(code).multiply(HOUR_IN_NANOS)
                TimeUnit.DAYS -> BigInteger.valueOf(code).multiply(DAY_IN_NANOS)
            }
        }

    }

}
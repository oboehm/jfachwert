/*
 * Copyright (c) 2018-2024 by Oliver Boehm
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
 * (c)reated 13.01.2018 by oboehm (ob@oasd.de)
 */
package de.jfachwert

import de.jfachwert.pruefung.exception.LocalizedValidationException
import de.jfachwert.pruefung.exception.ValidationException
import de.jfachwert.util.SmallUUID
import de.jfachwert.util.TinyUUID
import java.io.Serializable
import java.lang.reflect.InvocationTargetException
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger

/**
 * Ueber die FachwertFactory kann ein beliebiger Fachwert generiert oder geholt
 * werden. Normalerweise sollte man da den entsprechenden Konstruktor des
 * Fachwerts bemuehen, aber es gibt auch Situation, wo man den genauen Typ des
 * Fachwertes (noch) nicht weiss. Fuer diese Situation ist diese Factory
 * gedacht.
 *
 * Zum Generieren eines Fachwerts wird der aktuelle Classpath abgescannt, um
 * die passende Implementierung zu finden. Um die Implementierung erweitern
 * aoder austauschen zu koennen, hat diese Factory keine statischen Methoden,
 * sondern ist als normale Klasse implementiert.
 *
 *
 * Da in dieser Klasse Exceptions auftreten koennen, die nicht weitergegeben
 * werden, wird dazu der Standard-Logger aus dem JDK verwendet. Damit kann man
 * sich zum Debuggen diese Exceptions im Log-Level "FINE" ausgeben lassen.
 *
 * @author oboehm
 * @since 0.5 (13.01.2018)
 */
class FachwertFactory private constructor() {

    private val registeredClasses: MutableMap<String, Class<out KFachwert>> = HashMap()

    companion object {

        private val log = Logger.getLogger(KFachwert::class.java.name)

        /**
         * Die FachwertFactory ist als Singleton angelegt, um die Implementierung
         * durch Ableitung erweiern zu koennen. Mit dieser Methode wird die
         * einzige Instanz dieser Klasse zurueckgegeben.
         *
         * @return die einzige Instanz
         */
        @JvmStatic
        val instance = FachwertFactory()

        private fun getValidator(clazz: Class<out KFachwert>?): Optional<KSimpleValidator<*>> {
            try {
                val validatorField = clazz!!.getDeclaredField("VALIDATOR")
                validatorField.isAccessible = true
                val obj = validatorField[null]
                if (obj is KSimpleValidator<*>) {
                    return Optional.of(obj)
                }
            } catch (ex: ReflectiveOperationException) {
                log.log(Level.FINE, "Kann nicht auf den Validator in $clazz zugreifen.")
                log.log(Level.FINER, "Details:", ex)
            }
            return Optional.empty()
        }

        private fun callValidate(clazz: Class<out KFachwert>, args: Array<Serializable>) {
            try {
                val companion = getCompanionOf(clazz)
                callValidate(companion, args, companion.javaClass as Class<out KFachwert>)
            } catch (ex: ReflectiveOperationException) {
                log.log(Level.FINE, "Kann nicht Companion von $clazz verwenden.")
                log.log(Level.FINER, "Details:", ex)
                try {
                    callValidate(null, args, clazz)
                } catch (rex: ReflectiveOperationException) {
                    log.log(Level.FINE, "Kann die validate-Methode von $clazz nicht aufrufen.")
                    log.log(Level.FINER, "Details:", rex)
                }
            }
        }

        @Throws(ReflectiveOperationException::class)
        private fun callValidate(obj: Any?, args: Array<Serializable>, clazz: Class<out KFachwert>) {
            try {
                val argTypes = toTypes(args)
                val method = clazz.getMethod("validate", *argTypes)
                method.invoke(obj, *args)
            } catch (ex: InvocationTargetException) {
                log.log(Level.FINE, "Aufruf der validate-Methode von $clazz funktioniert nicht.")
                if (ex.targetException is ValidationException) {
                    throw (ex.targetException as ValidationException)
                } else {
                    log.log(Level.FINER, "Details:", ex)
                }
            }
        }

        @Throws(ReflectiveOperationException::class)
        private fun getCompanionOf(clazz: Class<out KFachwert>): Any {
            val companionField = clazz.getField("Companion")
            return companionField[null]
        }

        private fun toTypes(args: Array<out Serializable>): Array<Class<*>?> {
            val argTypes: Array<Class<*>?> = arrayOfNulls(args.size)
            for (i in args.indices) {
                argTypes[i] = args[i].javaClass
            }
            return argTypes
        }

        private fun distance(a: String, b: String): Int {
            return Text(a).getDistanz(b)
        }

        // Die Registrierung hier ist unschoen, weil dazu die FachwertFactory alle
        // Fachwert-Klassen kennen muss. Schoener waere es, wenn sich die einzelnen
        // Klassen selber registrieren wuerden. Das Problem dabei ist, dass sie es
        // erst machen koennen, wenn sie vom Classloader geladen wurde (Henne-Ei-
        // Problem).
        init {
            instance.register(Text::class.java)
            instance.register(TinyUUID::class.java)
            instance.register(SmallUUID::class.java)
            // bank-Module
            instance.register("de.jfachwert.bank.Bankverbindung")
            instance.register("de.jfachwert.bank.BIC")
            instance.register("de.jfachwert.bank.BLZ")
            instance.register("de.jfachwert.bank.IBAN")
            instance.register("de.jfachwert.bank.Kontonummer")
            instance.register("de.jfachwert.bank.Zinssatz")
            // formular-Module
            instance.register("de.jfachwert.formular.Anrede")
            instance.register("de.jfachwert.formular.Familienstand")
            instance.register("de.jfachwert.formular.Geschlecht")
            // math-Modul
            instance.register("de.jfachwert.math.Bruch")
            instance.register("de.jfachwert.math.Nummer")
            instance.register("de.jfachwert.math.PackedDecimal")
            instance.register("de.jfachwert.math.Prozent")
            instance.register("de.jfachwert.math.Promille")
            // money-Modul
            instance.register("de.jfachwert.money.Geldbetrag")
            instance.register("de.jfachwert.money.Waehrung")
            // med-Modul
            instance.register("de.jfachwert.med.BSNR")
            instance.register("de.jfachwert.med.Hilfsmittelnummer")
            instance.register("de.jfachwert.med.IK")
            instance.register("de.jfachwert.med.LANR")
            instance.register("de.jfachwert.med.LEGS")
            instance.register("de.jfachwert.med.PZN")
            instance.register("de.jfachwert.med.SNOMED")
            instance.register("de.jfachwert.med.Versichertennummer")
            instance.register("de.jfachwert.med.ZANR")
            // net-Modul
            instance.register("de.jfachwert.net.ChatAccount")
            instance.register("de.jfachwert.net.Domainname")
            instance.register("de.jfachwert.net.EMailAdresse")
            instance.register("de.jfachwert.net.Telefonnummer")
            // steuer-Modul
            instance.register("de.jfachwert.steuer.Mehrwertsteuer")
            instance.register("de.jfachwert.steuer.SteuerIdNr")
            instance.register("de.jfachwert.steuer.Steuernummer")
            instance.register("de.jfachwert.steuer.UStIdNr")
            // post-Modul
            instance.register("de.jfachwert.post.Adressat")
            instance.register("de.jfachwert.post.Adresse")
            instance.register("de.jfachwert.post.Anschrift")
            instance.register("de.jfachwert.post.Name")
            instance.register("de.jfachwert.post.Ort")
            instance.register("de.jfachwert.post.PLZ")
            instance.register("de.jfachwert.post.Postfach")
            // rechnung-Modul
            instance.register("de.jfachwert.rechnung.Artikelnummer")
            instance.register("de.jfachwert.rechnung.Bestellnummer")
            instance.register("de.jfachwert.rechnung.Kundennummer")
            instance.register("de.jfachwert.rechnung.Rechnungsmonat")
            instance.register("de.jfachwert.rechnung.Rechnungsnummer")
            instance.register("de.jfachwert.rechnung.Referenznummer")
            // zeit-Modul
            instance.register("de.jfachwert.zeit.Zeitdauer")
            instance.register("de.jfachwert.zeit.Zeitpunkt")
            instance.register("de.jfachwert.zeit.Zeitraum")
            instance.register("de.jfachwert.zeit.Zeiteinheit")
        }
    }

    /**
     * Hierueber sollten sich die einzelnen Fachwert-Klassen registrieren.
     * Ansonsten werden sie bei [FachwertFactory.getFachwert]
     * nicht gefunden.
     *
     * @param fachwertClass Fachwert-Klasse
     */
    @Synchronized
    fun register(fachwertClass: Class<out KFachwert>) {
        try {
            registeredClasses[fachwertClass.simpleName] = fachwertClass
        } catch (ex: NoClassDefFoundError) {
            logIgnoredRegistration(fachwertClass, ex)
        }
    }

    fun register(classname: String) {
        try {
            val fachwertClass = Class.forName(classname) as Class<out KFachwert>
            register(fachwertClass)
        } catch (ex: ClassNotFoundException) {
            logIgnoredRegistration(classname, ex)
        } catch (ex: NoClassDefFoundError) {
            // kann auch vorkommen, wenn Abhaengigkeit zu javax.money:money-api fehlt
            logIgnoredRegistration(classname, ex)
        }
    }

    private fun logIgnoredRegistration(fachwertClass: Any, ex: Throwable) {
        log.log(Level.FINE, "Registrierung von $fachwertClass wird ignoriert.")
        log.log(Level.FINER, "Details:", ex)
    }

    /**
     * Liefert die registrierten Klassen zurueck.
     *
     * @return registrierte Klassen
     */
    fun getRegisteredClasses(): Map<String, Class<out KFachwert>> {
        return registeredClasses
    }

    /**
     * Liefert einen Fachwert zum angegebenen (Klassen-)Namen. Als Name wird
     * der Klassennamen erwartet. Wird keine Klasse gefunden, wird die Klasse
     * genommen, die am ehesten passt. So wird bei "IBAN1" als Name eine
     * Instanz der IBAN-Klasse zurueckgeliefert.
     *
     * Anmerkung: Die Aehnlichkeit des uebergebenen Namens mit dem
     * tatsaechlichen Namen wird anhand der Levenshtein-Distanz bestimmt.
     * Ist die Differenz zu groß, wird als Fallback die [Text]-Klasse
     * verwendet.
     *
     * @param name Namen der Fachwert-Klasse, z.B. "IBAN"
     * @param args Argument(e) fuer den Konstruktor der Fachwert-Klasse
     * @return ein Fachwert
     */
    fun getFachwert(name: String, vararg args: Serializable): KFachwert {
        val fachwertClass = getClassFor(name)
        return getFachwert(fachwertClass, *args)
    }

    /**
     * Liefert einen Fachwert zur angegebenen Klasse.
     *
     * @param clazz Fachwert-Klasse
     * @param args Argument(e) fuer den Konstruktor der Fachwert-Klasse
     * @return ein Fachwert
     */
    fun getFachwert(clazz: Class<out KFachwert>, vararg args: Serializable): KFachwert {
        val argTypes = toTypes(args)
        return try {
            val ctor = clazz.getConstructor(*argTypes)
            ctor.newInstance(*args)
        } catch (ex: ReflectiveOperationException) {
            val cause = ex.cause
            if (cause is ValidationException) {
                throw (cause as ValidationException?)!!
            } else if (cause is IllegalArgumentException) {
                throw LocalizedValidationException(cause.message, cause)
            } else {
                throw IllegalArgumentException("cannot create " + clazz + " with " + Arrays.toString(args), ex)
            }
        }
    }

    /**
     * Validiert die uebergebenen Argumente mit Hilfe der angegebenen Klasse,
     * die als (Klassen-)Namen angegeben wird. Viele Fachwert-Klassen haben
     * eine (statische) validate-Methode, die dafuer verwendet wird. Fehlt
     * diese validate-Methode, wird der Konstruktor fuer die Validierung
     * herangezogen. Schlaegt die Validierung fehl, wird eine
     * Schlaegt die Validierung fehl, wird eine
     * [ValidationException] geworfen.
     *
     * Wenn es den uebergebenen (Klassen-)Namen nicht gibt, wird mithilfe der
     * Levenshtein-Distanz die aehnlichste Klasse genommen. Ist die Differenz
     * zu groß, wird als Fallback die [Text]-Klasse verwendet.
     *
     * @param name Namen der Fachwert-Klasse, z.B. "IBAN"
     * @param args Argument(e), die validiert werden
     */
    fun validate(name: String, vararg args: Serializable) {
        val fachwertClass = getClassFor(name)
        validate(fachwertClass, *args)
    }

    /**
     * Validiert die uebergebenen Argumente mit Hilfe der angegebenen Klasse.
     * Viele Fachwert-Klassen haben eine (statische) validate-Methode, die
     * dafuer verwendet wird. Fehlt diese validate-Methode, wird der
     * Konstruktor fuer die Validierung herangezogen. Schlaegt die Validierung
     * fehl, wird eine [ValidationException] geworfen.
     *
     * Dies ist eine der wenigen Stelle, wo eine
     * Log-Ausgabe erscheinen kann. Hintergrund ist die Exception, die hier
     * gefangen, aber nicht weitergegeben wird. Im Log-Level "FINE" kann man
     * sich diese Exception zur Fehlersuche ausgeben.
     *
     * @param clazz Fachwert-Klasse
     * @param args Argument(e), die validiert werden
     */
    fun validate(clazz: Class<out KFachwert>, vararg args: Serializable) {
        val validator = getValidator(clazz)
        if (validator.isPresent) {
            validator.get().validateObject(args[0])
        } else {
            callValidate(clazz, args as Array<Serializable>)
        }
    }

    private fun getClassFor(name: String): Class<out KFachwert> {
        var fachwertClass = registeredClasses[name]
        if (fachwertClass == null) {
            fachwertClass = registeredClasses[getSimilarName(name)]
        }
        return fachwertClass!!
    }

    private fun getSimilarName(name: String): String {
        var similarName = "?"
        var minDistance = Int.MAX_VALUE
        for (registeredName in registeredClasses.keys) {
            val dist = distance(name, registeredName)
            if (dist < minDistance) {
                similarName = registeredName
                minDistance = dist
            }
        }
        if (minDistance > 2) {
            if (log.isLoggable(Level.FINE)) {
                log.fine("Nearest name for '" + name + "' is '" + similarName + "' and too far away (" + minDistance +
                        ") - will use Text class as fallback.")
            }
            similarName = Text::class.java.simpleName
        }
        return similarName
    }

}
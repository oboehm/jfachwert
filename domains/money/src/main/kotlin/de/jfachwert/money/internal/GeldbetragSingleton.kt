/*
 * Copyright (c) 2018-2020 by Oliver Boehm
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
 * (c)reated 03.08.2018 by oboehm (ob@oasd.de)
 */
package de.jfachwert.money.internal

import de.jfachwert.money.Geldbetrag
import de.jfachwert.money.GeldbetragFactory
import java.util.concurrent.ConcurrentHashMap
import javax.money.MonetaryAmount
import javax.money.MonetaryAmountFactory
import javax.money.MonetaryException
import javax.money.spi.Bootstrap
import javax.money.spi.MonetaryAmountFactoryProviderSpi
import javax.money.spi.MonetaryAmountsSingletonSpi

/**
 * Eine Implementierung fuer [MonetaryAmountsSingletonSpi], die dafuer
 * sorgt, dass der [de.jfachwert.money.Geldbetrag] als Implementierung
 * gesehen wird. Als Vorlage fuer die Implementierung diente die
 * DefaultMonetaryAmountsSingletonSpi aus der Referenzimplementierung, die
 * auf den [Bootstrap]-Mechanismus des JSRs aufsetzt.
 *
 * Diese Klasse war notwendig, um die TCK-Suite zu JSR 354, die aus
 * GeldbetragIT aufgerufen wird, zu bestehen.
 */
class GeldbetragSingleton : MonetaryAmountsSingletonSpi {

    private val factories: MutableMap<Class<out MonetaryAmount>, MonetaryAmountFactoryProviderSpi<*>> = ConcurrentHashMap()

    override fun <T : MonetaryAmount?> getAmountFactory(amountType: Class<T>): MonetaryAmountFactory<T> {
        if (Geldbetrag::class.java == amountType) {
            return GeldbetragFactory() as MonetaryAmountFactory<T>
        }
        val f = factories[amountType] as MonetaryAmountFactoryProviderSpi<T>?
        if (f != null) {
            val monetaryAmountFactory = f.createMonetaryAmountFactory()
            monetaryAmountFactory.setContext(monetaryAmountFactory.maximalMonetaryContext)
            return monetaryAmountFactory
        }
        throw MonetaryException("no MonetaryAmountFactory found for $amountType")
    }

    /**
     * Liefert den [Geldbetrag] als Default-Implemntierung fuer
     * [MonetaryAmount].
     *
     * @return Geldbetrag-Klasse
     * @see MonetaryAmountsSingletonSpi.getDefaultAmountType
     */
    override fun getDefaultAmountType(): Class<out MonetaryAmount> {
        return Geldbetrag::class.java
    }

    /**
     * Liefert eine Liste der registrierten [MonetaryAmount]-Klassen,
     * in der auch die [Geldbetrag]-Klasse enthalten ist.
     *
     * @return Liste der verfuegbaren [MonetaryAmount]-Klassen
     */
    override fun getAmountTypes(): Set<Class<out MonetaryAmount>> {
        return factories.keys
    }

    init {
        for (f in Bootstrap.getServices(MonetaryAmountFactoryProviderSpi::class.java)) {
            factories.putIfAbsent(f.amountType, f)
        }
        factories[Geldbetrag::class.java] = GeldbetragFactoryProvider()
    }

}

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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express orimplied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * (c)reated 02.09.2019 by oboehm (ob@jfachwert.de)
 */
package de.jfachwert.money.internal

import de.jfachwert.money.Geldbetrag
import de.jfachwert.money.GeldbetragFactory
import javax.money.MonetaryAmountFactory
import javax.money.MonetaryContext
import javax.money.spi.MonetaryAmountFactoryProviderSpi

/**
 * Klasse GeldbetragFactoryProviderSpi.
 *
 * @author oboehm
 * @since 1.0 (02.09.2019)
 */
class GeldbetragFactoryProvider : MonetaryAmountFactoryProviderSpi<Geldbetrag> {

    /**
     * Liefert [Geldbetrag] als Geldtyp.
     *
     * @return Geldbetrag-Klasse
     */
    override fun getAmountType(): Class<Geldbetrag> {
        return Geldbetrag::class.java
    }

    /**
     * Liefert eine [GeldbetragFactory].
     *
     * @return  [GeldbetragFactory] als [MonetaryAmountFactory].
     */
    override fun createMonetaryAmountFactory(): MonetaryAmountFactory<Geldbetrag> {
        return GeldbetragFactory()
    }

    /**
     * Liederf den [MonetaryContext], wenn kein [MonetaryContext]
     * angegeben ist.
     *
     * @return den Default-[MonetaryContext], nie `null`.
     * @see .getMaximalMonetaryContext
     */
    override fun getDefaultMonetaryContext(): MonetaryContext {
        return GeldbetragFactory.MAX_CONTEXT
    }

}
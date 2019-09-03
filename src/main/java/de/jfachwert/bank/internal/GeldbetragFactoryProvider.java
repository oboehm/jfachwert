/*
 * Copyright (c) 2019 by Oliver Boehm
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
package de.jfachwert.bank.internal;

import de.jfachwert.bank.Geldbetrag;
import de.jfachwert.bank.GeldbetragFactory;

import javax.money.MonetaryAmountFactory;
import javax.money.MonetaryContext;
import javax.money.spi.MonetaryAmountFactoryProviderSpi;

/**
 * Klasse GeldbetragFactoryProviderSpi.
 *
 * @author oboehm
 * @since x.x (02.09.2019)
 */
public class GeldbetragFactoryProvider implements MonetaryAmountFactoryProviderSpi<Geldbetrag> {

    /**
     * Liefert {@link Geldbetrag} als Geldtyp.
     *
     * @return Geldbetrag-Klasse
     */
    @Override
    public Class<Geldbetrag> getAmountType() {
        return Geldbetrag.class;
    }

    /**
     * Liefert eine {@link GeldbetragFactory}.
     *
     * @return  {@link GeldbetragFactory} als {@link MonetaryAmountFactory}.
     */
    @Override
    public MonetaryAmountFactory<Geldbetrag> createMonetaryAmountFactory() {
        return new GeldbetragFactory();
    }

    /**
     * Liederf den {@link MonetaryContext}, wenn kein {@link MonetaryContext}
     * angegeben ist.
     *
     * @return den Default-{@link MonetaryContext}, nie {@code null}.
     * @see #getMaximalMonetaryContext()
     */
    @Override
    public MonetaryContext getDefaultMonetaryContext() {
        return GeldbetragFactory.MAX_CONTEXT;
    }

}

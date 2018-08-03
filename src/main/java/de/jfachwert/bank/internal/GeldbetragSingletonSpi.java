/*
 * Copyright (c) 2018 by Oliver Boehm
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
package de.jfachwert.bank.internal;

import de.jfachwert.bank.Geldbetrag;
import de.jfachwert.bank.GeldbetragFactory;

import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.MonetaryAmountFactory;
import javax.money.MonetaryException;
import javax.money.spi.Bootstrap;
import javax.money.spi.MonetaryAmountFactoryProviderSpi;
import javax.money.spi.MonetaryAmountsSingletonSpi;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Eine Implementierung fuer {@link MonetaryAmountsSingletonSpi}, die dafuer
 * sorgt, dass der {@link de.jfachwert.bank.Geldbetrag} als Implementierung
 * gesehen wird. Als Vorlage fuer die Implementierung diente die 
 * DefaultMonetaryAmountsSingletonSpi aus der Referenzimplementierung, die
 * auf den {@link Bootstrap}-Mechanismus des JSRs aufsetzt.
 * <p>
 * Diese Klasse war notwendig, um die TCK-Suite zu JSR 354, die aus
 * GeldbetragIT aufgerufen wird, zu bestehen.
 * </p>
 */
public class GeldbetragSingletonSpi implements MonetaryAmountsSingletonSpi {

    private final Map<Class<? extends MonetaryAmount>, MonetaryAmountFactoryProviderSpi<?>> factories =
            new ConcurrentHashMap<>();

    public GeldbetragSingletonSpi() {
        for (MonetaryAmountFactoryProviderSpi<?> f : Bootstrap.getServices(MonetaryAmountFactoryProviderSpi.class)) {
            factories.putIfAbsent(f.getAmountType(), f);
        }
    }

    // save cast, since members are managed by this instance
    @SuppressWarnings("unchecked")
    @Override
    public <T extends MonetaryAmount> MonetaryAmountFactory<T> getAmountFactory(Class<T> amountType) {
        if (Geldbetrag.class.equals(amountType)) {
            return (MonetaryAmountFactory<T>) new GeldbetragFactory();
        }
        MonetaryAmountFactoryProviderSpi<T> f = MonetaryAmountFactoryProviderSpi.class.cast(factories.get(amountType));
        if (Objects.nonNull(f)) {
            return f.createMonetaryAmountFactory();
        }
        throw new MonetaryException("no MonetaryAmountFactory found for " + amountType);
    }

    /**
     * Liefert den {@link Geldbetrag} als Default-Implemntierung fuer 
     * {@link MonetaryAmount}.
     *
     * @return Geldbetrag-Klasse
     * @see Monetary#getDefaultAmountType()
     */
    @Override
    public Class<? extends MonetaryAmount> getDefaultAmountType() {
        return Geldbetrag.class;
    }

    /**
     * Liefert eine Liste der registrierten {@link MonetaryAmount}-Klassen,
     * in der auch die {@link Geldbetrag}-Klasse enthalten ist.
     * 
     * @return Liste der verfuegbaren {@link MonetaryAmount}-Klassen
     */
    @Override
    public Set<Class<? extends MonetaryAmount>> getAmountTypes() {
        Set<Class<? extends MonetaryAmount>> amountTypes = new HashSet<>(factories.keySet());
        amountTypes.add(Geldbetrag.class);
        return amountTypes;
    }

}

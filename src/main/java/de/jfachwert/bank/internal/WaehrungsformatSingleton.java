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
 * (c)reated 13.08.2019 by oboehm (ob@jfachwert.de)
 */
package de.jfachwert.bank.internal;

import de.jfachwert.bank.GeldbetragFormatter;

import javax.money.MonetaryAmountFactory;
import javax.money.format.AmountFormatContext;
import javax.money.format.AmountFormatContextBuilder;
import javax.money.format.AmountFormatQuery;
import javax.money.format.MonetaryAmountFormat;
import javax.money.spi.Bootstrap;
import javax.money.spi.MonetaryAmountFormatProviderSpi;
import javax.money.spi.MonetaryFormatsSingletonSpi;
import java.util.*;

/**
 * Die Klasse WaehrungsformatSingletonSpi wird benoetigt, um die entsprechenden
 * Waehrungsformate zu erzeugen und das TCK unter Java 11 zu bestehen.
 *
 * @author oboehm
 * @since 2.4 (13.08.2019)
 */
public class WaehrungsformatSingleton implements MonetaryFormatsSingletonSpi {

    private static final Collection<MonetaryFormatsSingletonSpi> MONETARY_FORMATS_SINGLETON_SPIS = new ArrayList<>();

    static {
        for (MonetaryFormatsSingletonSpi spi : Bootstrap.getServices(MonetaryFormatsSingletonSpi.class)) {
            if (!(spi instanceof WaehrungsformatSingleton)) {
                MONETARY_FORMATS_SINGLETON_SPIS.add(spi);
            }
        }
    }

    /**
     * Ermittelt alle verfuegbaren Loacales.
     *
     * @param providers zusaetzliche Provider (werden aber ignoriert)
     * @return verfuegbare Locales, nie {@code null}.
     */
    @Override
    public Set<Locale> getAvailableLocales(String... providers) {
        return WaehrungsformatProvider.INSTANCE.getAvailableLocales();
    }

    /**
     * Ermittelt alle Formate, die auf die uebergebene Query uebereinstimmen.
     *
     * @param formatQuery The format query defining the requirements of the formatter.
     * @return the corresponding {@link MonetaryAmountFormat} instances, never null
     */
    @Override
    public Collection<MonetaryAmountFormat> getAmountFormats(AmountFormatQuery formatQuery) {
        Collection<MonetaryAmountFormat> result = new ArrayList<>();
        Locale locale = formatQuery.getLocale();
        MonetaryAmountFactory amountFactory = formatQuery.getMonetaryAmountFactory();
        String formatName = formatQuery.getFormatName();
        AmountFormatContext context = AmountFormatContextBuilder.of(formatName)
                .setMonetaryAmountFactory(amountFactory)
                .setLocale(locale == null ? Locale.getDefault() : locale)
                .build();
        result.add(GeldbetragFormatter.of(context));
        for (MonetaryAmountFormatProviderSpi spi : Bootstrap.getServices(MonetaryAmountFormatProviderSpi.class)) {
            result.addAll(spi.getAmountFormats(formatQuery));
        }
        return result;
    }

    /**
     * Ermittelt die Namen der aktuell registrierten Format-Provider.
     *
     * @return Providernamen
     */
    @Override
    public Set<String> getProviderNames() {
        Set<String> providerNames = new HashSet<>();
        for (MonetaryFormatsSingletonSpi spi : MONETARY_FORMATS_SINGLETON_SPIS) {
            providerNames.addAll(spi.getProviderNames());
        }
        return providerNames;
    }

    /**
     * Liefert die Default-Provider-Kette
     *
     * @return Default-Provider-Kette.
     */
    @Override
    public List<String> getDefaultProviderChain() {
        List<String> defaultProviderChain = new ArrayList<>();
        for (MonetaryFormatsSingletonSpi spi : MONETARY_FORMATS_SINGLETON_SPIS) {
            defaultProviderChain.addAll(spi.getDefaultProviderChain());
        }
        return defaultProviderChain;
    }

}

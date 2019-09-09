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
 * (c)reated 27.08.2019 by oboehm (ob@jfachwert.de)
 */
package de.jfachwert.bank.internal;

import de.jfachwert.bank.GeldbetragFactory;
import de.jfachwert.bank.GeldbetragFormatter;

import javax.money.format.AmountFormatContext;
import javax.money.format.AmountFormatContextBuilder;
import javax.money.format.AmountFormatQuery;
import javax.money.format.MonetaryAmountFormat;
import javax.money.spi.Bootstrap;
import javax.money.spi.MonetaryAmountFormatProviderSpi;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Klasse WaehrungsformatProviderSpi.
 *
 * @author oboehm
 * @since 3.0 (27.08.2019)
 */
public class WaehrungsformatProviderSpi implements MonetaryAmountFormatProviderSpi  {

    static final WaehrungsformatProviderSpi INSTANCE = new WaehrungsformatProviderSpi();

    private final Set<Locale> availableLocales;
    private final Set<String> availableFormatNames = Collections.unmodifiableSet(Collections.singleton("jfachwert"));

    public WaehrungsformatProviderSpi() {
        Set<Locale> locales = new HashSet<>();
        locales.add(Locale.GERMANY);
        locales.addAll(Arrays.asList(DecimalFormat.getAvailableLocales()));
        availableLocales = Collections.unmodifiableSet(locales);
    }

    /**
     * Liefert eine Liste mit dem {@link GeldbetragFormatter} zurueck.
     *
     * @param formatQuery der {@link AmountFormatContext}, der verwendet werden
     *                    soll
     * @return Liste mit {@link GeldbetragFormatter} (oder leere Liste)
     */
    @Override
    public Collection<MonetaryAmountFormat> getAmountFormats(AmountFormatQuery formatQuery) {
        Collection<MonetaryAmountFormat> amountFormats = new ArrayList<>();
        if (formatQuery.getProviderNames().contains(getProviderName()) || (formatQuery
                .getMonetaryAmountFactory() instanceof GeldbetragFactory)) {
            Locale locale = formatQuery.getLocale();
            AmountFormatContextBuilder builder = AmountFormatContextBuilder.of("jfachwert");
            builder.setLocale(locale == null ? Locale.getDefault() : locale);
            builder.importContext(formatQuery, false);
            builder.setMonetaryAmountFactory(formatQuery.getMonetaryAmountFactory());
            amountFormats.add(GeldbetragFormatter.of(builder.build()));
        } else {
            for (MonetaryAmountFormatProviderSpi spi : Bootstrap.getServices(MonetaryAmountFormatProviderSpi.class)) {
                if (!(spi instanceof WaehrungsformatProviderSpi)) {
                    amountFormats.addAll(spi.getAmountFormats(formatQuery));
                }
            }
        }
        return amountFormats;
    }

    /**
     * Liefert eine Liste der unterstuetzten Locales.
     *
     * @return verfuegbare Locales, nie {@code null}.
     */
    @Override
    public Set<Locale> getAvailableLocales() {
        return this.availableLocales;
    }

    /**
     * Als Formatname wird lediglich eine Liste mit "jfachwert" zurueckgegeben.
     *
     * @return Set mit "jfachwert"
     */
    @Override
    public Set<String> getAvailableFormatNames() {
        return this.availableFormatNames;
    }

    /**
     * Access the provider's name.
     *
     * @return this provider's name, not null.
     */
    @Override
    public String getProviderName() {
        return "jfachwert";
    }

}

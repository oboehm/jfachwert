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
import de.jfachwert.math.PackedDecimal;

import javax.money.format.AmountFormatQuery;
import javax.money.format.MonetaryAmountFormat;
import javax.money.spi.Bootstrap;
import javax.money.spi.MonetaryAmountFormatProviderSpi;
import javax.money.spi.MonetaryFormatsSingletonSpi;
import java.text.DecimalFormat;
import java.util.*;
import java.util.logging.Logger;

/**
 * Die Klasse WaehrungsformatSingletonSpi wird benoetigt, um die entsprechenden
 * Waehrungsformate zu erzeugen und das TCK unter Java 11 zu bestehen.
 *
 * @author oboehm
 * @since 2.4 (13.08.2019)
 */
public class WaehrungsformatSingletonSpi implements MonetaryFormatsSingletonSpi {

    /**
     * Ermittelt alle verfuegbaren Loacales.
     *
     * @param providers zusaetzliche Provider (werden aber ignoriert)
     * @return verfuegbare Locales, nie {@code null}.
     */
    @Override
    public Set<Locale> getAvailableLocales(String... providers) {
        Set<Locale>  availableLocales = new HashSet<>();
        availableLocales.addAll(Arrays.asList(DecimalFormat.getAvailableLocales()));
        return availableLocales;
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
        result.add(new GeldbetragFormatter(formatQuery.getLocale()));
        for (MonetaryAmountFormatProviderSpi spi : Bootstrap.getServices(MonetaryAmountFormatProviderSpi.class)) {
            result.addAll(spi.getAmountFormats(formatQuery));
        }
        return result;
    }

    /**
     * Get the names of the currently registered format providers.
     *
     * @return the provider names, never null.
     */
    @Override
    public Set<String> getProviderNames() {
        throw new UnsupportedOperationException("not yet implemented");
    }

    /**
     * Get the default provider chain, identified by the unique provider names in order as evaluated and used.
     *
     * @return the default provider chain, never null.
     */
    @Override
    public List<String> getDefaultProviderChain() {
        throw new UnsupportedOperationException("not yet implemented");
    }
}
